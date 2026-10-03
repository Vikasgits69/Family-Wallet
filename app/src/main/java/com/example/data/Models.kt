package com.example.data

enum class CardNetwork(val label: String) {
    RUPAY("RuPay"),
    VISA("VISA"),
    MASTERCARD("Mastercard"),
    AMEX("American Express")
}

enum class CardStatus(val label: String) {
    ACTIVE("Active"),
    BLOCKED("Blocked"),
    EXPIRED("Expired"),
    INACTIVE("Inactive")
}

enum class KycStatus(val label: String) {
    FULL_KYC("Full KYC Verified"),
    MIN_KYC("Min KYC Completed"),
    PENDING("KYC Verification Pending")
}

enum class AccountStatus(val label: String) {
    ACTIVE("Active"),
    DORMANT("Dormant"),
    FROZEN("Frozen")
}

enum class WalletStatus(val label: String) {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    INACTIVE("Inactive")
}

enum class AccountType(val label: String) {
    SAVINGS("Savings Account"),
    CURRENT("Current Account"),
    SALARY("Salary Account"),
    NRI("NRE / NRO Account"),
    FIXED_DEPOSIT("Fixed Deposit / Vault")
}

enum class CardThemeColor(val title: String, val hexCode: Long) {
    CHARCOAL("Sleek Charcoal", 0xFF1E2235),
    EMERALD("Rich Emerald", 0xFF0D5C46),
    SAPPHIRE("Deep Sapphire", 0xFF1E3A8A),
    INDIGO("Royal Indigo", 0xFF4338CA),
    RUBY("Crimson Ruby", 0xFF881337),
    AMBER("Warm Amber", 0xFF92400E),
    TITANIUM("Titanium Gold", 0xFF854D0E)
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
    WALLETS("Wallets"),
    MEMBERS("Family"),
    SETTINGS("Settings")
}

enum class AppThemeMode(val title: String, val subtitle: String, val emoji: String) {
    LIGHT("Light", "Clean slate & crisp white", "☀️"),
    DARK("Dark", "Obsidian midnight with tonal elevation", "🌙"),
    PITCH_BLACK("Pitch Black", "Pure AMOLED deep black", "🖤"),
    SYSTEM("System", "Follow Android system mode", "⚙️"),
    PAPERLIKE("Paperlike", "Warm cream editorial", "📜")
}

data class FamilyMember(
    val id: String,
    val name: String,
    val relationship: String,
    val avatarEmoji: String,
    val colorHex: Long,
    val initials: String = name.take(2).uppercase()
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
    val statementDate: String, // e.g., "12th of every month"
    val dueDate: String, // e.g., "2nd of every month"
    val creditLimit: Double,
    val annualFee: Double,
    val waiverCondition: String, // e.g., "Spend ₹1,50,000 annually to waive fee"
    val status: CardStatus = CardStatus.ACTIVE,
    val memberId: String,
    val themeColor: CardThemeColor = CardThemeColor.CHARCOAL
)

data class DebitCard(
    val id: String,
    val bankName: String,
    val linkedAccount: String, // e.g., "HDFC Salary A/c •••• 5678"
    val network: CardNetwork,
    val cardNumber: String,
    val expiry: String, // MM/YY
    val cvv: String,
    val cardholderName: String,
    val atmLimit: Double, // ₹ per day
    val posLimit: Double, // ₹ per day
    val status: CardStatus = CardStatus.ACTIVE,
    val memberId: String,
    val themeColor: CardThemeColor = CardThemeColor.EMERALD
)

data class OnlineWallet(
    val id: String,
    val providerName: String, // e.g., "Paytm", "PhonePe", "Google Pay", "Amazon Pay", "CRED"
    val registeredMobile: String,
    val registeredEmail: String,
    val upiId: String,
    val kycStatus: KycStatus,
    val walletLimit: Double,
    val status: WalletStatus = WalletStatus.ACTIVE,
    val memberId: String
)

data class BankAccount(
    val id: String,
    val bankName: String,
    val accountType: AccountType,
    val accountNumber: String,
    val ifscCode: String,
    val branchName: String,
    val accountHolderName: String,
    val customerId: String,
    val linkedMobile: String,
    val linkedUpi: String,
    val minBalance: Double,
    val status: AccountStatus = AccountStatus.ACTIVE,
    val memberId: String
)
