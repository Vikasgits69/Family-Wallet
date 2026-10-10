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

private val EmeraldVaultColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFF6EE7B7),
    secondary = Color(0xFFF59E0B),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF022C22),
    background = Color(0xFF041712),
    onBackground = Color(0xFFECFDF5),
    surface = Color(0xFF08271E),
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF0F3E31),
    onSurfaceVariant = Color(0xFFA7F3D0),
    outline = Color(0x5510B981),
    outlineVariant = Color(0x2210B981)
)

private val MidnightRoseColorScheme = darkColorScheme(
    primary = Color(0xFFF43F5E),
    onPrimary = Color(0xFF4C0519),
    primaryContainer = Color(0xFF881337),
    onPrimaryContainer = Color(0xFFFFD1DC),
    secondary = Color(0xFFA855F7),
    onSecondary = Color(0xFF3B0764),
    secondaryContainer = Color(0xFF581C87),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = Color(0xFFFB7185),
    onTertiary = Color(0xFF4C0519),
    background = Color(0xFF0F0713),
    onBackground = Color(0xFFFFF1F2),
    surface = Color(0xFF190D20),
    onSurface = Color(0xFFFFF1F2),
    surfaceVariant = Color(0xFF2B1437),
    onSurfaceVariant = Color(0xFFFBCFE8),
    outline = Color(0x55F43F5E),
    outlineVariant = Color(0x22F43F5E)
)

private val PlatinumLuxuryColorScheme = darkColorScheme(
    primary = Color(0xFFCBD5E1),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF334155),
    onPrimaryContainer = Color(0xFFF8FAFC),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFFE2E8F0),
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFF0B0D10),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF14171C),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF20252D),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0x6664748B),
    outlineVariant = Color(0x2294A3B8)
)

private val CyberNeonColorScheme = darkColorScheme(
    primary = Color(0xFF00F5FF),
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFF80DEEA),
    secondary = Color(0xFFD946EF),
    onSecondary = Color(0xFF4A044E),
    secondaryContainer = Color(0xFF701A75),
    onSecondaryContainer = Color(0xFFF5D0FE),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF002244),
    background = Color(0xFF060814),
    onBackground = Color(0xFFE0F7FA),
    surface = Color(0xFF0B0F24),
    onSurface = Color(0xFFE0F7FA),
    surfaceVariant = Color(0xFF131A3E),
    onSurfaceVariant = Color(0xFF80DEEA),
    outline = Color(0x7700F5FF),
    outlineVariant = Color(0x33D946EF)
)

private val NordicFrostColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBAE6FD),
    onSecondaryContainer = Color(0xFF075985),
    tertiary = Color(0xFF64748B),
    onTertiary = Color.White,
    background = Color(0xFFF0F4F8),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

private val SunsetAmberColorScheme = lightColorScheme(
    primary = Color(0xFFEA580C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF9A3412),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    background = Color(0xFFFAF5EE),
    onBackground = Color(0xFF292524),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF292524),
    surfaceVariant = Color(0xFFF5EBE1),
    onSurfaceVariant = Color(0xFF57534E),
    outline = Color(0xFFE7DFD5),
    outlineVariant = Color(0xFFF0EAE1)
)

private val MatchaSageColorScheme = lightColorScheme(
    primary = Color(0xFF4D7C0F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECFCCB),
    onPrimaryContainer = Color(0xFF365314),
    secondary = Color(0xFF65A30D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9F99D),
    onSecondaryContainer = Color(0xFF1A2E05),
    tertiary = Color(0xFF3F6212),
    onTertiary = Color.White,
    background = Color(0xFFF7F8F2),
    onBackground = Color(0xFF1C1D18),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1D18),
    surfaceVariant = Color(0xFFEBF0E1),
    onSurfaceVariant = Color(0xFF4B553D),
    outline = Color(0xFFDDE5D3),
    outlineVariant = Color(0xFFE5EDE0)
)

private val SwissGoldColorScheme = darkColorScheme(
    primary = Color(0xFFE5B94E), // 24k Polished Swiss Gold
    onPrimary = Color(0xFF1C1605),
    primaryContainer = Color(0xFF3B2F0B),
    onPrimaryContainer = Color(0xFFFDE68A),
    secondary = Color(0xFFD4AF37), // Metallic Gold
    onSecondary = Color(0xFF1C1605),
    secondaryContainer = Color(0xFF261E0A),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color(0xFF2E1A02),
    background = Color(0xFF0A0A0A), // Onyx Black
    onBackground = Color(0xFFFAF7EE),
    surface = Color(0xFF121212), // Deep Obsidian
    onSurface = Color(0xFFFAF7EE),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFD4AF37),
    outline = Color(0x66D4AF37),
    outlineVariant = Color(0x33D4AF37)
)

