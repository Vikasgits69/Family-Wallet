package com.example.data

enum class DocType(
    val title: String,
    val iconEmoji: String,
    val placeholder: String,
    val defaultColorHex: Long
) {
    AADHAAR("Aadhaar Card", "🆔", "e.g. 1234 5678 9012", 0xFFEA580C),
    PAN("PAN Card", "💳", "e.g. ABCDE1234F", 0xFF0284C7),
    PASSPORT("Passport", "🛂", "e.g. Z1234567", 0xFF1E3A8A),
    DRIVING_LICENSE("Driving Licence", "🪪", "e.g. MH02 20210012345", 0xFF7C3AED),
    VOTER_ID("Voter ID", "🗳️", "e.g. WXJ1234567", 0xFF059669),
    INSURANCE_POLICY("Insurance Policy", "🛡️", "e.g. POL-89012345", 0xFF0D9488),
    VEHICLE_RC("Vehicle RC", "🚗", "e.g. MH01 AB 1234", 0xFFD97706),
    PROPERTY("Property / Deed", "🏠", "e.g. DEED-4491-A", 0xFF4338CA),
    OTHER("Custom Document", "📄", "e.g. DOC-98765432", 0xFF475569)
}
