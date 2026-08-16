package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RexerDarkColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = Color.White,
    primaryContainer = NeonRedDark,
    onPrimaryContainer = Color.White,
    secondary = CyberCyan,
    onSecondary = CarbonBlack,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = CyberCyan,
    tertiary = ElectricAmber,
    onTertiary = CarbonBlack,
    background = CarbonBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // Ultra-modern high-tech dark theme as requested
    MaterialTheme(
        colorScheme = RexerDarkColorScheme,
        typography = Typography,
        content = content
    )
}
