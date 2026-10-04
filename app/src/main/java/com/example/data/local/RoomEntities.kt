package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val relationship: String,
    val relationshipCategory: String, // MEN, WOMEN, CHILD, PARENTS, SIBLINGS, OTHER
    val customRelationship: String = "",
    val profilePictureUri: String? = null,
    val colorHex: Long = 0xFF4F46E5
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
    val issuanceDate: String,
    val ccRewardPoints: Long = 0L,
    val statementDate: String = "", // Bill Date e.g. "12th"
    val dueDate: String = "",       // Payment Due Date e.g. "2nd"
    val remindExpiry: Boolean = true,
    val remindBillDate: Boolean = true,
    val remindDueDate: Boolean = true,
    val memberId: String = "",
    val colorHex: Long = 0xFF1E293B,
    val linkedEmail: String = "",
    val linkedPhone: String = ""
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
    val issuanceDate: String,
    val rewardPoints: Long = 0L,
    val remindExpiry: Boolean = true,
    val memberId: String = "",
    val colorHex: Long = 0xFF0D5C46,
    val linkedEmail: String = "",
    val linkedPhone: String = ""
)

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey val id: String,
    val bankName: String,
    val accountType: String, // Savings, Current, Overdraft, Loan Account
    val accountNumber: String,
    val ifscCode: String,
    val micrCode: String,
    val accountHolderName: String,
    val branchName: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFF004C8F,
    val linkedEmail: String = "",
    val linkedPhone: String = ""
)

@Entity(tableName = "wallets_and_gift_cards")
data class WalletOrGiftCardEntity(
    @PrimaryKey val id: String,
    val isGiftCard: Boolean, // false for online wallet, true for gift card
    val providerOrName: String, // Wallet Provider (e.g. Paytm, GPay) OR Gift Card Brand (Amazon, Apple)
    val cardNumberOrUpi: String, // UPI ID / Phone OR Gift Card Code/PIN
    val amount: Double = 0.0,
    val expiryDate: String = "",
    val modeOfRedemption: String = "Online / App", // Online, In-Store, App, Voucher Code
    val remarks: String = "",
    val kycStatus: String = "Full KYC Verified",
    val registeredMobile: String = "",
    val memberId: String = "",
    val colorHex: Long = 0xFFB45309
)