private val WarmEspressoColorScheme = lightColorScheme(
    primary = Color(0xFF6F4E37), // Rich Roasted Espresso
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFE8E1), // Warm Oat Milk Foam
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFF8D6E63), // Roasted Hazelnut
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7CCC8),
    onSecondaryContainer = Color(0xFF2D1B17),
    tertiary = Color(0xFFA1887F),
    onTertiary = Color.White,
    background = Color(0xFFFBF8F5), // Soft Cream Canvas
    onBackground = Color(0xFF2A1F1D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2A1F1D),
    surfaceVariant = Color(0xFFF3EDE7),
    onSurfaceVariant = Color(0xFF5D4037),
    outline = Color(0xFFD7CCC8),
    outlineVariant = Color(0xFFEFEBE9)
)

private val SakuraBlossomColorScheme = lightColorScheme(
    primary = Color(0xFFE11D74), // Vivid Cherry Blossom Pink
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE4EC), // Soft Sakura Blush
    onPrimaryContainer = Color(0xFF700B33),
    secondary = Color(0xFF9D174D), // Deep Plum
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFF500720),
    tertiary = Color(0xFFF472B6),
    onTertiary = Color.White,
    background = Color(0xFFFFF7F9), // Pearl Sakura Background
    onBackground = Color(0xFF280B16),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF280B16),
    surfaceVariant = Color(0xFFFDE8EF),
    onSurfaceVariant = Color(0xFF6B213D),
    outline = Color(0xFFFBCFE8),
    outlineVariant = Color(0xFFFDF2F8)
)

private val RetroTerminalColorScheme = darkColorScheme(
    primary = Color(0xFF22C55E), // CRT Green Phosphor
    onPrimary = Color(0xFF021B0B),
    primaryContainer = Color(0xFF052E16),
    onPrimaryContainer = Color(0xFF86EFAC),
    secondary = Color(0xFFF59E0B), // CRT Amber Secondary
    onSecondary = Color(0xFF261502),
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFF022C22),
    background = Color(0xFF030A05), // Phosphor CRT Black
    onBackground = Color(0xFFDCFCE7),
    surface = Color(0xFF06140A),
    onSurface = Color(0xFFDCFCE7),
    surfaceVariant = Color(0xFF0B2312),
    onSurfaceVariant = Color(0xFF4ADE80),
    outline = Color(0x6622C55E),
    outlineVariant = Color(0x3322C55E)
)

fun parseHexColor(hexString: String?): Color? {
    if (hexString.isNullOrBlank()) return null
    return try {
        val clean = hexString.trim().removePrefix("#")
        val colorLong = when (clean.length) {
            6 -> ("FF" + clean).toLong(16)
            8 -> clean.toLong(16)
            3 -> {
                val r = clean[0]
                val g = clean[1]
                val b = clean[2]
                ("FF$r$r$g$g$b$b").toLong(16)
            }
            else -> return null
        }
        Color(colorLong)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun FamilyWalletTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    customAccentHex: String? = null,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val baseColorScheme = when (themeMode) {
        AppThemeMode.DOODLE -> DoodleColorScheme
        AppThemeMode.DOODLE_DARK -> DoodleDarkColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.PITCH_BLACK -> PitchBlackColorScheme
        AppThemeMode.HIGH_CONTRAST -> HighContrastColorScheme
        AppThemeMode.PAPERLIKE -> PaperlikeColorScheme
        AppThemeMode.EMERALD_VAULT -> EmeraldVaultColorScheme
        AppThemeMode.MIDNIGHT_ROSE -> MidnightRoseColorScheme
        AppThemeMode.PLATINUM_LUXURY -> PlatinumLuxuryColorScheme
        AppThemeMode.SWISS_GOLD -> SwissGoldColorScheme
        AppThemeMode.WARM_ESPRESSO -> WarmEspressoColorScheme
        AppThemeMode.SAKURA_BLOSSOM -> SakuraBlossomColorScheme
        AppThemeMode.RETRO_TERMINAL -> RetroTerminalColorScheme
        AppThemeMode.CYBER_NEON -> CyberNeonColorScheme
        AppThemeMode.NORDIC_FROST -> NordicFrostColorScheme
        AppThemeMode.SUNSET_AMBER -> SunsetAmberColorScheme
        AppThemeMode.MATCHA_SAGE -> MatchaSageColorScheme
        AppThemeMode.SYSTEM -> if (systemInDark) DarkColorScheme else LightColorScheme
    }

    val customAccentColor = parseHexColor(customAccentHex)
    val targetColorScheme = if (customAccentColor != null) {
        val lum = customAccentColor.red * 0.299f + customAccentColor.green * 0.587f + customAccentColor.blue * 0.114f
        val onPrimaryColor = if (lum > 0.65f) Color.Black else Color.White
        val isDark = baseColorScheme.background.red * 0.299f + baseColorScheme.background.green * 0.587f + baseColorScheme.background.blue * 0.114f < 0.5f
        val container = if (isDark) customAccentColor.copy(alpha = 0.25f) else customAccentColor.copy(alpha = 0.15f)
        val onContainer = if (isDark) Color.White else customAccentColor
        baseColorScheme.copy(
            primary = customAccentColor,
            onPrimary = onPrimaryColor,
            primaryContainer = container,
            onPrimaryContainer = onContainer
        )
    } else {
        baseColorScheme
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
