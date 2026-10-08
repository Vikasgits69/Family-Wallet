package com.example.data

import com.example.data.local.BankAccountEntity
import com.example.data.local.CreditCardEntity
import com.example.data.local.DebitCardEntity
import com.example.data.local.DocumentEntity
import com.example.data.local.FamilyMemberEntity
import com.example.data.local.SubscriptionEntity
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
    ACCOUNTS("Banks"),
    WALLETS("Wallets & Gifts"),
    DOCUMENTS("Docs"),
    MEMBERS("Family"),
    SETTINGS("Settings")
}

enum class ThemeCategory(val label: String, val emoji: String) {
    ALL("All Themes", "✨"),
    CORE("Core Essentials", "🌟"),
    LUXURY("Luxury & Vault", "👑"),
    ARTISTIC("Artistic & Editorial", "🎨"),
    VIBE("Modern & Moods", "🌈")
}

enum class AppThemeMode(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val category: ThemeCategory,
    val isDark: Boolean
) {
    SYSTEM("System", "Follow Android system mode", "⚙️", ThemeCategory.CORE, true),
    DARK("Dark Obsidian", "Midnight slate with tonal elevation & indigo", "🌙", ThemeCategory.CORE, true),
    LIGHT("Light Contrast", "Crisp stark white & high contrast black text", "☀️", ThemeCategory.CORE, false),
    PITCH_BLACK("Pitch Black", "Pure AMOLED deep black #000000", "🖤", ThemeCategory.CORE, true),

    EMERALD_VAULT("Emerald Vault", "Swiss private banking, jade & gold accents", "🌲", ThemeCategory.LUXURY, true),
    MIDNIGHT_ROSE("Midnight Rose", "Plum velvet with electric rose & amethyst", "🌹", ThemeCategory.LUXURY, true),
    PLATINUM_LUXURY("Titanium Luxury", "Matte gunmetal, titanium gray & chrome", "💎", ThemeCategory.LUXURY, true),

    DOODLE("Doodle Light", "Hand-drawn playful notebook & pastel markers", "🎨", ThemeCategory.ARTISTIC, false),
    DOODLE_DARK("Doodle Dark", "Chalkboard & neon gel pens on slate", "✏️", ThemeCategory.ARTISTIC, true),
    PAPERLIKE("Paperlike", "Warm cream editorial Notion parchment", "📜", ThemeCategory.ARTISTIC, false),

    CYBER_NEON("Cyber Neon", "Synthwave cosmic navy with glowing cyan & purple", "🌌", ThemeCategory.VIBE, true),
    NORDIC_FROST("Nordic Frost", "Minimalist arctic glacial blue & ice slate", "❄️", ThemeCategory.VIBE, false),
    SUNSET_AMBER("Sunset Amber", "Warm terracotta copper, sand & desert gold", "🌇", ThemeCategory.VIBE, false),
    MATCHA_SAGE("Matcha & Sage", "Soothing calm organic herbal tea & olive", "🍵", ThemeCategory.VIBE, false),
    HIGH_CONTRAST("High Contrast", "WCAG AAA cyber neon yellow & stark cyan", "⚡", ThemeCategory.VIBE, true)
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
    val isEmergencyContact: Boolean = false,
    val emergencyPhone: String = "",
    val bloodGroup: String = "",
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
    val isBillPaid: Boolean = false,
    val lastPaidDate: String = "",
    val domesticPosLimit: Long = 0L,
    val atmDailyLimit: Long = 0L,
    val internationalEnabled: Boolean = false,
    val atmPin: String = "",
    val cardPin: String = "",
    val remindExpiry: Boolean = true,
    val remindBillDate: Boolean = true,
    val remindDueDate: Boolean = true,
    val status: CardStatus = CardStatus.ACTIVE,
    val memberId: String = "",
    val colorHex: Long = 0xFF1E293B,
    val linkedEmail: String = "",
    val linkedPhone: String = "",
    val customerCareNumber: String = "",
    val supportEmail: String = "",
    val frontCardImagePath: String? = null,
    val backCardImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList()
) {
    val dailyAtmLimit: Long get() = atmDailyLimit
    val internationalUsage: Boolean get() = internationalEnabled
}

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
    val domesticPosLimit: Long = 0L,
    val atmDailyLimit: Long = 0L,
    val internationalEnabled: Boolean = false,
    val atmPin: String = "",
    val cardPin: String = "",
    val remindExpiry: Boolean = true,
    val status: CardStatus = CardStatus.ACTIVE,
    val memberId: String = "",
    val colorHex: Long = 0xFF0D5C46,
    val linkedEmail: String = "",
    val linkedPhone: String = "",
    val customerCareNumber: String = "",
    val supportEmail: String = "",
    val frontCardImagePath: String? = null,
    val backCardImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList()
) {
    val dailyAtmLimit: Long get() = atmDailyLimit
    val internationalUsage: Boolean get() = internationalEnabled
}

