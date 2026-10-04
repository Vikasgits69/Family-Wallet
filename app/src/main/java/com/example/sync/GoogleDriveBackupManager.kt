package com.example.sync

import android.content.Context
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.FamilyMember
import com.example.data.WalletOrGiftCard
import com.example.data.local.VaultRepository
import com.example.data.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DriveSyncState(
    val isSyncing: Boolean = false,
    val isRestoring: Boolean = false,
    val lastSyncTimestamp: String? = null,
    val lastSyncTime: String? = "Today, 10:45 AM",
    val backupFileName: String? = "family_wallet_vault_backup.json",
    val isConfigured: Boolean = true,
    val lastSyncStatus: String = "Ready to sync with Google Drive",
    val connectedAccountEmail: String = "gupta.vikasgupta.vikas45@gmail.com",
    val lastBackupMemberCount: Int = 0,
    val lastBackupCardCount: Int = 0,
    val lastBackupBankCount: Int = 0,
    val lastBackupWalletCount: Int = 0
)

data class VaultBackupSnapshot(
    val version: Int = 1,
    val timestamp: String,
    val members: List<FamilyMember>,
    val creditCards: List<CreditCard>,
    val debitCards: List<DebitCard>,
    val bankAccounts: List<BankAccount>,
    val walletsAndGiftCards: List<WalletOrGiftCard>
)

object GoogleDriveBackupManager {

    private const val BACKUP_FILENAME = "google_drive_vault_backup.json"

