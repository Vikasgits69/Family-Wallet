package com.example.data

/**
 * Visual Density mode for spacious vs pro-compact layout density.
 */
enum class VisualDensityMode(
    val title: String,
    val subtitle: String,
    val cardCornerRadiusDp: Int,
    val contentPaddingDp: Int,
    val itemSpacingDp: Int
) {
    SPACIOUS(
        title = "Spacious Modern",
        subtitle = "Generous padding & 20dp smooth curves",
        cardCornerRadiusDp = 20,
        contentPaddingDp = 16,
        itemSpacingDp = 16
    ),
    COMPACT(
        title = "Compact Pro",
        subtitle = "Tighter padding & 12dp curves (more visible)",
        cardCornerRadiusDp = 12,
        contentPaddingDp = 12,
        itemSpacingDp = 10
    )
}

/**
 * Shaders & tactile surface textures for financial cards.
 */
enum class CardSurfaceShader(
    val title: String,
    val description: String,
    val emoji: String
) {
    CLASSIC_GRADIENT("Classic Radiant", "Smooth tonal depth gradient", "✨"),
    BRUSHED_TITANIUM("Brushed Titanium", "Horizontal metallic brushed sheen", "🪙"),
    HOLOGRAPHIC_FOIL("Holographic Foil", "Diagonal iridescent rainbow luster", "🌈"),
    FROSTED_GLASS("Frosted Glass", "Translucent glass with bright edge glow", "💎"),
    CARBON_FIBER("Carbon Fiber", "Tactical woven dark micro-mesh", "🛡️"),
    GUILLOCHE("Guilloché Security", "Intricate currency banknote security wave", "📜")
}
