package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AccessKeyEntity
import com.example.data.model.ActiveSignal
import com.example.data.model.BetHouse
import com.example.data.model.EbookEntity
import com.example.data.model.GalePhase
import com.example.data.model.GameOutcome
import com.example.data.model.ResolvedGame
import com.example.data.model.ScoreboardEntity
import com.example.data.model.SignalTarget
import com.example.data.model.StrategyEntity
import com.example.data.repository.BotRepository
import com.example.data.repository.KeyValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AppScreen {
    data object Login : AppScreen()
    data object BotHome : AppScreen()
    data object BetHouses : AppScreen()
    data object Ebooks : AppScreen()
    data object Bankroll : AppScreen()
    data object AdminPanel : AppScreen()
    data class WebViewBet(val betHouse: BetHouse) : AppScreen()
}

class BotViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BotRepository(database)
    private val prefs = application.getSharedPreferences("mauro_bot_prefs", Context.MODE_PRIVATE)

    // Navigation State
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Login)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _userKey = MutableStateFlow<String?>(null)
    val userKey: StateFlow<String?> = _userKey.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Game and Signal State
    private val _recentGames = MutableStateFlow<List<ResolvedGame>>(emptyList())
    val recentGames: StateFlow<List<ResolvedGame>> = _recentGames.asStateFlow()

    private val _activeSignal = MutableStateFlow<ActiveSignal?>(null)
    val activeSignal: StateFlow<ActiveSignal?> = _activeSignal.asStateFlow()

    private val _celebrationEvent = MutableSharedFlow<String>()
    val celebrationEvent: SharedFlow<String> = _celebrationEvent.asSharedFlow()

    private val _isApiConnecting = MutableStateFlow(true)
    val isApiConnecting: StateFlow<Boolean> = _isApiConnecting.asStateFlow()

    // Room DB Flows
    val strategies: StateFlow<List<StrategyEntity>> = repository.strategiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accessKeys: StateFlow<List<AccessKeyEntity>> = repository.accessKeysFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scoreboard: StateFlow<ScoreboardEntity> = repository.scoreboardFlow
        .map { it ?: ScoreboardEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScoreboardEntity())

    val ebooks: StateFlow<List<EbookEntity>> = repository.ebooksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Internal tracking for game resolution
    private var lastProcessedEventId: String? = null
    private var lastEvaluatedTriggerEventId: String? = null

    init {
        // Prepopulate database if needed
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.populateInitialData(database)
        }

        // Check if there was a saved session
        val savedKey = prefs.getString("saved_key", null)
        val savedIsAdmin = prefs.getBoolean("saved_is_admin", false)
        if (savedIsAdmin) {
            _isAdmin.value = true
            _isLoggedIn.value = true
            _currentScreen.value = AppScreen.BotHome
        } else if (!savedKey.isNullOrBlank()) {
            viewModelScope.launch {
                loginWithKey(savedKey, remember = true)
            }
        }

        // Start live polling of Bac Bo events
        startLivePolling()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun loginWithKey(keyInput: String, remember: Boolean = true) {
        viewModelScope.launch {
            _authError.value = null
            when (val result = repository.validateAccessKey(keyInput)) {
                is KeyValidationResult.Admin -> {
                    _isAdmin.value = true
                    _isLoggedIn.value = true
                    _userKey.value = "ADMINISTRADOR"
                    if (remember) {
                        prefs.edit().putBoolean("saved_is_admin", true).apply()
                    }
                    _currentScreen.value = AppScreen.BotHome
                    triggerHaptic(50)
                }
                is KeyValidationResult.Valid -> {
                    _isAdmin.value = false
                    _isLoggedIn.value = true
                    _userKey.value = result.key.key
                    if (remember) {
                        prefs.edit().putString("saved_key", result.key.key).apply()
                    }
                    _currentScreen.value = AppScreen.BotHome
                    triggerHaptic(50)
                }
                is KeyValidationResult.Expired -> {
                    _authError.value = result.message
                }
                is KeyValidationResult.Invalid -> {
                    _authError.value = result.message
                }
            }
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
        _isAdmin.value = false
        _userKey.value = null
        _currentScreen.value = AppScreen.Login
    }

    private fun startLivePolling() {
        viewModelScope.launch {
            repository.pollLiveGamesFlow().collect { games ->
                if (games.isNotEmpty()) {
                    _isApiConnecting.value = false
                    val previousLatest = _recentGames.value.firstOrNull()
                    _recentGames.value = games

                    val currentLatest = games.firstOrNull()
                    if (currentLatest != null && currentLatest.eventId != previousLatest?.eventId) {
                        // New settled game detected!
                        onNewGameSettled(currentLatest, games)
                    }
                }
            }
        }
    }

    private suspend fun onNewGameSettled(newGame: ResolvedGame, allRecent: List<ResolvedGame>) {
        val currentSignal = _activeSignal.value

        if (currentSignal != null) {
            when (currentSignal.phase) {
                GalePhase.ENTRY_CONFIRMED -> {
                    // This is the first attempt (Sem Gale - SG)
                    val isDirectWin = (currentSignal.target == SignalTarget.BANKER && newGame.outcome == GameOutcome.BANKER) ||
                            (currentSignal.target == SignalTarget.PLAYER && newGame.outcome == GameOutcome.PLAYER)
                    val isTie = newGame.outcome == GameOutcome.TIE

                    if (isDirectWin || isTie) {
                        // Won on Sem Gale or Won on Tie!
                        val message = if (isTie) "🎉 GREEN NO EMPATE! (PROTEÇÃO CONFIRMADA)" else "🎉 GREEN CONFIRMADO! (SEM GALE)"
                        _activeSignal.value = currentSignal.copy(
                            phase = GalePhase.RESULT_GREEN,
                            resultMessage = message,
                            isWin = true,
                            isTieWin = isTie
                        )
                        repository.registerSignalResult(isWin = true, isTie = isTie)
                        _celebrationEvent.emit(message)
                        triggerHaptic(100)
                        delay(4000)
                        _activeSignal.value = null
                        lastEvaluatedTriggerEventId = newGame.eventId
                    } else {
                        // Lost on Sem Gale -> Must go to GALE 1!
                        _activeSignal.value = currentSignal.copy(
                            phase = GalePhase.GALE_1,
                            sgEventId = newGame.eventId,
                            resultMessage = "⚠️ ATENÇÃO: APLICAR GALE 1 AGORA!"
                        )
                        triggerHaptic(80)
                    }
                }
                GalePhase.GALE_1 -> {
                    // This is the Gale 1 attempt
                    val isGaleWin = (currentSignal.target == SignalTarget.BANKER && newGame.outcome == GameOutcome.BANKER) ||
                            (currentSignal.target == SignalTarget.PLAYER && newGame.outcome == GameOutcome.PLAYER)
                    val isTie = newGame.outcome == GameOutcome.TIE

                    if (isGaleWin || isTie) {
                        // Won on Gale 1!
                        val message = if (isTie) "🎉 GREEN NO EMPATE NO GALE 1!" else "🎉 GREEN NO GALE 1! RECUPERAÇÃO COM LUCRO"
                        _activeSignal.value = currentSignal.copy(
                            phase = GalePhase.RESULT_GREEN,
                            g1EventId = newGame.eventId,
                            resultMessage = message,
                            isWin = true,
                            isTieWin = isTie
                        )
                        repository.registerSignalResult(isWin = true, isTie = isTie)
                        _celebrationEvent.emit(message)
                        triggerHaptic(100)
                    } else {
                        // Loss on Gale 1
                        val message = "❌ LOSS REGISTRADO. GESTÃO DE BANCA PRESERVADA"
                        _activeSignal.value = currentSignal.copy(
                            phase = GalePhase.RESULT_LOSS,
                            g1EventId = newGame.eventId,
                            resultMessage = message,
                            isWin = false
                        )
                        repository.registerSignalResult(isWin = false, isTie = false)
                    }
                    delay(4000)
                    _activeSignal.value = null
                    lastEvaluatedTriggerEventId = newGame.eventId
                }
                else -> {
                    // Signal already resolved, waiting to clear
                }
            }
        } else {
            // No active signal in progress. Check if strategies match!
            // Requirement: "se no painel administrativo não tiver nenhuma estratégia o bot só tem que ficar em Analisando o Jogo"
            val activeStrategiesList = strategies.value.filter { it.isActive }
            if (activeStrategiesList.isEmpty()) {
                // Strictly stay in Analyzing
                return
            }

            if (newGame.eventId != lastEvaluatedTriggerEventId) {
                val match = repository.evaluateStrategies(allRecent, activeStrategiesList)
                if (match != null) {
                    val (strat, target) = match
                    _activeSignal.value = ActiveSignal(
                        target = target,
                        phase = GalePhase.ENTRY_CONFIRMED,
                        strategyName = strat.name,
                        confidence = strat.confidence,
                        triggeredAfterEventId = newGame.eventId
                    )
                    lastEvaluatedTriggerEventId = newGame.eventId
                    triggerHaptic(120)
                }
            }
        }
    }

    // Admin Operations
    fun addStrategy(name: String, sequence: String, recommendation: String, confidence: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = StrategyEntity(
                name = name,
                sequence = sequence,
                recommendation = recommendation,
                confidence = confidence,
                isActive = true
            )
            repository.addStrategy(entity)
        }
    }

    fun toggleStrategy(strategy: StrategyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStrategy(strategy.copy(isActive = !strategy.isActive))
        }
    }

    fun deleteStrategy(strategy: StrategyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStrategy(strategy)
        }
    }

    fun generateAccessKey(customKey: String? = null, durationDays: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val code = if (!customKey.isNullOrBlank()) {
                customKey.trim().uppercase()
            } else {
                val randPart = (1000..9999).random()
                "MAURO-$randPart"
            }

            val entity = AccessKeyEntity(
                key = code,
                durationDays = durationDays,
                note = if (durationDays < 0) "Vitalício (Mais de 20 pessoas)" else "$durationDays dias de acesso"
            )
            repository.addAccessKey(entity)
        }
    }

    fun deleteAccessKey(key: AccessKeyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAccessKey(key)
        }
    }

    fun updateScoreboard(greens: Int, losses: Int, empates: Int, streak: Int, maxStreak: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateScoreboard(greens, losses, empates, streak, maxStreak)
        }
    }

    fun resetScoreboard() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetScoreboard()
        }
    }

    fun addEbook(title: String, category: String, readTime: String, summary: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEbook(
                EbookEntity(
                    title = title,
                    category = category,
                    readTime = readTime,
                    summary = summary,
                    content = content
                )
            )
        }
    }

    fun deleteEbook(ebook: EbookEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEbook(ebook)
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
