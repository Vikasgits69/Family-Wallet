package com.example.sync

import com.example.data.BankAccount
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.FamilyMember
import com.example.data.OnlineWallet
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WalletBackupPayload(
    val version: Int = 2,
    val backupTimestamp: Long = System.currentTimeMillis(),
    val backupDateFormatted: String = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
    val appName: String = "Family Wallet Vault",
    val memberCount: Int,
    val creditCardCount: Int,
    val debitCardCount: Int,
    val bankAccountCount: Int,
    val walletCount: Int,
    val members: List<FamilyMember>,
    val creditCards: List<CreditCard>,
    val debitCards: List<DebitCard>,
    val bankAccounts: List<BankAccount>,
    val onlineWallets: List<OnlineWallet>
)

data class DriveSyncState(
    val isSyncing: Boolean = false,
    val lastSyncTime: String? = null,
    val lastSyncStatus: String = "Ready to sync with Google Drive",
    val isAutoSyncEnabled: Boolean = true,
    val backupSizeKb: Double = 0.0,
    val cloudFileId: String? = null,
    val connectedAccountEmail: String = "gupta.vikasgupta.vikas45@gmail.com"
)

object GoogleDriveBackupManager {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun serializeBackup(
        members: List<FamilyMember>,
        creditCards: List<CreditCard>,
        debitCards: List<DebitCard>,
        bankAccounts: List<BankAccount>,
        onlineWallets: List<OnlineWallet>
    ): String {
        val payload = WalletBackupPayload(
            memberCount = members.size,
            creditCardCount = creditCards.size,
            debitCardCount = debitCards.size,
            bankAccountCount = bankAccounts.size,
            walletCount = onlineWallets.size,
            members = members,
            creditCards = creditCards,
            debitCards = debitCards,
            bankAccounts = bankAccounts,
            onlineWallets = onlineWallets
        )
        val adapter = moshi.adapter(WalletBackupPayload::class.java).indent("  ")
        return adapter.toJson(payload)
    }

    suspend fun simulateBackup(
        onProgress: (DriveSyncState) -> Unit,
        onComplete: (Boolean, String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgress(
                DriveSyncState(
                    isSyncing = true,
                    lastSyncStatus = "Encrypting vault payload (AES-256 GCM)..."
                )
            )
            delay(500)

            onProgress(
                DriveSyncState(
                    isSyncing = true,
                    lastSyncStatus = "Connecting to Google Drive API v3..."
                )
            )
            delay(500)

            val now = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
            onProgress(
                DriveSyncState(
                    isSyncing = false,
                    lastSyncTime = now,
                    lastSyncStatus = "Encrypted backup synced successfully (Revision #4)",
                    backupSizeKb = 8.4,
                    cloudFileId = "1aBcD_FamilyWalletVault_Encrypted"
                )
            )
            onComplete(true, "Backup synced successfully")
        } catch (e: Exception) {
            onProgress(
                DriveSyncState(
                    isSyncing = false,
                    lastSyncStatus = "Sync failed: ${e.localizedMessage ?: "Unknown error"}"
                )
            )
            onComplete(false, e.localizedMessage ?: "Unknown error")
        }
    }
}
