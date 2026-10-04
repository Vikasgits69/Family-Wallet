package com.example.data

import com.example.data.local.BankAccountEntity
import com.example.data.local.CreditCardEntity
import com.example.data.local.DebitCardEntity
import com.example.data.local.FamilyMemberEntity
import com.example.data.local.WalletOrGiftCardEntity

enum class CardNetwork(val label: String) {
    RUPAY("RuPay"),
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express")
}

enum class CardStatus(val label: String) {
    ACTIVE("Active"),
    BLOCKED("Blocked"),
    EXPIRED("Expired"),
    INACTIVE("Inactive")
}

enum class DisplayMode {
    CAROUSEL,
    GRID,
    LIST
}

enum class NavigationTab(val title: String) {
    DASHBOARD("Dashboard"),
    CARDS("Cards"),
    ACCOUNTS("Accounts"),
    WALLETS("Wallets & Gifts"),
    MEMBERS("Family"),
    SETTINGS("Settings")
}

enum class AppThemeMode(val title: String, val subtitle: String, val emoji: String) {
    DOODLE("Doodle Light", "Hand-drawn playful notebook & sketch aesthetic", "🎨"),
    DOODLE_DARK("Doodle Dark", "Chalkboard & neon gel pens on slate sketch paper", "✏️"),
    DARK("Dark", "Obsidian midnight with tonal elevation", "🌙"),
    LIGHT("Light (High Contrast)", "Stark black text on crisp white", "☀️"),
    PITCH_BLACK("Pitch Black", "Pure AMOLED deep black", "🖤"),
    HIGH_CONTRAST("High Contrast", "Maximum accessibility & stark neon", "⚡"),
    PAPERLIKE("Paperlike", "Warm cream editorial", "📜"),
    SYSTEM("System", "Follow Android system mode", "⚙️")
}

enum class RelationshipCategory(val label: String) {
    SELF("Self"),
    SPOUSE("Spouse"),
    MOTHER("Mother"),
    FATHER("Father"),
    BROTHER("Brother"),
    SISTER("Sister"),
    OTHERS("Others")
}

data class FamilyMember(
    val id: String,
    val name: String,
    val relationship: String,
    val relationshipCategory: String = RelationshipCategory.OTHERS.name,
    val customRelationship: String = "",
    val profilePictureUri: String? = null,
    val colorHex: Long = 0xFF4F46E5,
    val initials: String = name.take(2).uppercase().ifBlank { "FM" }
)

data class CreditCard(
    val id: String,
    val bankName: String,
    val cardName: String,
    val network: CardNetwork,
    val cardNumber: String,
    val expiry: String, // MM/YY
    val cvv: String,
    val cardholderName: String,
    val issuanceDate: String = "",
    val ccRewardPoints: Long = 0L,
    val statementDate: String = "", // Bill Date e.g. "12th"
    val dueDate: String = "",       // Payment Due Date e.g. "2nd"
    val remindExpiry: Boolean = true,
    val remindBillDate: Boolean = true,
    val remindDueDate: Boolean = true,
    val status: CardStatus = CardStatus.ACTIVE,
    val memberId: String = "",
    val colorHex: Long = 0xFF1E293B,
    val linkedEmail: String = "",
    val linkedPhone: String = ""
)

data class DebitCard(
    val id: String,
    val bankName: String,
    val cardName: String,
    val network: CardNetwork,
    val cardNumber: String,
    val expiry: String, // MM/YY
    val cvv: String,
    val cardholderName: String,
    val issuanceDate: String = "",
    val rewardPoints: Long = 0L,
    val remindExpiry: Boolean = true,
    val status: CardStatus = CardStatus.ACTIVE,
    val memberId: String = "",
    val colorHex: Long = 0xFF0D5C46,
    val linkedEmail: String = "",
    val linkedPhone: String = ""
)

data class BankAccount(
    val id: String,
    val bankName: String,
    val accountType: String, // Savings, Current, Overdraft, Loan Account
    val accountNumber: String,
    val ifscCode: String,
    val micrCode: String = "",
    val accountHolderName: String,
    val branchName: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFF004C8F,
    val linkedEmail: String = "",
    val linkedPhone: String = ""
)

data class WalletOrGiftCard(
    val id: String,
    val isGiftCard: Boolean, // false for Online Wallet, true for Gift Card
    val providerOrName: String, // Provider for wallet (Paytm/GPay/PhonePe), Brand for Gift Card (Amazon/Apple)
    val cardNumberOrUpi: String, // UPI ID / Mobile for wallet, Voucher / PIN for Gift Card
    val amount: Double = 0.0,
    val expiryDate: String = "",
    val modeOfRedemption: String = "Online / App", // Online, In-Store, App, Voucher Code
    val remarks: String = "",
    val kycStatus: String = "Full KYC Verified",
    val registeredMobile: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFFB45309
)

