package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ActiveSignal
import com.example.data.model.GalePhase
import com.example.data.model.GameOutcome
import com.example.data.model.ResolvedGame
import com.example.data.model.ScoreboardEntity
import com.example.data.model.SignalTarget
import com.example.ui.theme.BankerRed
import com.example.ui.theme.BankerRedBg
import com.example.ui.theme.BotRedDark
import com.example.ui.theme.BotRedGlow
import com.example.ui.theme.BotRedPrimary
import com.example.ui.theme.CasinoBlack
import com.example.ui.theme.CasinoCard
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoDarkSurface
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LossRed
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.PlayerBlueBg
import com.example.ui.theme.TieGold
import com.example.ui.theme.TieGoldBg

@Composable
fun BotHomeScreen(
    activeSignal: ActiveSignal?,
    recentGames: List<ResolvedGame>,
    scoreboard: ScoreboardEntity,
    isAdmin: Boolean,
    userKey: String?,
    isApiConnecting: Boolean,
    onNavigateToBetHouses: () -> Unit,
    onNavigateToBankroll: () -> Unit,
    onNavigateToEbooks: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar"
    )

    val latestGame = recentGames.firstOrNull()

    // Determine current table theme color based on latest game outcome:
    // "Quando sair BANCKER tem que ficar Vermelho, se sair Player tem que ficar Azul"
    val outcomeColor by animateColorAsState(
        targetValue = when (latestGame?.outcome) {
            GameOutcome.BANKER -> BankerRed
            GameOutcome.PLAYER -> PlayerBlue
            GameOutcome.TIE -> TieGold
            else -> BotRedPrimary
        },
        label = "outcomeColor"
    )

    val outcomeBgColor by animateColorAsState(
        targetValue = when (latestGame?.outcome) {
            GameOutcome.BANKER -> BankerRedBg
            GameOutcome.PLAYER -> PlayerBlueBg
            GameOutcome.TIE -> TieGoldBg
            else -> CasinoDarkSurface
        },
        label = "outcomeBgColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // TOP APP BAR / BRANDING
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, BotRedPrimary, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bot_mascot),
                        contentDescription = "Mauro Bot Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "🤖 MAURO BOT",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isApiConnecting) TieGold else GreenSuccess)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isApiConnecting) "Conectando API (1s)..." else "BAC BO LIVE • 1s ONLINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isApiConnecting) TieGold else GreenSuccess
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isAdmin) {
                    IconButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF221115), RoundedCornerShape(8.dp))
                            .border(1.dp, BotRedPrimary, RoundedCornerShape(8.dp))
                            .testTag("admin_panel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Painel Admin",
                            tint = BotRedGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(38.dp)
                        .background(CasinoCard, RoundedCornerShape(8.dp))
                        .border(1.dp, CasinoCardBorder, RoundedCornerShape(8.dp))
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sair",
                        tint = Color(0xFFA0A5B5),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PLACAR DE GREENS, LOSS, EMPATE E SEQUÊNCIA (SCOREBOARD)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scoreboard_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoCard),
            border = BorderStroke(1.dp, CasinoCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 PLACAR AO VIVO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TieGold,
                        letterSpacing = 1.sp
                    )

                    // Assertiveness percentage
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF0C2417), RoundedCornerShape(20.dp))
                            .border(1.dp, GreenSuccess, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = GreenSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ASSERTIVIDADE ${String.format("%.1f", scoreboard.assertiveness)}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenSuccess
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // GREEN item
                    ScoreItem(
                        title = "GREEN",
                        count = scoreboard.greens,
                        color = GreenSuccess,
                        icon = "✅",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // LOSS item
                    ScoreItem(
                        title = "LOSS",
                        count = scoreboard.losses,
                        color = LossRed,
                        icon = "❌",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // EMPATE item
                    ScoreItem(
                        title = "EMPATE",
                        count = scoreboard.empates,
                        color = TieGold,
                        icon = "🟡",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // SEQUÊNCIA item
                    ScoreItem(
                        title = "SEGUIDOS",
                        count = scoreboard.currentStreak,
                        color = Color(0xFFFF9100),
                        icon = "🔥",
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // MAIN SIGNAL CARD (ROBÔ VERMELHO E PRETO COM ANIMAÇÃO)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("signal_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (activeSignal != null) Color(0xFF190D11) else CasinoDarkSurface
            ),
            border = BorderStroke(
                width = 2.dp,
                brush = if (activeSignal != null) {
                    Brush.sweepGradient(listOf(BotRedPrimary, BotRedGlow, TieGold, BotRedPrimary))
                } else {
                    Brush.linearGradient(listOf(CasinoCardBorder, Color(0xFF20232F)))
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (activeSignal == null) {
                    // "se no painel administrativo não tiver nenhuma estratégia o bot só tem que ficar em Analisando o Jogo"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = "Radar",
                                tint = BotRedPrimary,
                                modifier = Modifier
                                    .size(64.dp)
                                    .rotate(radarRotation)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "🔎 Analisando o Jogo...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Aguardando formação do padrão das estratégias...",
                            fontSize = 12.sp,
                            color = Color(0xFFA0A5B5),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1E1418), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "⚡ LEITURA DA API A CADA 1s SEM ATRASO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BotRedGlow
                            )
                        }
                    }
                } else {
                    // ACTIVE SIGNAL DISPLAY:
                    // "ENTRADA CONFIRMADA \n BANCKER + EMPATE \n ATÉ GALE 1."
                    val targetName = if (activeSignal.target == SignalTarget.BANKER) "BANKER" else "PLAYER"
                    val targetColor = if (activeSignal.target == SignalTarget.BANKER) BankerRed else PlayerBlue

                    // Pulsing Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .scale(pulseScale)
                            .background(
                                color = if (activeSignal.phase == GalePhase.GALE_1) Color(0xFF3B1E05) else Color(0xFF33090D),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.5.dp,
                                if (activeSignal.phase == GalePhase.GALE_1) Color(0xFFFF9800) else BotRedPrimary,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (activeSignal.phase == GalePhase.GALE_1) "⚠️ ATENÇÃO: APLICAR GALE 1" else "🚨 ENTRADA CONFIRMADA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activeSignal.phase == GalePhase.GALE_1) Color(0xFFFFB74D) else BotRedGlow,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // TARGET RECOMMENDATION
                    Text(
                        text = "$targetName + EMPATE",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = targetColor,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "ATÉ GALE 1.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Strategy Details Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoBadge(
                            label = "CONFIANÇA",
                            value = "${activeSignal.confidence}%",
                            color = GreenSuccess
                        )
                        InfoBadge(
                            label = "STATUS",
                            value = if (activeSignal.phase == GalePhase.GALE_1) "GALE 1 (G1)" else "SEM GALE (SG)",
                            color = if (activeSignal.phase == GalePhase.GALE_1) Color(0xFFFF9800) else TieGold
                        )
                        InfoBadge(
                            label = "PROTEÇÃO",
                            value = "EMPATE 88x",
                            color = TieGold
                        )
                    }

                    if (!activeSignal.resultMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = activeSignal.resultMessage,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (activeSignal.isWin == true) GreenSuccess else LossRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (activeSignal.isWin == true) Color(0xFF0B2E1C) else Color(0xFF330E14),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // RESULTADO DA MESA AO VIVO (LATEST ROUND BAC BO TABLE)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_table_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = outcomeBgColor),
            border = BorderStroke(1.5.dp, outcomeColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎲 RESULTADO DA MESA EVOLUTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0A5B5),
                        letterSpacing = 0.5.sp
                    )

                    latestGame?.let {
                        Text(
                            text = "VENCEDOR: ${it.outcome.displayName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = outcomeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (latestGame != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PLAYER SIDE (BLUE)
                        DiceBox(
                            sideName = "JOGADOR (PLAYER)",
                            dice1 = latestGame.playerDice1,
                            dice2 = latestGame.playerDice2,
                            score = latestGame.playerScore,
                            isWinner = latestGame.outcome == GameOutcome.PLAYER,
                            themeColor = PlayerBlue,
                            isTie = latestGame.outcome == GameOutcome.TIE
                        )

                        // VS / TIE CENTER
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "VS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            if (latestGame.outcome == GameOutcome.TIE) {
                                Text(
                                    text = "EMPATE!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TieGold
                                )
                            }
                        }

                        // BANKER SIDE (RED)
                        DiceBox(
                            sideName = "BANCA (BANKER)",
                            dice1 = latestGame.bankerDice1,
                            dice2 = latestGame.bankerDice2,
                            score = latestGame.bankerScore,
                            isWinner = latestGame.outcome == GameOutcome.BANKER,
                            themeColor = BankerRed,
                            isTie = latestGame.outcome == GameOutcome.TIE
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aguardando primeira rodada da API...",
                            color = Color(0xFFA0A5B5),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // HISTÓRICO DE RODADAS (BEAD ROAD CHIPS)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoCard),
            border = BorderStroke(1.dp, CasinoCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📊 HISTÓRICO DE RODADAS (ÚLTIMAS 20)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA0A5B5),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(recentGames) { game ->
                        OutcomeChip(outcome = game.outcome)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // QUICK ACTION TILES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavButton(
                title = "Casas de Apostas",
                subtitle = "Elephant, Kwanza, Bantu",
                icon = Icons.Default.Casino,
                accentColor = BotRedPrimary,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToBetHouses
            )

            QuickNavButton(
                title = "Gestão de Banca",
                subtitle = "Gale 1 & Stop Win",
                icon = Icons.Default.MonetizationOn,
                accentColor = GreenSuccess,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToBankroll
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavButton(
                title = "E-books Bac Bo",
                subtitle = "Estratégias & Leitura",
                icon = Icons.Default.MenuBook,
                accentColor = TieGold,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToEbooks
            )

            if (isAdmin) {
                QuickNavButton(
                    title = "Painel Admin",
                    subtitle = "Chaves & Estratégias",
                    icon = Icons.Default.AdminPanelSettings,
                    accentColor = Color(0xFFFF5252),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAdmin
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ScoreItem(
    title: String,
    count: Int,
    color: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF101217),
        border = BorderStroke(1.dp, Color(0xFF222634))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA0A5B5)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
fun InfoBadge(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8C92A4)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun DiceBox(
    sideName: String,
    dice1: Int,
    dice2: Int,
    score: Int,
    isWinner: Boolean,
    themeColor: Color,
    isTie: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = if (isWinner) themeColor.copy(alpha = 0.25f) else Color(0xFF13151D),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isWinner) 2.dp else 1.dp,
                color = if (isWinner) themeColor else Color(0xFF282C3D),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = sideName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isWinner) themeColor else Color(0xFFA0A5B5)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DiceSquare(number = dice1, color = themeColor)
            DiceSquare(number = dice2, color = themeColor)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Total: $score",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun DiceSquare(number: Int, color: Color) {
    Surface(
        modifier = Modifier.size(28.dp),
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF1A1D27),
        border = BorderStroke(1.dp, color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (number > 0) number.toString() else "-",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun OutcomeChip(outcome: GameOutcome) {
    val (code, color, bg) = when (outcome) {
        GameOutcome.BANKER -> Triple("B", BankerRed, BankerRedBg)
        GameOutcome.PLAYER -> Triple("P", PlayerBlue, PlayerBlueBg)
        GameOutcome.TIE -> Triple("T", TieGold, TieGoldBg)
        GameOutcome.UNKNOWN -> Triple("?", Color.Gray, Color.DarkGray)
    }

    Surface(
        modifier = Modifier.size(34.dp),
        shape = CircleShape,
        color = bg,
        border = BorderStroke(1.5.dp, color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = code,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
fun QuickNavButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoCard),
        border = BorderStroke(1.dp, CasinoCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF8C92A4),
                    maxLines = 1
                )
            }
        }
    }
}
