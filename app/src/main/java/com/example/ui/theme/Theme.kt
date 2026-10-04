package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

// Strict High-Contrast Light Theme: Crisp White Background + Stark Black Text + Colorful Vivid Icons
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A73E8), // Vibrant Google Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F0FE),
    onPrimaryContainer = Color(0xFF174EA6),
    secondary = Color(0xFF059669), // Vivid Emerald Green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = Color(0xFFD97706), // Vivid Amber
    onTertiary = Color.White,
    background = Color(0xFFFFFFFF), // Crisp 100% White
    onBackground = Color(0xFF000000), // Stark 100% Black
    surface = Color(0xFFFFFFFF), // Crisp 100% White
    onSurface = Color(0xFF000000), // Stark 100% Black
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF111827),
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB)
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

private val DoodleColorScheme = lightColorScheme(
    primary = DoodlePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE3E3),
    onPrimaryContainer = Color(0xFF6E1A24),
    secondary = DoodleSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEBFF),
    onSecondaryContainer = Color(0xFF0F3970),
    tertiary = DoodleTertiary,
    onTertiary = Color.White,
    background = DoodleBackground,
    onBackground = DoodleInk,
    surface = DoodleSurface,
    onSurface = DoodleInk,
    surfaceVariant = DoodleSurfaceElevated,
    onSurfaceVariant = DoodleInkSecondary,
    outline = DoodleBorder,
    outlineVariant = Color(0x3322223B)
)

private val DoodleDarkColorScheme = darkColorScheme(
    primary = DoodleDarkPrimary,
    onPrimary = Color(0xFF2B0A11),
    primaryContainer = DoodleDarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFFFD9DF),
    secondary = DoodleDarkSecondary,
    onSecondary = Color(0xFF042033),
    secondaryContainer = DoodleDarkSecondaryContainer,
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = DoodleDarkTertiary,
    onTertiary = Color(0xFF052B14),
    background = DoodleDarkBackground,
    onBackground = DoodleDarkInk,
    surface = DoodleDarkSurface,
    onSurface = DoodleDarkInk,
    surfaceVariant = DoodleDarkSurfaceElevated,
    onSurfaceVariant = DoodleDarkInkSecondary,
    outline = DoodleDarkBorder,
    outlineVariant = Color(0x4464748B)
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

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF262600),
    onPrimaryContainer = HighContrastPrimary,
    secondary = HighContrastSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF002E2E),
    onSecondaryContainer = HighContrastSecondary,
    tertiary = HighContrastTertiary,
    onTertiary = Color.Black,
    background = HighContrastBackground,
    onBackground = HighContrastTextPrimary,
    surface = HighContrastSurface,
    onSurface = HighContrastTextPrimary,
    surfaceVariant = HighContrastSurfaceElevated,
    onSurfaceVariant = HighContrastTextSecondary,
    outline = HighContrastBorder,
    outlineVariant = Color(0xFFB3B3B3)
)

@Composable
fun FamilyWalletTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val targetColorScheme = when (themeMode) {
        AppThemeMode.DOODLE -> DoodleColorScheme
        AppThemeMode.DOODLE_DARK -> DoodleDarkColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.PITCH_BLACK -> PitchBlackColorScheme
        AppThemeMode.HIGH_CONTRAST -> HighContrastColorScheme
        AppThemeMode.PAPERLIKE -> PaperlikeColorScheme
        AppThemeMode.SYSTEM -> if (systemInDark) DoodleDarkColorScheme else DoodleColorScheme
    }

    // Smooth color transitions when theme is switched
    val animatedPrimary by animateColorAsState(targetValue = targetColorScheme.primary, animationSpec = tween(300), label = "primary")
    val animatedOnPrimary by animateColorAsState(targetValue = targetColorScheme.onPrimary, animationSpec = tween(300), label = "onPrimary")
    val animatedPrimaryContainer by animateColorAsState(targetValue = targetColorScheme.primaryContainer, animationSpec = tween(300), label = "primaryContainer")
    val animatedOnPrimaryContainer by animateColorAsState(targetValue = targetColorScheme.onPrimaryContainer, animationSpec = tween(300), label = "onPrimaryContainer")
    val animatedSecondary by animateColorAsState(targetValue = targetColorScheme.secondary, animationSpec = tween(300), label = "secondary")
    val animatedBackground by animateColorAsState(targetValue = targetColorScheme.background, animationSpec = tween(300), label = "background")
    val animatedOnBackground by animateColorAsState(targetValue = targetColorScheme.onBackground, animationSpec = tween(300), label = "onBackground")
    val animatedSurface by animateColorAsState(targetValue = targetColorScheme.surface, animationSpec = tween(300), label = "surface")
    val animatedOnSurface by animateColorAsState(targetValue = targetColorScheme.onSurface, animationSpec = tween(300), label = "onSurface")
    val animatedSurfaceVariant by animateColorAsState(targetValue = targetColorScheme.surfaceVariant, animationSpec = tween(300), label = "surfaceVariant")
    val animatedOnSurfaceVariant by animateColorAsState(targetValue = targetColorScheme.onSurfaceVariant, animationSpec = tween(300), label = "onSurfaceVariant")
    val animatedOutline by animateColorAsState(targetValue = targetColorScheme.outline, animationSpec = tween(300), label = "outline")
    val animatedOutlineVariant by animateColorAsState(targetValue = targetColorScheme.outlineVariant, animationSpec = tween(300), label = "outlineVariant")

    val animatedColorScheme = targetColorScheme.copy(
        primary = animatedPrimary,
        onPrimary = animatedOnPrimary,
        primaryContainer = animatedPrimaryContainer,
        onPrimaryContainer = animatedOnPrimaryContainer,
        secondary = animatedSecondary,
        background = animatedBackground,
        onBackground = animatedOnBackground,
        surface = animatedSurface,
        onSurface = animatedOnSurface,
        surfaceVariant = animatedSurfaceVariant,
        onSurfaceVariant = animatedOnSurfaceVariant,
        outline = animatedOutline,
        outlineVariant = animatedOutlineVariant
    )

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        content = content
    )
}
