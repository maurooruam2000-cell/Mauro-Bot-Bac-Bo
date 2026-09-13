package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BotRedPrimary,
    onPrimary = Color.White,
    primaryContainer = BotRedDark,
    onPrimaryContainer = Color.White,
    secondary = TieGold,
    onSecondary = Color.Black,
    secondaryContainer = CasinoCard,
    onSecondaryContainer = TextPrimary,
    tertiary = GreenSuccess,
    onTertiary = Color.Black,
    background = CasinoBlack,
    onBackground = TextPrimary,
    surface = CasinoDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CasinoCard,
    onSurfaceVariant = TextSecondary,
    outline = CasinoCardBorder,
    error = LossRed,
    onError = Color.White
)

@Composable
fun MauroBotTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

