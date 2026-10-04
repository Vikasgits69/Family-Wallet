package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Premium Card Style Data Class
 * Encapsulates gradient background brushes, high-contrast typography colors,
 * metallic accents, and styling properties for bank-branded credit/debit cards.
 */
data class CardStyle(
    val backgroundBrush: Brush,
    val textColor: Color = Color.White,
    val textSecondaryColor: Color = Color.White.copy(alpha = 0.75f),
    val accentColor: Color = Color(0xFFD4AF37), // Metallic Gold default
    val chipColor: Color = Color(0xFFD4AF37),
    val chipBorderColor: Color = Color(0xFF8A6827),
    val borderColor: Color = Color.White.copy(alpha = 0.25f),
    val bankDisplayName: String = ""
)

/**
 * Factory function: getCardStyle(bankName: String)
 * Dynamically maps Indian banks to their hyper-premium credit card aesthetics.
 */
fun getCardStyle(bankName: String): CardStyle {
    val cleanName = bankName.trim()

    return when {
        // 1. HDFC Bank (Infinia / Regalia: Deep Royal Obsidian Navy & Crimson Accent)
        cleanName.contains("HDFC", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF070D1E), // Deep Void Navy
                        Color(0xFF0F1E3D), // Royal HDFC Navy
                        Color(0xFF081226)  // Midnight Onyx
                    )
                ),
                textColor = Color(0xFFF8FAFC),
                textSecondaryColor = Color(0xFF94A3B8),
                accentColor = Color(0xFFED232A), // HDFC Dynamic Red
                chipColor = Color(0xFFE2B755),
                chipBorderColor = Color(0xFF9A7822),
                borderColor = Color(0xFF2563EB).copy(alpha = 0.35f),
                bankDisplayName = "HDFC BANK"
            )
        }

        // 2. State Bank of India (SBI: Aurum / Elite Dark Charcoal & Imperial Gold)
        cleanName.contains("SBI", ignoreCase = true) ||
        cleanName.contains("State Bank", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Deep Slate Midnight
                        Color(0xFF1E293B), // Dark Titanium
                        Color(0xFF0B192C)  // SBI Deep Blue-Black
                    )
                ),
                textColor = Color(0xFFF8FAFC),
                textSecondaryColor = Color(0xFFCBD5E1),
                accentColor = Color(0xFFF59E0B), // Aurum Imperial Gold
                chipColor = Color(0xFFFCD34D),
                chipBorderColor = Color(0xFFB45309),
                borderColor = Color(0xFF38BDF8).copy(alpha = 0.3f),
                bankDisplayName = "STATE BANK OF INDIA"
            )
        }

        // 3. ICICI Bank (Emeralde / Sapphiro Deep Sapphire Navy & Emerald Sheen)
        cleanName.contains("ICICI", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF031926), // Deep Oceanic Navy
                        Color(0xFF0B3C49), // Emeralde Dark Teal
                        Color(0xFF051923)  // Midnight Sapphire
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFA5F3FC),
                accentColor = Color(0xFFF97316), // ICICI Signature Warm Orange
                chipColor = Color(0xFFE5C07B),
                chipBorderColor = Color(0xFF96732B),
                borderColor = Color(0xFF06B6D4).copy(alpha = 0.35f),
                bankDisplayName = "ICICI BANK"
            )
        }

        // 4. Axis Bank (Magnus / Atlas Deep Matte Burgundy & Rose Gold)
        cleanName.contains("Axis", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2B0711), // Deepest Burgundy
                        Color(0xFF4C0E1E), // Axis Signature Velvet Burgundy
                        Color(0xFF190308)  // Obsidian Noir
                    )
                ),
                textColor = Color(0xFFFFF1F2),
                textSecondaryColor = Color(0xFFFECDD3),
                accentColor = Color(0xFFFB7185), // Rose Gold / Burgundy Glow
                chipColor = Color(0xFFFBCFE8),
                chipBorderColor = Color(0xFF9D174D),
                borderColor = Color(0xFFE11D48).copy(alpha = 0.35f),
                bankDisplayName = "AXIS BANK"
            )
        }

        // 5. Kotak Mahindra Bank (Kotak White / Dark Solitaire Black & Ruby Red)
        cleanName.contains("Kotak", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0C0E14), // Pure Onyx
                        Color(0xFF1E222D), // Dark Graphite
                        Color(0xFF08090D)  // Deep Coal
                    )
                ),
                textColor = Color(0xFFF8FAFC),
                textSecondaryColor = Color(0xFFE2E8F0),
                accentColor = Color(0xFFEF4444), // Kotak Ruby Crimson
                chipColor = Color(0xFFD4AF37),
                chipBorderColor = Color(0xFF854D0E),
                borderColor = Color(0xFFEF4444).copy(alpha = 0.4f),
                bankDisplayName = "KOTAK MAHINDRA BANK"
            )
        }

        // 6. Bank of Baroda (Eterna / Premier Dark Slate & Baroda Sun Orange)
        cleanName.contains("Baroda", ignoreCase = true) ||
        cleanName.contains("BOB", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF111827), // Dark Gunmetal
                        Color(0xFF1F2937), // Matte Slate
                        Color(0xFF2E1B10)  // Baroda Sun Warm Undertone
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFFED7AA),
                accentColor = Color(0xFFF97316), // Vermillion Orange
                chipColor = Color(0xFFFDBA74),
                chipBorderColor = Color(0xFFC2410C),
                borderColor = Color(0xFFF97316).copy(alpha = 0.35f),
                bankDisplayName = "BANK OF BARODA"
            )
        }

        // 7. IndusInd Bank (Pinnacle / Pioneer Heritage Metal Royal Burgundy & Ochre)
        cleanName.contains("IndusInd", ignoreCase = true) ||
        cleanName.contains("Indus", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF22030B), // Deep Wine
                        Color(0xFF380715), // IndusInd Signature Burgundy
                        Color(0xFF140106)  // Midnight Onyx
                    )
                ),
                textColor = Color(0xFFFFFBEB),
                textSecondaryColor = Color(0xFFFDE68A),
                accentColor = Color(0xFFEAB308), // Yellow Ochre / Imperial Gold
                chipColor = Color(0xFFFCD34D),
                chipBorderColor = Color(0xFF854D0E),
                borderColor = Color(0xFFCA8A04).copy(alpha = 0.4f),
                bankDisplayName = "INDUSIND BANK"
            )
        }

        // 8. IDFC First Bank (FIRST Wealth / FIRST Private Charcoal Navy & Maroon)
        cleanName.contains("IDFC", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0B0F19), // Midnight Charcoal
                        Color(0xFF151C2C), // Deep Tech Navy
                        Color(0xFF260D15)  // IDFC Deep Maroon Edge
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFCBD5E1),
                accentColor = Color(0xFF9E1B32), // IDFC Core Maroon
                chipColor = Color(0xFFE2E8F0),
                chipBorderColor = Color(0xFF64748B),
                borderColor = Color(0xFF9E1B32).copy(alpha = 0.45f),
                bankDisplayName = "IDFC FIRST BANK"
            )
        }

        // 9. Yes Bank (YES Marquee / Reserv Deep Electric Navy & Crimson Check)
        cleanName.contains("Yes", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF061124), // Void Navy
                        Color(0xFF0D2552), // Electric Royal Blue
                        Color(0xFF040A17)  // Deep Midnight
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFBAE6FD),
                accentColor = Color(0xFFE11D48), // Yes Bank Red Check
                chipColor = Color(0xFF38BDF8),
                chipBorderColor = Color(0xFF0284C7),
                borderColor = Color(0xFF0284C7).copy(alpha = 0.4f),
                bankDisplayName = "YES BANK"
            )
        }

        // 10. Punjab National Bank (PNB LUXURA Metal Obsidian & Mustard Gold)
        cleanName.contains("PNB", ignoreCase = true) ||
        cleanName.contains("Punjab National", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF14120E), // Obsidian Dark Earth
                        Color(0xFF241F16), // Deep Bronze Charcoal
                        Color(0xFF0F0E0B)  // Coal Black
                    )
                ),
                textColor = Color(0xFFFFFBEB),
                textSecondaryColor = Color(0xFFFDE68A),
                accentColor = Color(0xFFF59E0B), // PNB Mustard Gold
                chipColor = Color(0xFFFBBF24),
                chipBorderColor = Color(0xFF92400E),
                borderColor = Color(0xFFD97706).copy(alpha = 0.35f),
                bankDisplayName = "PUNJAB NATIONAL BANK"
            )
        }

        // 11. RBL Bank (Icon / Insignia Gunmetal Grey & Lust Red)
        cleanName.contains("RBL", ignoreCase = true) ||
        cleanName.contains("Ratnakar", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F141C), // Deep Gunmetal
                        Color(0xFF1E2638), // Bay Of Many Deep Blue
                        Color(0xFF0B0E14)  // Dark Titanium
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFCBD5E1),
                accentColor = Color(0xFFDC2626), // RBL Lust Red
                chipColor = Color(0xFFE2E8F0),
                chipBorderColor = Color(0xFF475569),
                borderColor = Color(0xFFDC2626).copy(alpha = 0.35f),
                bankDisplayName = "RBL BANK"
            )
        }

        // 12. AU Small Finance Bank (Zenith / Vetta Imperial Purple & Gold)
        cleanName.contains("AU", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF19092B), // Deep Velvet Violet
                        Color(0xFF2F114D), // Royal AU Purple
                        Color(0xFF10051C)  // Midnight Onyx
                    )
                ),
                textColor = Color(0xFFFAF5FF),
                textSecondaryColor = Color(0xFFE9D5FF),
                accentColor = Color(0xFFFBBF24), // AU Trust Gold
                chipColor = Color(0xFFFDE047),
                chipBorderColor = Color(0xFFA16207),
                borderColor = Color(0xFFA855F7).copy(alpha = 0.4f),
                bankDisplayName = "AU SMALL FINANCE BANK"
            )
        }

        // 13. Federal Bank (Celesta / Scapia Deep Midnight Obsidian & Copper Sunset)
        cleanName.contains("Federal", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0B1326), // Federal Deep Navy
                        Color(0xFF152244), // Corporate Blue
                        Color(0xFF2B170E)  // Copper Warm Undertone
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFFFEDD5),
                accentColor = Color(0xFFF97316), // Energetic Federal Orange
                chipColor = Color(0xFFFB923C),
                chipBorderColor = Color(0xFF9A3412),
                borderColor = Color(0xFFF97316).copy(alpha = 0.35f),
                bankDisplayName = "FEDERAL BANK"
            )
        }

        // 14. Standard Chartered (SC Ultimate Luxurious Matte Black & Emerald Sheen)
        cleanName.contains("Standard Chartered", ignoreCase = true) ||
        cleanName.contains("StanChart", ignoreCase = true) ||
        cleanName.contains("SCB", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF090E17), // Matte Void Black
                        Color(0xFF11222C), // SC Emerald Teal-Navy
                        Color(0xFF070B12)  // Deep Coal
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFA7F3D0),
                accentColor = Color(0xFF10B981), // Standard Chartered Green
                chipColor = Color(0xFFFCD34D),
                chipBorderColor = Color(0xFF047857),
                borderColor = Color(0xFF10B981).copy(alpha = 0.4f),
                bankDisplayName = "STANDARD CHARTERED"
            )
        }

        // 15. HSBC (Premier Mastercard Carbon Black & Crimson Hexagon)
        cleanName.contains("HSBC", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF111215), // Carbon Black
                        Color(0xFF1F2229), // Metallic Slate Grey
                        Color(0xFF0D0E11)  // Obsidian Noir
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFE2E8F0),
                accentColor = Color(0xFFDB0011), // HSBC Iconic Crimson Red
                chipColor = Color(0xFFCBD5E1),
                chipBorderColor = Color(0xFF475569),
                borderColor = Color(0xFFDB0011).copy(alpha = 0.4f),
                bankDisplayName = "HSBC"
            )
        }

        // 16. American Express (Amex Centurion / Platinum Heavy Brushed Titanium)
        cleanName.contains("Amex", ignoreCase = true) ||
        cleanName.contains("American Express", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1A1F2C), // Brushed Titanium Blue-Grey
                        Color(0xFF2A3447), // Amex Steel Platinum
                        Color(0xFF10141E)  // Dark Onyx
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFF93C5FD),
                accentColor = Color(0xFF006FCF), // Amex Signature Blue
                chipColor = Color(0xFFE2E8F0),
                chipBorderColor = Color(0xFF0284C7),
                borderColor = Color(0xFF60A5FA).copy(alpha = 0.45f),
                bankDisplayName = "AMERICAN EXPRESS"
            )
        }

        // 17. Union Bank of India (French Navy & Lebanese Red)
        cleanName.contains("Union", ignoreCase = true) ||
        cleanName.contains("UBI", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A1424), // Deep French Navy
                        Color(0xFF162544), // Institutional Blue
                        Color(0xFF2B0A11)  // Lebanese Ruby Edge
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFE2E8F0),
                accentColor = Color(0xFFE11D48), // Lebanese Red
                chipColor = Color(0xFFF59E0B),
                chipBorderColor = Color(0xFF881337),
                borderColor = Color(0xFF3B82F6).copy(alpha = 0.35f),
                bankDisplayName = "UNION BANK OF INDIA"
            )
        }

        // 18. Canara Bank (Deep Sky Midnight Navy & Mikado Yellow)
        cleanName.contains("Canara", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF06182E), // Deep Sky Navy
                        Color(0xFF0C2E59), // Canara Bright Navy
                        Color(0xFF04101F)  // Midnight Abyss
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFFEF08A),
                accentColor = Color(0xFFFACC15), // Mikado Warm Yellow
                chipColor = Color(0xFFFDE047),
                chipBorderColor = Color(0xFFA16207),
                borderColor = Color(0xFF38BDF8).copy(alpha = 0.35f),
                bankDisplayName = "CANARA BANK"
            )
        }

        // 19. Bank of India (BOI Dark Star Navy & Flame Orange)
        cleanName.contains("Bank of India", ignoreCase = true) ||
        cleanName.contains("BOI", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF081426), // BOI Star Midnight
                        Color(0xFF10284D), // Deep Navy
                        Color(0xFF26150B)  // Star Flame Orange Glow
                    )
                ),
                textColor = Color(0xFFFFFFFF),
                textSecondaryColor = Color(0xFFFFEDD5),
                accentColor = Color(0xFFF97316), // BOI Star Orange
                chipColor = Color(0xFFFDBA74),
                chipBorderColor = Color(0xFFC2410C),
                borderColor = Color(0xFFF97316).copy(alpha = 0.35f),
                bankDisplayName = "BANK OF INDIA"
            )
        }

        // 20. South Indian Bank (Deep Ruby-Wine & Royal Night Blue)
        cleanName.contains("South Indian", ignoreCase = true) ||
        cleanName.contains("SIB", ignoreCase = true) -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1C0818), // Deep Ruby Wine
                        Color(0xFF2B0F2A), // Imperial Mulberry
                        Color(0xFF0C1326)  // Royal Night Blue
                    )
                ),
                textColor = Color(0xFFFFFBEB),
                textSecondaryColor = Color(0xFFFCE7F3),
                accentColor = Color(0xFFF43F5E), // SIB Vibrant Red
                chipColor = Color(0xFFFCD34D),
                chipBorderColor = Color(0xFF9F1239),
                borderColor = Color(0xFFFB7185).copy(alpha = 0.35f),
                bankDisplayName = "SOUTH INDIAN BANK"
            )
        }

        // Fallback / Unknown Bank (Ultra-Sleek Titanium & Matte Black Metal)
        else -> {
            CardStyle(
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF11141A), // Deep Obsidian Charcoal
                        Color(0xFF212733), // Brushed Metallic Slate
                        Color(0xFF0D0F14)  // Pitch Onyx
                    )
                ),
                textColor = Color(0xFFF8FAFC),
                textSecondaryColor = Color(0xFF94A3B8),
                accentColor = Color(0xFFD4AF37), // Classic Platinum Gold
                chipColor = Color(0xFFE2E8F0),
                chipBorderColor = Color(0xFF64748B),
                borderColor = Color(0xFF94A3B8).copy(alpha = 0.3f),
                bankDisplayName = cleanName.ifBlank { "PREMIUM VAULT" }.uppercase()
            )
        }
    }
}