data class BankAccount(
    val id: String,
    val bankName: String,
    val accountType: String, // Savings, Current, Overdraft, Loan Account
    val accountNumber: String,
    val ifscCode: String,
    val micrCode: String = "",
    val cifOrClientCode: String? = null,
    val accountHolderName: String,
    val branchName: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFF004C8F,
    val linkedEmail: String = "",
    val linkedPhone: String = "",
    val customerCareNumber: String = "",
    val supportEmail: String = "",
    val netBankingUserId: String = "",
    val netBankingPassword: String = "",
    val mobileBankingUserId: String = "",
    val mobileBankingPassword: String = "",
    val chequeBookImagePath: String? = null,
    val passbookImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList()
)

data class Document(
    val id: String,
    val title: String,
    val docType: DocType,
    val customDocTypeName: String? = null,
    val docNumber: String,
    val issuanceDate: Long? = null,
    val expiryDate: Long? = null,
    val pdfPassword: String = "",
    val memberId: String = "",
    val notes: String? = null,
    val attachmentPaths: List<String> = emptyList(),
    val colorHex: Long = docType.defaultColorHex
) {
    val displayTitle: String
        get() = if (title.isNotBlank()) title else if (docType == DocType.OTHER && !customDocTypeName.isNullOrBlank()) customDocTypeName else docType.title

    val isExpired: Boolean
        get() = expiryDate != null && expiryDate < System.currentTimeMillis()

    val daysUntilExpiry: Long?
        get() = expiryDate?.let { (it - System.currentTimeMillis()) / (1000 * 60 * 60 * 24) }
}

// Typealias for smooth backward compatibility
typealias PersonalDocument = Document

data class WalletOrGiftCard(
    val id: String,
    val isGiftCard: Boolean, // false for Online Wallet, true for Gift Card
    val providerOrName: String, // Provider for wallet (Paytm/GPay/PhonePe), Brand for Gift Card (Amazon/Apple)
    val cardNumberOrUpi: String, // UPI ID / Mobile for wallet, Voucher / PIN for Gift Card
    val giftCardPin: String = "", // PIN to redeem GiftCard next to voucher code
    val vendorName: String = "", // Vendor name from where gift card is bought
    val remindExpiry: Boolean = true, // Remind on expiry day
    val amount: Double = 0.0,
    val initialAmount: Double = if (amount > 0) amount else 0.0,
    val currentBalance: Double = if (amount > 0) amount else 0.0,
    val expiryDate: String = "",
    val modeOfRedemption: String = "Online / App", // Online, In-Store, App, Voucher Code
    val remarks: String = "",
    val kycStatus: String = "Full KYC Verified",
    val registeredMobile: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFFB45309,
    val barcodeOrReceiptImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList()
)

data class Subscription(
    val id: String,
    val name: String,
    val planName: String = "",
    val cost: Double = 0.0,
    val billingCycle: String = "Monthly", // Monthly, Annual, Quarterly
    val nextRenewalDate: String = "",
    val linkedPaymentMethod: String = "",
    val memberId: String = "",
    val category: String = "Entertainment",
    val colorHex: Long = 0xFF6366F1,
    val notes: String = ""
)

