package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AccessKeyEntity
import com.example.data.model.ActiveSignal
import com.example.data.model.BacBoEventItem
import com.example.data.model.EbookEntity
import com.example.data.model.GalePhase
import com.example.data.model.GameOutcome
import com.example.data.model.ResolvedGame
import com.example.data.model.ScoreboardEntity
import com.example.data.model.SignalTarget
import com.example.data.model.StrategyEntity
import com.example.data.remote.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

sealed class KeyValidationResult {
    data object Admin : KeyValidationResult()
    data class Valid(val key: AccessKeyEntity) : KeyValidationResult()
    data class Expired(val message: String) : KeyValidationResult()
    data class Invalid(val message: String) : KeyValidationResult()
}

class BotRepository(private val db: AppDatabase) {
    private val api = ApiClient.apiService

    val strategiesFlow = db.strategyDao().getAllFlow()
    val accessKeysFlow = db.accessKeyDao().getAllFlow()
    val scoreboardFlow = db.scoreboardDao().getScoreboardFlow()
    val ebooksFlow = db.ebookDao().getAllFlow()

    suspend fun validateAccessKey(input: String): KeyValidationResult {
        val trimmed = input.trim()
        if (trimmed.equals("MD22", ignoreCase = true)) {
            return KeyValidationResult.Admin
        }

        val keyEntity = db.accessKeyDao().getByKey(trimmed)
            ?: return KeyValidationResult.Invalid("Chave de acesso não encontrada. Solicite ao administrador.")

        if (!keyEntity.isActive) {
            return KeyValidationResult.Expired("Esta chave foi desativada pelo administrador.")
        }

        if (keyEntity.isExpired()) {
            return KeyValidationResult.Expired("O prazo de validade desta chave expirou. Contate o suporte.")
        }

        return KeyValidationResult.Valid(keyEntity)
    }

    suspend fun addStrategy(strategy: StrategyEntity) = db.strategyDao().insert(strategy)
    suspend fun updateStrategy(strategy: StrategyEntity) = db.strategyDao().update(strategy)
    suspend fun deleteStrategy(strategy: StrategyEntity) = db.strategyDao().delete(strategy)
    suspend fun deleteStrategyById(id: String) = db.strategyDao().deleteById(id)

    suspend fun addAccessKey(key: AccessKeyEntity) = db.accessKeyDao().insert(key)
    suspend fun deleteAccessKey(key: AccessKeyEntity) = db.accessKeyDao().delete(key)
    suspend fun deleteAccessKeyById(id: String) = db.accessKeyDao().deleteById(id)

    suspend fun updateScoreboard(greens: Int, losses: Int, empates: Int, currentStreak: Int, maxStreak: Int) {
        db.scoreboardDao().updateScores(greens, losses, empates, currentStreak, maxStreak)
    }

    suspend fun resetScoreboard() {
        db.scoreboardDao().resetScore()
    }

    suspend fun addEbook(ebook: EbookEntity) = db.ebookDao().insert(ebook)
    suspend fun deleteEbook(ebook: EbookEntity) = db.ebookDao().delete(ebook)
    suspend fun deleteEbookById(id: String) = db.ebookDao().deleteById(id)

    suspend fun registerSignalResult(isWin: Boolean, isTie: Boolean) {
        val current = db.scoreboardDao().getScoreboard() ?: ScoreboardEntity()
        val newGreens = if (isWin) current.greens + 1 else current.greens
        val newLosses = if (!isWin) current.losses + 1 else current.losses
        val newEmpates = if (isTie) current.empates + 1 else current.empates
        val newStreak = if (isWin) current.currentStreak + 1 else 0
        val newMax = maxOf(current.maxConsecutiveGreens, newStreak)

        db.scoreboardDao().updateScores(
            greens = newGreens,
            losses = newLosses,
            empates = newEmpates,
            currentStreak = newStreak,
            maxStreak = newMax
        )
    }

    fun pollLiveGamesFlow(): Flow<List<ResolvedGame>> = flow {
        while (true) {
            try {
                val rawEvents = api.getRecentEvents(page = 0, size = 25)
                val resolved = parseEvents(rawEvents)
                emit(resolved)
            } catch (e: Exception) {
                // Emitting empty or keeping flow alive on network glitch
            }
            delay(1000) // Poll every 1 second without delay
        }
    }

    private fun parseEvents(events: List<BacBoEventItem>): List<ResolvedGame> {
        return events.mapNotNull { item ->
            val data = item.data ?: return@mapNotNull null
            val result = data.result ?: return@mapNotNull null
            val outcome = GameOutcome.fromApiOutcome(result.outcome)
            if (outcome == GameOutcome.UNKNOWN) return@mapNotNull null

            val pDice = result.playerDice
            val bDice = result.bankerDice

            ResolvedGame(
                eventId = item.id,
                settledAt = data.settledAt ?: "",
                outcome = outcome,
                playerScore = pDice?.score ?: ((pDice?.first ?: 0) + (pDice?.second ?: 0)),
                bankerScore = bDice?.score ?: ((bDice?.first ?: 0) + (bDice?.second ?: 0)),
                playerDice1 = pDice?.first ?: 0,
                playerDice2 = pDice?.second ?: 0,
                bankerDice1 = bDice?.first ?: 0,
                bankerDice2 = bDice?.second ?: 0
            )
        }
    }

    suspend fun evaluateStrategies(
        recentGames: List<ResolvedGame>,
        activeStrategies: List<StrategyEntity>
    ): Pair<StrategyEntity, SignalTarget>? {
        if (activeStrategies.isEmpty() || recentGames.size < 2) return null

        // Convert recent outcomes into simple code strings
        // recentGames is ordered descending (index 0 is latest settled round)
        // We evaluate patterns against the sequence of resolved rounds
        for (strategy in activeStrategies) {
            if (!strategy.isActive) continue

            val requiredCodes = strategy.sequence
                .split(",")
                .map { it.trim().uppercase() }
                .filter { it.isNotEmpty() }

            if (requiredCodes.isEmpty() || recentGames.size < requiredCodes.size) continue

            // Check if recentGames matches the trigger pattern
            // requiredCodes: e.g. ["B", "B"] (oldest -> newest of the trigger)
            // recentGames[0] is newest, recentGames[1] is older, etc.
            val subList = recentGames.take(requiredCodes.size).reversed()
            val codesFromGames = subList.map { it.outcome.code }

            var matches = true
            for (i in requiredCodes.indices) {
                val req = requiredCodes[i]
                val actual = codesFromGames[i]
                if (req != actual && req != "*") {
                    matches = false
                    break
                }
            }

            if (matches) {
                val target = if (strategy.recommendation.equals("BANKER", ignoreCase = true)) {
                    SignalTarget.BANKER
                } else {
                    SignalTarget.PLAYER
                }
                return Pair(strategy, target)
            }
        }
        return null
    }
}
