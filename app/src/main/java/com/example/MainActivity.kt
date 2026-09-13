package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.BankrollScreen
import com.example.ui.screens.BetHousesScreen
import com.example.ui.screens.BotHomeScreen
import com.example.ui.screens.EbooksScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.WebViewBetScreen
import com.example.ui.theme.CasinoBlack
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.MauroBotTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BotViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MauroBotTheme {
                MauroBotApp()
            }
        }
    }
}

@Composable
fun MauroBotApp(botViewModel: BotViewModel = viewModel()) {
    val currentScreen by botViewModel.currentScreen.collectAsState()
    val authError by botViewModel.authError.collectAsState()
    val activeSignal by botViewModel.activeSignal.collectAsState()
    val recentGames by botViewModel.recentGames.collectAsState()
    val scoreboard by botViewModel.scoreboard.collectAsState()
    val strategies by botViewModel.strategies.collectAsState()
    val accessKeys by botViewModel.accessKeys.collectAsState()
    val ebooks by botViewModel.ebooks.collectAsState()
    val isAdmin by botViewModel.isAdmin.collectAsState()
    val userKey by botViewModel.userKey.collectAsState()
    val isApiConnecting by botViewModel.isApiConnecting.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        botViewModel.celebrationEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Hardware / gesture back handling
    BackHandler(enabled = currentScreen !is AppScreen.Login && currentScreen !is AppScreen.BotHome) {
        botViewModel.navigateTo(AppScreen.BotHome)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CasinoBlack,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0C2417),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenSuccess),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = GreenSuccess,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Login -> {
                    LoginScreen(
                        authError = authError,
                        onLoginClick = { key -> botViewModel.loginWithKey(key) }
                    )
                }

                is AppScreen.BotHome -> {
                    BotHomeScreen(
                        activeSignal = activeSignal,
                        recentGames = recentGames,
                        scoreboard = scoreboard,
                        isAdmin = isAdmin,
                        userKey = userKey,
                        isApiConnecting = isApiConnecting,
                        onNavigateToBetHouses = { botViewModel.navigateTo(AppScreen.BetHouses) },
                        onNavigateToBankroll = { botViewModel.navigateTo(AppScreen.Bankroll) },
                        onNavigateToEbooks = { botViewModel.navigateTo(AppScreen.Ebooks) },
                        onNavigateToAdmin = { botViewModel.navigateTo(AppScreen.AdminPanel) },
                        onLogout = { botViewModel.logout() }
                    )
                }

                is AppScreen.BetHouses -> {
                    BetHousesScreen(
                        onBackClick = { botViewModel.navigateTo(AppScreen.BotHome) },
                        onSelectBetHouse = { house -> botViewModel.navigateTo(AppScreen.WebViewBet(house)) }
                    )
                }

                is AppScreen.Bankroll -> {
                    BankrollScreen(
                        onBackClick = { botViewModel.navigateTo(AppScreen.BotHome) }
                    )
                }

                is AppScreen.Ebooks -> {
                    EbooksScreen(
                        ebooks = ebooks,
                        onBackClick = { botViewModel.navigateTo(AppScreen.BotHome) }
                    )
                }

                is AppScreen.AdminPanel -> {
                    AdminPanelScreen(
                        strategies = strategies,
                        accessKeys = accessKeys,
                        scoreboard = scoreboard,
                        ebooks = ebooks,
                        onBackClick = { botViewModel.navigateTo(AppScreen.BotHome) },
                        onAddStrategy = { name, seq, rec, conf ->
                            botViewModel.addStrategy(name, seq, rec, conf)
                        },
                        onToggleStrategy = { strat -> botViewModel.toggleStrategy(strat) },
                        onDeleteStrategy = { strat -> botViewModel.deleteStrategy(strat) },
                        onGenerateKey = { key, days -> botViewModel.generateAccessKey(key, days) },
                        onDeleteKey = { key -> botViewModel.deleteAccessKey(key) },
                        onUpdateScoreboard = { g, l, e, s, m ->
                            botViewModel.updateScoreboard(g, l, e, s, m)
                        },
                        onResetScoreboard = { botViewModel.resetScoreboard() },
                        onAddEbook = { title, cat, time, sum, content ->
                            botViewModel.addEbook(title, cat, time, sum, content)
                        },
                        onDeleteEbook = { ebook -> botViewModel.deleteEbook(ebook) }
                    )
                }

                is AppScreen.WebViewBet -> {
                    WebViewBetScreen(
                        betHouse = screen.betHouse,
                        activeSignal = activeSignal,
                        onBackClick = { botViewModel.navigateTo(AppScreen.BetHouses) }
                    )
                }
            }
        }
    }
}
