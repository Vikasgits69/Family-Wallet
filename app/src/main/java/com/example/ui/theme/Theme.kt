package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = IndigoAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E284A),
    onPrimaryContainer = Color(0xFFD6DEFF),
    secondary = CyanAccent,
    onSecondary = Color(0xFF06283D),
    secondaryContainer = Color(0xFF163853),
    onSecondaryContainer = Color(0xFFD0F0FD),
    tertiary = EmeraldMint,
    onTertiary = Color(0xFF03281E),
    background = DarkBackgroundStart,
    onBackground = TextPrimaryDark,
    surface = DarkBackgroundEnd,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkGlassBorder,
    outlineVariant = Color(0x1A94A3B8)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF082F49),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = LightBackgroundStart,
    onBackground = TextPrimaryLight,
    surface = LightSurfaceElevated,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = LightGlassBorder,
    outlineVariant = Color(0x1A0F172A)
)

private val PitchBlackColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0F172A),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = EmeraldMint,
    onTertiary = Color.Black,
    background = PitchBlackBackgroundStart,
    onBackground = Color.White,
    surface = PitchBlackSurface,
    onSurface = Color.White,
    surfaceVariant = PitchBlackSurfaceElevated,
    onSurfaceVariant = Color(0xFFA3A3A3),
    outline = PitchBlackGlassBorder,
    outlineVariant = Color(0xFF1F1F1F)
)

private val PaperlikeColorScheme = lightColorScheme(
    primary = PaperlikeAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDE68A),
    onPrimaryContainer = Color(0xFF451A03),
    secondary = PaperlikeSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    background = PaperlikeBackgroundStart,
    onBackground = TextPrimaryPaperlike,
    surface = PaperlikeSurface,
    onSurface = TextPrimaryPaperlike,
    surfaceVariant = PaperlikeSurfaceElevated,
    onSurfaceVariant = TextSecondaryPaperlike,
    outline = PaperlikeGlassBorder,
    outlineVariant = Color(0x33B45309)
)

@Composable
fun FamilyWalletTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    darkTheme: Boolean = true, // for backwards-compatibility
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.PITCH_BLACK -> PitchBlackColorScheme
        AppThemeMode.PAPERLIKE -> PaperlikeColorScheme
        AppThemeMode.SYSTEM -> if (systemInDark) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