// Extension mappers between Entities and Domain Models
fun FamilyMemberEntity.toDomain() = FamilyMember(
    id = id,
    name = name,
    relationship = relationship,
    relationshipCategory = relationshipCategory,
    customRelationship = customRelationship,
    profilePictureUri = profilePictureUri,
    colorHex = colorHex,
    isEmergencyContact = isEmergencyContact,
    emergencyPhone = emergencyPhone,
    bloodGroup = bloodGroup
)

fun FamilyMember.toEntity() = FamilyMemberEntity(
    id = id,
    name = name,
    relationship = relationship,
    relationshipCategory = relationshipCategory,
    customRelationship = customRelationship,
    profilePictureUri = profilePictureUri,
    colorHex = colorHex,
    isEmergencyContact = isEmergencyContact,
    emergencyPhone = emergencyPhone,
    bloodGroup = bloodGroup
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
    isBillPaid = isBillPaid,
    lastPaidDate = lastPaidDate,
    domesticPosLimit = domesticPosLimit,
    atmDailyLimit = atmDailyLimit,
    internationalEnabled = internationalEnabled,
    atmPin = atmPin,
    cardPin = cardPin,
    remindExpiry = remindExpiry,
    remindBillDate = remindBillDate,
    remindDueDate = remindDueDate,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone,
    customerCareNumber = customerCareNumber,
    supportEmail = supportEmail,
    frontCardImagePath = frontCardImagePath,
    backCardImagePath = backCardImagePath,
    attachmentPaths = attachmentPaths
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
    isBillPaid = isBillPaid,
    lastPaidDate = lastPaidDate,
    domesticPosLimit = domesticPosLimit,
    atmDailyLimit = atmDailyLimit,
    internationalEnabled = internationalEnabled,
    atmPin = atmPin,
    cardPin = cardPin,
    remindExpiry = remindExpiry,
    remindBillDate = remindBillDate,
    remindDueDate = remindDueDate,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone,
    customerCareNumber = customerCareNumber,
    supportEmail = supportEmail,
    frontCardImagePath = frontCardImagePath,
    backCardImagePath = backCardImagePath,
    attachmentPaths = attachmentPaths
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
    domesticPosLimit = domesticPosLimit,
    atmDailyLimit = atmDailyLimit,
    internationalEnabled = internationalEnabled,
    atmPin = atmPin,
    cardPin = cardPin,
    remindExpiry = remindExpiry,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone,
    customerCareNumber = customerCareNumber,
    supportEmail = supportEmail,
    frontCardImagePath = frontCardImagePath,
    backCardImagePath = backCardImagePath,
    attachmentPaths = attachmentPaths
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
    domesticPosLimit = domesticPosLimit,
    atmDailyLimit = atmDailyLimit,
    internationalEnabled = internationalEnabled,
    atmPin = atmPin,
    cardPin = cardPin,
    remindExpiry = remindExpiry,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone,
    customerCareNumber = customerCareNumber,
    supportEmail = supportEmail,
    frontCardImagePath = frontCardImagePath,
    backCardImagePath = backCardImagePath,
    attachmentPaths = attachmentPaths
)

fun BankAccountEntity.toDomain() = BankAccount(
    id = id,
    bankName = bankName,
    accountType = accountType,
    accountNumber = accountNumber,
    ifscCode = ifscCode,
    micrCode = micrCode,
    cifOrClientCode = cifOrClientCode,
    accountHolderName = accountHolderName,
    branchName = branchName,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone,
    customerCareNumber = customerCareNumber,
    supportEmail = supportEmail,
    netBankingUserId = netBankingUserId,
    netBankingPassword = netBankingPassword,
    mobileBankingUserId = mobileBankingUserId,
    mobileBankingPassword = mobileBankingPassword,
    chequeBookImagePath = chequeBookImagePath,
    passbookImagePath = passbookImagePath,
    attachmentPaths = attachmentPaths
)

