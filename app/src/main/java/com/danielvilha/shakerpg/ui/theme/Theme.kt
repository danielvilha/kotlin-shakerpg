package com.danielvilha.shakerpg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RpgDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1E1400),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = ArcanePurple,
    onSecondary = Color.White,
    secondaryContainer = SurfaceCardElevated,
    onSecondaryContainer = ArcanePurpleLight,
    tertiary = CritSuccessGreen,
    onTertiary = Color.Black,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = CritFailRed,
    onError = Color.White
)

@Composable
fun ShakeRPGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally use the rich RPG dark palette for maximum tabletop immersion
    MaterialTheme(
        colorScheme = RpgDarkColorScheme,
        typography = Typography,
        content = content
    )
}