// Extension mappers between Entities and Domain Models
fun FamilyMemberEntity.toDomain() = FamilyMember(
    id = id,
    name = name,
    relationship = relationship,
    relationshipCategory = relationshipCategory,
    customRelationship = customRelationship,
    profilePictureUri = profilePictureUri,
    colorHex = colorHex
)

fun FamilyMember.toEntity() = FamilyMemberEntity(
    id = id,
    name = name,
    relationship = relationship,
    relationshipCategory = relationshipCategory,
    customRelationship = customRelationship,
    profilePictureUri = profilePictureUri,
    colorHex = colorHex
)

fun CreditCardEntity.toDomain() = CreditCard(
    id = id,
    bankName = bankName,
    cardName = cardName,
    network = runCatching { CardNetwork.valueOf(network) }.getOrDefault(CardNetwork.VISA),
    cardNumber = cardNumber,
    expiry = expiry,
    cvv = cvv,
    cardholderName = cardholderName,
    issuanceDate = issuanceDate,
    ccRewardPoints = ccRewardPoints,
    statementDate = statementDate,
    dueDate = dueDate,
    remindExpiry = remindExpiry,
    remindBillDate = remindBillDate,
    remindDueDate = remindDueDate,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone
)

fun CreditCard.toEntity() = CreditCardEntity(
    id = id,
    bankName = bankName,
    cardName = cardName,
    network = network.name,
    cardNumber = cardNumber,
    expiry = expiry,
    cvv = cvv,
    cardholderName = cardholderName,
    issuanceDate = issuanceDate,
    ccRewardPoints = ccRewardPoints,
    statementDate = statementDate,
    dueDate = dueDate,
    remindExpiry = remindExpiry,
    remindBillDate = remindBillDate,
    remindDueDate = remindDueDate,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone
)

fun DebitCardEntity.toDomain() = DebitCard(
    id = id,
    bankName = bankName,
    cardName = cardName,
    network = runCatching { CardNetwork.valueOf(network) }.getOrDefault(CardNetwork.RUPAY),
    cardNumber = cardNumber,
    expiry = expiry,
    cvv = cvv,
    cardholderName = cardholderName,
    issuanceDate = issuanceDate,
    rewardPoints = rewardPoints,
    remindExpiry = remindExpiry,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone
)

fun DebitCard.toEntity() = DebitCardEntity(
    id = id,
    bankName = bankName,
    cardName = cardName,
    network = network.name,
    cardNumber = cardNumber,
    expiry = expiry,
    cvv = cvv,
    cardholderName = cardholderName,
    issuanceDate = issuanceDate,
    rewardPoints = rewardPoints,
    remindExpiry = remindExpiry,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone
)

fun BankAccountEntity.toDomain() = BankAccount(
    id = id,
    bankName = bankName,
    accountType = accountType,
    accountNumber = accountNumber,
    ifscCode = ifscCode,
    micrCode = micrCode,
    accountHolderName = accountHolderName,
    branchName = branchName,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone
)

fun BankAccount.toEntity() = BankAccountEntity(
    id = id,
    bankName = bankName,
    accountType = accountType,
    accountNumber = accountNumber,
    ifscCode = ifscCode,
    micrCode = micrCode,
    accountHolderName = accountHolderName,
    branchName = branchName,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone
)

fun WalletOrGiftCardEntity.toDomain() = WalletOrGiftCard(
    id = id,
    isGiftCard = isGiftCard,
    providerOrName = providerOrName,
    cardNumberOrUpi = cardNumberOrUpi,
    amount = amount,
    expiryDate = expiryDate,
    modeOfRedemption = modeOfRedemption,
    remarks = remarks,
    kycStatus = kycStatus,
    registeredMobile = registeredMobile,
    memberId = memberId,
    colorHex = colorHex
)

fun WalletOrGiftCard.toEntity() = WalletOrGiftCardEntity(
    id = id,
    isGiftCard = isGiftCard,
    providerOrName = providerOrName,
    cardNumberOrUpi = cardNumberOrUpi,
    amount = amount,
    expiryDate = expiryDate,
    modeOfRedemption = modeOfRedemption,
    remarks = remarks,
    kycStatus = kycStatus,
    registeredMobile = registeredMobile,
    memberId = memberId,
    colorHex = colorHex
)