fun BankAccount.toEntity() = BankAccountEntity(
    id = id,
    bankName = bankName,
    accountType = accountType,
    accountNumber = accountNumber,
    ifscCode = ifscCode,
    micrCode = micrCode,
    cifOrClientCode = cifOrClientCode,
    accountHolderName = accountHolderName,
    branchName = branchName,
    memberId = memberId,
    colorHex = colorHex,
    linkedEmail = linkedEmail,
    linkedPhone = linkedPhone,
    customerCareNumber = customerCareNumber,
    supportEmail = supportEmail,
    netBankingUserId = netBankingUserId,
    netBankingPassword = netBankingPassword,
    mobileBankingUserId = mobileBankingUserId,
    mobileBankingPassword = mobileBankingPassword,
    chequeBookImagePath = chequeBookImagePath,
    passbookImagePath = passbookImagePath,
    attachmentPaths = attachmentPaths
)

fun DocumentEntity.toDomain() = Document(
    id = id,
    title = title,
    docType = docType,
    customDocTypeName = customDocTypeName,
    docNumber = docNumber,
    issuanceDate = issuanceDate,
    expiryDate = expiryDate,
    pdfPassword = pdfPassword,
    notes = notes,
    memberId = memberId,
    attachmentPaths = attachmentPaths,
    colorHex = colorHex
)

fun Document.toEntity() = DocumentEntity(
    id = id,
    title = title,
    docType = docType,
    customDocTypeName = customDocTypeName,
    docNumber = docNumber,
    issuanceDate = issuanceDate,
    expiryDate = expiryDate,
    pdfPassword = pdfPassword,
    notes = notes,
    memberId = memberId,
    attachmentPaths = attachmentPaths,
    colorHex = colorHex
)

fun WalletOrGiftCardEntity.toDomain() = WalletOrGiftCard(
    id = id,
    isGiftCard = isGiftCard,
    providerOrName = providerOrName,
    cardNumberOrUpi = cardNumberOrUpi,
    giftCardPin = giftCardPin,
    vendorName = vendorName,
    remindExpiry = remindExpiry,
    amount = amount,
    initialAmount = if (initialAmount > 0) initialAmount else amount,
    currentBalance = if (currentBalance > 0) currentBalance else amount,
    expiryDate = expiryDate,
    modeOfRedemption = modeOfRedemption,
    remarks = remarks,
    kycStatus = kycStatus,
    registeredMobile = registeredMobile,
    memberId = memberId,
    colorHex = colorHex,
    barcodeOrReceiptImagePath = barcodeOrReceiptImagePath,
    attachmentPaths = attachmentPaths
)

fun WalletOrGiftCard.toEntity() = WalletOrGiftCardEntity(
    id = id,
    isGiftCard = isGiftCard,
    providerOrName = providerOrName,
    cardNumberOrUpi = cardNumberOrUpi,
    giftCardPin = giftCardPin,
    vendorName = vendorName,
    remindExpiry = remindExpiry,
    amount = amount,
    initialAmount = if (initialAmount > 0) initialAmount else amount,
    currentBalance = if (currentBalance > 0) currentBalance else amount,
    expiryDate = expiryDate,
    modeOfRedemption = modeOfRedemption,
    remarks = remarks,
    kycStatus = kycStatus,
    registeredMobile = registeredMobile,
    memberId = memberId,
    colorHex = colorHex,
    barcodeOrReceiptImagePath = barcodeOrReceiptImagePath,
    attachmentPaths = attachmentPaths
)

fun SubscriptionEntity.toDomain() = Subscription(
    id = id,
    name = name,
    planName = planName,
    cost = cost,
    billingCycle = billingCycle,
    nextRenewalDate = nextRenewalDate,
    linkedPaymentMethod = linkedPaymentMethod,
    memberId = memberId,
    category = category,
    colorHex = colorHex,
    notes = notes
)

fun Subscription.toEntity() = SubscriptionEntity(
    id = id,
    name = name,
    planName = planName,
    cost = cost,
    billingCycle = billingCycle,
    nextRenewalDate = nextRenewalDate,
    linkedPaymentMethod = linkedPaymentMethod,
    memberId = memberId,
    category = category,
    colorHex = colorHex,
    notes = notes
)