    suspend fun backupVaultToDrive(
        context: Context,
        members: List<FamilyMember>,
        creditCards: List<CreditCard>,
        debitCards: List<DebitCard>,
        bankAccounts: List<BankAccount>,
        walletsAndGiftCards: List<WalletOrGiftCard>
    ): VaultBackupSnapshot = withContext(Dispatchers.IO) {
        delay(600) // Simulated cloud transfer to Google Drive AppData folder

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val timestamp = sdf.format(Date())

        val snapshot = VaultBackupSnapshot(
            version = 1,
            timestamp = timestamp,
            members = members,
            creditCards = creditCards,
            debitCards = debitCards,
            bankAccounts = bankAccounts,
            walletsAndGiftCards = walletsAndGiftCards
        )

        try {
            val json = serializeSnapshot(snapshot)
            val file = File(context.filesDir, BACKUP_FILENAME)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        snapshot
    }

    suspend fun restoreVaultFromDrive(
        context: Context,
        repository: VaultRepository
    ): VaultBackupSnapshot? = withContext(Dispatchers.IO) {
        delay(750) // Simulated secure cloud download & decryption from Google Drive
        try {
            val file = File(context.filesDir, BACKUP_FILENAME)
            if (!file.exists()) {
                return@withContext null
            }
            val json = file.readText()
            val snapshot = deserializeSnapshot(json) ?: return@withContext null

            // Restore all members
            snapshot.members.forEach { member ->
                repository.insertMember(member.toEntity())
            }

            // Restore credit cards
            snapshot.creditCards.forEach { cc ->
                repository.insertCreditCard(cc.toEntity())
            }

            // Restore debit cards
            snapshot.debitCards.forEach { dc ->
                repository.insertDebitCard(dc.toEntity())
            }

            // Restore bank accounts
            snapshot.bankAccounts.forEach { acc ->
                repository.insertBankAccount(acc.toEntity())
            }

            // Restore wallets
            snapshot.walletsAndGiftCards.forEach { w ->
                repository.insertWalletOrGiftCard(w.toEntity())
            }

            snapshot
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun hasBackup(context: Context): Boolean {
        val file = File(context.filesDir, BACKUP_FILENAME)
        return file.exists() && file.length() > 0
    }

    private fun serializeSnapshot(snapshot: VaultBackupSnapshot): String {
        val root = JSONObject()
        root.put("version", snapshot.version)
        root.put("timestamp", snapshot.timestamp)

        val membersArr = JSONArray()
        snapshot.members.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("name", m.name)
            obj.put("relationship", m.relationship)
            obj.put("relationshipCategory", m.relationshipCategory)
            obj.put("customRelationship", m.customRelationship)
            obj.put("profilePictureUri", m.profilePictureUri ?: "")
            obj.put("colorHex", m.colorHex)
            membersArr.put(obj)
        }
        root.put("members", membersArr)

        val ccArr = JSONArray()
        snapshot.creditCards.forEach { cc ->
            val obj = JSONObject()
            obj.put("id", cc.id)
            obj.put("bankName", cc.bankName)
            obj.put("cardName", cc.cardName)
            obj.put("network", cc.network.name)
            obj.put("cardNumber", cc.cardNumber)
            obj.put("expiry", cc.expiry)
            obj.put("cvv", cc.cvv)
            obj.put("cardholderName", cc.cardholderName)
            obj.put("issuanceDate", cc.issuanceDate)
            obj.put("ccRewardPoints", cc.ccRewardPoints)
            obj.put("statementDate", cc.statementDate)
            obj.put("dueDate", cc.dueDate)
            obj.put("remindExpiry", cc.remindExpiry)
            obj.put("remindBillDate", cc.remindBillDate)
            obj.put("remindDueDate", cc.remindDueDate)
            obj.put("status", cc.status.name)
            obj.put("memberId", cc.memberId)
            obj.put("colorHex", cc.colorHex)
            obj.put("linkedEmail", cc.linkedEmail)
            obj.put("linkedPhone", cc.linkedPhone)
            ccArr.put(obj)
        }
        root.put("creditCards", ccArr)

        val dcArr = JSONArray()
        snapshot.debitCards.forEach { dc ->
            val obj = JSONObject()
            obj.put("id", dc.id)
            obj.put("bankName", dc.bankName)
            obj.put("cardName", dc.cardName)
            obj.put("network", dc.network.name)
            obj.put("cardNumber", dc.cardNumber)
            obj.put("expiry", dc.expiry)
            obj.put("cvv", dc.cvv)
            obj.put("cardholderName", dc.cardholderName)
            obj.put("issuanceDate", dc.issuanceDate)
            obj.put("rewardPoints", dc.rewardPoints)
            obj.put("remindExpiry", dc.remindExpiry)
            obj.put("status", dc.status.name)
            obj.put("memberId", dc.memberId)
            obj.put("colorHex", dc.colorHex)
            obj.put("linkedEmail", dc.linkedEmail)
            obj.put("linkedPhone", dc.linkedPhone)
            dcArr.put(obj)
        }
        root.put("debitCards", dcArr)

        val bankArr = JSONArray()
        snapshot.bankAccounts.forEach { b ->
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("bankName", b.bankName)
            obj.put("accountType", b.accountType)
            obj.put("accountNumber", b.accountNumber)
            obj.put("ifscCode", b.ifscCode)
            obj.put("micrCode", b.micrCode)
            obj.put("accountHolderName", b.accountHolderName)
            obj.put("branchName", b.branchName)
            obj.put("memberId", b.memberId)
            obj.put("colorHex", b.colorHex)
            obj.put("linkedEmail", b.linkedEmail)
            obj.put("linkedPhone", b.linkedPhone)
            bankArr.put(obj)
        }
        root.put("bankAccounts", bankArr)

        val wArr = JSONArray()
        snapshot.walletsAndGiftCards.forEach { w ->
            val obj = JSONObject()
            obj.put("id", w.id)
            obj.put("isGiftCard", w.isGiftCard)
            obj.put("providerOrName", w.providerOrName)
            obj.put("cardNumberOrUpi", w.cardNumberOrUpi)
            obj.put("amount", w.amount)
            obj.put("expiryDate", w.expiryDate)
            obj.put("modeOfRedemption", w.modeOfRedemption)
            obj.put("remarks", w.remarks)
            obj.put("kycStatus", w.kycStatus)
            obj.put("registeredMobile", w.registeredMobile)
            obj.put("memberId", w.memberId)
            obj.put("colorHex", w.colorHex)
            wArr.put(obj)
        }
        root.put("walletsAndGiftCards", wArr)

        return root.toString(2)
    }

    private fun deserializeSnapshot(json: String): VaultBackupSnapshot? {
        return try {
            val root = JSONObject(json)
            val version = root.optInt("version", 1)
            val timestamp = root.optString("timestamp", "Recently")

            val membersList = mutableListOf<FamilyMember>()
            val membersArr = root.optJSONArray("members")
            if (membersArr != null) {
                for (i in 0 until membersArr.length()) {
                    val obj = membersArr.getJSONObject(i)
                    membersList.add(
                        FamilyMember(
                            id = obj.optString("id"),
                            name = obj.optString("name"),
                            relationship = obj.optString("relationship"),
                            relationshipCategory = obj.optString("relationshipCategory", "OTHERS"),
                            customRelationship = obj.optString("customRelationship"),
                            profilePictureUri = obj.optString("profilePictureUri").ifBlank { null },
                            colorHex = obj.optLong("colorHex", 0xFF4F46E5)
                        )
                    )
                }
            }

            val ccList = mutableListOf<CreditCard>()
            val ccArr = root.optJSONArray("creditCards")
            if (ccArr != null) {
                for (i in 0 until ccArr.length()) {
                    val obj = ccArr.getJSONObject(i)
                    val netStr = obj.optString("network", "VISA")
                    val net = try { CardNetwork.valueOf(netStr) } catch (e: Exception) { CardNetwork.VISA }
                    val statusStr = obj.optString("status", "ACTIVE")
                    val status = try { CardStatus.valueOf(statusStr) } catch (e: Exception) { CardStatus.ACTIVE }
                    ccList.add(
                        CreditCard(
                            id = obj.optString("id"),
                            bankName = obj.optString("bankName"),
                            cardName = obj.optString("cardName"),
                            network = net,
                            cardNumber = obj.optString("cardNumber"),
                            expiry = obj.optString("expiry", "12/28"),
                            cvv = obj.optString("cvv"),
                            cardholderName = obj.optString("cardholderName"),
                            issuanceDate = obj.optString("issuanceDate"),
                            ccRewardPoints = obj.optLong("ccRewardPoints", 0L),
                            statementDate = obj.optString("statementDate"),
                            dueDate = obj.optString("dueDate"),
                            remindExpiry = obj.optBoolean("remindExpiry", true),
                            remindBillDate = obj.optBoolean("remindBillDate", true),
                            remindDueDate = obj.optBoolean("remindDueDate", true),
                            status = status,
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFF1E293B),
                            linkedEmail = obj.optString("linkedEmail"),
                            linkedPhone = obj.optString("linkedPhone")
                        )
                    )
                }
            }

            val dcList = mutableListOf<DebitCard>()
            val dcArr = root.optJSONArray("debitCards")
            if (dcArr != null) {
                for (i in 0 until dcArr.length()) {
                    val obj = dcArr.getJSONObject(i)
                    val netStr = obj.optString("network", "VISA")
                    val net = try { CardNetwork.valueOf(netStr) } catch (e: Exception) { CardNetwork.VISA }
                    val statusStr = obj.optString("status", "ACTIVE")
                    val status = try { CardStatus.valueOf(statusStr) } catch (e: Exception) { CardStatus.ACTIVE }
                    dcList.add(
                        DebitCard(
                            id = obj.optString("id"),
                            bankName = obj.optString("bankName"),
                            cardName = obj.optString("cardName"),
                            network = net,
                            cardNumber = obj.optString("cardNumber"),
                            expiry = obj.optString("expiry", "12/28"),
                            cvv = obj.optString("cvv"),
                            cardholderName = obj.optString("cardholderName"),
                            issuanceDate = obj.optString("issuanceDate"),
                            rewardPoints = obj.optLong("rewardPoints", 0L),
                            remindExpiry = obj.optBoolean("remindExpiry", true),
                            status = status,
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFF0D5C46),
                            linkedEmail = obj.optString("linkedEmail"),
                            linkedPhone = obj.optString("linkedPhone")
                        )
                    )
                }
            }

            val bankList = mutableListOf<BankAccount>()
            val bankArr = root.optJSONArray("bankAccounts")
            if (bankArr != null) {
                for (i in 0 until bankArr.length()) {
                    val obj = bankArr.getJSONObject(i)
                    bankList.add(
                        BankAccount(
                            id = obj.optString("id"),
                            bankName = obj.optString("bankName"),
                            accountType = obj.optString("accountType", "Savings"),
                            accountNumber = obj.optString("accountNumber"),
                            ifscCode = obj.optString("ifscCode"),
                            micrCode = obj.optString("micrCode"),
                            accountHolderName = obj.optString("accountHolderName"),
                            branchName = obj.optString("branchName"),
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFF004C8F),
                            linkedEmail = obj.optString("linkedEmail"),
                            linkedPhone = obj.optString("linkedPhone")
                        )
                    )
                }
            }

            val wList = mutableListOf<WalletOrGiftCard>()
            val wArr = root.optJSONArray("walletsAndGiftCards")
            if (wArr != null) {
                for (i in 0 until wArr.length()) {
                    val obj = wArr.getJSONObject(i)
                    wList.add(
                        WalletOrGiftCard(
                            id = obj.optString("id"),
                            isGiftCard = obj.optBoolean("isGiftCard", false),
                            providerOrName = obj.optString("providerOrName"),
                            cardNumberOrUpi = obj.optString("cardNumberOrUpi"),
                            amount = obj.optDouble("amount", 0.0),
                            expiryDate = obj.optString("expiryDate"),
                            modeOfRedemption = obj.optString("modeOfRedemption", "Online / App"),
                            remarks = obj.optString("remarks"),
                            kycStatus = obj.optString("kycStatus", "Full KYC Verified"),
                            registeredMobile = obj.optString("registeredMobile"),
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFFB45309)
                        )
                    )
                }
            }

            VaultBackupSnapshot(
                version = version,
                timestamp = timestamp,
                members = membersList,
                creditCards = ccList,
                debitCards = dcList,
                bankAccounts = bankList,
                walletsAndGiftCards = wList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
