package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PayPalBlueBright,
    onPrimary = Color.White,
    primaryContainer = PayPalDarkBlue,
    onPrimaryContainer = Color(0xFFCCE5FF),
    secondary = ElectricCyan,
    onSecondary = Color(0xFF003544),
    secondaryContainer = Color(0xFF004D63),
    onSecondaryContainer = Color(0xFFB8EAFF),
    tertiary = EmeraldSafe,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = EmeraldSafeBg,
    onTertiaryContainer = Color(0xFF8CF7C1),
    error = CrimsonScam,
    onError = Color(0xFF450A0A),
    errorContainer = CrimsonScamBg,
    onErrorContainer = Color(0xFFFECACA),
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightElevated,
    onSurfaceVariant = TextSecondary,
    outline = MidnightCardBorder,
    outlineVariant = Color(0xFF1E293B)
)

// PayWise AI is designed intentionally as a sleek dark-mode intelligent fintech experience
@Composable
fun PayWiseTheme(
    darkTheme: Boolean = true, // Default to sleek dark-mode
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
