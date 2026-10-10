package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.DocType

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val relationship: String,
    val relationshipCategory: String, // MEN, WOMEN, CHILD, PARENTS, SIBLINGS, OTHER
    val customRelationship: String = "",
    val profilePictureUri: String? = null,
    val colorHex: Long = 0xFF4F46E5,
    val isEmergencyContact: Boolean = false,
    val emergencyPhone: String = "",
    val bloodGroup: String = ""
)

@Entity(tableName = "credit_cards")
data class CreditCardEntity(
    @PrimaryKey val id: String,
    val bankName: String,
    val cardName: String,
    val network: String, // RUPAY, VISA, MASTERCARD, AMEX
    val cardNumber: String,
    val expiry: String,
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
    val memberId: String = "",
    val colorHex: Long = 0xFF1E293B,
    val linkedEmail: String = "",
    val linkedPhone: String = "",
    val customerCareNumber: String = "",
    val supportEmail: String = "",
    val frontCardImagePath: String? = null,
    val backCardImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList(),
    val currentStatementMonth: String = "",
    val statementOpeningBalance: Double = 0.0,
    val statementTotalExpenses: Double = 0.0,
    val statementTotalPayments: Double = 0.0,
    val statementClosingBalance: Double = 0.0,
    val statementOpeningRewardPoints: Long = 0L,
    val statementRewardPointsEarned: Long = 0L,
    val statementRewardPointsRedeemed: Long = 0L,
    val statementClosingRewardPoints: Long = 0L,
    val statementLogsJson: String = ""
)

@Entity(tableName = "debit_cards")
data class DebitCardEntity(
    @PrimaryKey val id: String,
    val bankName: String,
    val cardName: String,
    val network: String, // RUPAY, VISA, MASTERCARD, AMEX
    val cardNumber: String,
    val expiry: String,
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
    val memberId: String = "",
    val colorHex: Long = 0xFF0D5C46,
    val linkedEmail: String = "",
    val linkedPhone: String = "",
    val customerCareNumber: String = "",
    val supportEmail: String = "",
    val frontCardImagePath: String? = null,
    val backCardImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList()
)

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "wallets_and_gift_cards")
data class WalletOrGiftCardEntity(
    @PrimaryKey val id: String,
    val isGiftCard: Boolean, // false for online wallet, true for gift card
    val providerOrName: String, // Wallet Provider (e.g. Paytm, GPay) OR Gift Card Brand (Amazon, Apple)
    val cardNumberOrUpi: String, // UPI ID / Phone OR Gift Card Code/PIN
    val giftCardPin: String = "", // PIN to redeem GiftCard next to voucher code
    val vendorName: String = "", // Vendor name from where gift card is bought
    val remindExpiry: Boolean = true, // Remind on expiry day
    val amount: Double = 0.0,
    val initialAmount: Double = 0.0,
    val currentBalance: Double = 0.0,
    val expiryDate: String = "",
    val modeOfRedemption: String = "Online / App", // Online, In-Store, App, Voucher Code
    val remarks: String = "",
    val kycStatus: String = "Full KYC Verified",
    val registeredMobile: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFFB45309,
    val barcodeOrReceiptImagePath: String? = null,
    val attachmentPaths: List<String> = emptyList(),
    val isMarkedAsUsed: Boolean = false
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
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
    val colorHex: Long = 0xFF0284C7
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val planName: String = "",
    val cost: Double = 0.0,
    val billingCycle: String = "Monthly", // Monthly, Annual, Quarterly
    val nextRenewalDate: String = "", // e.g. 15 Nov 2026
    val linkedPaymentMethod: String = "",
    val memberId: String = "",
    val category: String = "Entertainment",
    val colorHex: Long = 0xFF6366F1,
    val notes: String = ""
)
