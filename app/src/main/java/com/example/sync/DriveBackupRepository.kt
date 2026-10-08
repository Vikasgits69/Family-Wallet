package com.example.sync

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.DocType
import com.example.data.Document
import com.example.data.FamilyMember
import com.example.data.Subscription
import com.example.data.WalletOrGiftCard
import com.example.data.local.VaultRepository
import com.example.data.toDomain
import com.example.data.toEntity
import com.example.security.CryptoManager
import com.example.util.AttachmentFileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class DriveSyncState(
    val isSyncing: Boolean = false,
    val isRestoring: Boolean = false,
    val lastSyncTimestamp: String? = null,
    val lastSyncTime: String? = "Today, 10:45 AM",
    val backupFileName: String? = "vault_backup_latest.enc",
    val isConfigured: Boolean = true,
    val lastSyncStatus: String = "Ready to sync encrypted vault with Google Drive",
    val connectedAccountEmail: String = "gupta.vikasgupta.vikas45@gmail.com",
    val lastBackupMemberCount: Int = 0,
    val lastBackupCardCount: Int = 0,
    val lastBackupBankCount: Int = 0,
    val lastBackupWalletCount: Int = 0,
    val lastBackupDocumentCount: Int = 0,
    val lastBackupAttachmentCount: Int = 0,
    val totalSnapshotVersions: Int = 1
)

data class DriveBackupFileInfo(
    val fileName: String,
    val fileSize: Long,
    val lastModified: Long,
    val formattedDate: String
)

data class VaultBackupSnapshot(
    val version: Int = 3,
    val timestamp: String,
    val members: List<FamilyMember>,
    val creditCards: List<CreditCard>,
    val debitCards: List<DebitCard>,
    val bankAccounts: List<BankAccount>,
    val walletsAndGiftCards: List<WalletOrGiftCard>,
    val documents: List<Document> = emptyList(),
    val subscriptions: List<Subscription> = emptyList(),
    val attachmentFileNames: List<String> = emptyList()
)

object DriveBackupRepository {

    private const val BACKUP_DIR_NAME = "google_drive_app_data_folder"
    private const val MAX_BACKUP_VERSIONS = 5

    private fun getDriveAppDataDir(context: Context): File {
        val dir = File(context.filesDir, BACKUP_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Packages SQLite/Room records (payload.json) + physical attachment files into an encrypted ZIP container.
     * Encrypts the ZIP container using AES-256-GCM via Android KeyStore.
     * Saves snapshot with format vault_backup_yyyyMMdd_HHmmss.enc and prunes older versions beyond 5.
     */
    suspend fun backupVaultToDrive(
        context: Context,
        members: List<FamilyMember>,
        creditCards: List<CreditCard>,
        debitCards: List<DebitCard>,
        bankAccounts: List<BankAccount>,
        walletsAndGiftCards: List<WalletOrGiftCard>,
        documents: List<Document> = emptyList(),
        subscriptions: List<Subscription> = emptyList(),
        includePhotos: Boolean = true
    ): VaultBackupSnapshot = withContext(Dispatchers.IO) {
        delay(600) // Simulated cloud transfer to Google Drive AppData folder

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val now = Date()
        val timestamp = sdf.format(now)
        val fileDateSuffix = fileDateFormat.format(now)

        // 1. Collect all referenced physical attachment files (if includePhotos is enabled)
        val referencedPaths = mutableSetOf<String>()
        if (includePhotos) {
            creditCards.forEach {
                it.frontCardImagePath?.let { p -> referencedPaths.add(p) }
                it.backCardImagePath?.let { p -> referencedPaths.add(p) }
                referencedPaths.addAll(it.attachmentPaths)
            }
            debitCards.forEach {
                it.frontCardImagePath?.let { p -> referencedPaths.add(p) }
                it.backCardImagePath?.let { p -> referencedPaths.add(p) }
                referencedPaths.addAll(it.attachmentPaths)
            }
            bankAccounts.forEach {
                it.chequeBookImagePath?.let { p -> referencedPaths.add(p) }
                it.passbookImagePath?.let { p -> referencedPaths.add(p) }
                referencedPaths.addAll(it.attachmentPaths)
            }
            walletsAndGiftCards.forEach {
                it.barcodeOrReceiptImagePath?.let { p -> referencedPaths.add(p) }
                referencedPaths.addAll(it.attachmentPaths)
            }
            documents.forEach {
                referencedPaths.addAll(it.attachmentPaths)
            }
        }

        val snapshot = VaultBackupSnapshot(
            version = 3,
            timestamp = timestamp,
            members = members,
            creditCards = creditCards,
            debitCards = debitCards,
            bankAccounts = bankAccounts,
            walletsAndGiftCards = walletsAndGiftCards,
            documents = documents,
            subscriptions = subscriptions,
            attachmentFileNames = if (includePhotos) referencedPaths.map { it.substringAfterLast("/") } else emptyList()
        )

        // 2. Build in-memory ZIP package
        val zipByteArrayOutputStream = ByteArrayOutputStream()
        ZipOutputStream(zipByteArrayOutputStream).use { zipOut ->
            // Entry 1: payload.json
            val jsonPayload = serializeSnapshot(snapshot)
            val jsonEntry = ZipEntry("payload.json")
            zipOut.putNextEntry(jsonEntry)
            zipOut.write(jsonPayload.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()

            // Entry 2: attachments/ (Only if includePhotos is enabled)
            if (includePhotos) {
                referencedPaths.forEach { relativeOrAbsolutePath ->
                    val file = AttachmentFileManager.getFile(context, relativeOrAbsolutePath)
                    if (file.exists() && file.isFile) {
                        val entryName = "attachments/${file.name}"
                        val fileEntry = ZipEntry(entryName)
                        zipOut.putNextEntry(fileEntry)
                        copyOrCompressFileToZip(file, zipOut)
                        zipOut.closeEntry()
                    }
                }
            }
        }

        val unencryptedZipBytes = zipByteArrayOutputStream.toByteArray()

        // 3. Hardware-Backed Encryption via AES-256-GCM
        val encryptedBytes = CryptoManager.encrypt(unencryptedZipBytes)

        // 4. Save to Drive AppDataFolder
        val driveFolder = getDriveAppDataDir(context)
        val snapshotFileName = "vault_backup_$fileDateSuffix.enc"
        val snapshotFile = File(driveFolder, snapshotFileName)
        snapshotFile.writeBytes(encryptedBytes)

        // Also write latest pointer
        val latestFile = File(driveFolder, "vault_backup_latest.enc")
        latestFile.writeBytes(encryptedBytes)

        // 5. Rolling Version Control: keep last 5 versions
        pruneOldSnapshots(driveFolder, MAX_BACKUP_VERSIONS)

        snapshot
    }

    /**
     * Downloads latest encrypted snapshot, decrypts with AES-256-GCM, unzips files to internal storage,
     * and overwrites Room database inside a single atomic transaction.
     */
    suspend fun restoreVaultFromDrive(
        context: Context,
        repository: VaultRepository
    ): VaultBackupSnapshot? = withContext(Dispatchers.IO) {
        delay(750) // Simulated secure cloud download & decryption from Google Drive
        try {
            val driveFolder = getDriveAppDataDir(context)
            val backupFiles = driveFolder.listFiles { file ->
                file.name.startsWith("vault_backup_") && file.name.endsWith(".enc")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()

            val targetEncFile = backupFiles.firstOrNull() ?: return@withContext null
            val encryptedBytes = targetEncFile.readBytes()

            // 1. Decrypt AES-256-GCM container
            val decryptedZipBytes = CryptoManager.decrypt(encryptedBytes)

            // 2. Unpack ZIP
            var jsonPayload: String? = null
            val attachmentsDir = AttachmentFileManager.getAttachmentsDir(context)

            ZipInputStream(decryptedZipBytes.inputStream()).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    val entryName = entry.name
                    if (entryName == "payload.json") {
                        jsonPayload = zipIn.bufferedReader(Charsets.UTF_8).readText()
                    } else if (entryName.startsWith("attachments/") && !entry.isDirectory) {
                        val fileName = entryName.substringAfterLast("/")
                        val destFile = File(attachmentsDir, fileName)
                        FileOutputStream(destFile).use { fileOut ->
                            zipIn.copyTo(fileOut)
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            if (jsonPayload.isNullOrBlank()) {
                return@withContext null
            }

            val snapshot = deserializeSnapshot(jsonPayload!!) ?: return@withContext null

            // 3. Atomically overwrite local tables with the restored payload
            repository.overwriteAllData(
                members = snapshot.members.map { it.toEntity() },
                creditCards = snapshot.creditCards.map { it.toEntity() },
                debitCards = snapshot.debitCards.map { it.toEntity() },
                bankAccounts = snapshot.bankAccounts.map { it.toEntity() },
                walletsAndGiftCards = snapshot.walletsAndGiftCards.map { it.toEntity() },
                documents = snapshot.documents.map { it.toEntity() },
                subscriptions = snapshot.subscriptions.map { it.toEntity() }
            )

            snapshot
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Exports a readable/restorable JSON backup of the vault
     */
    fun exportVaultToJson(snapshot: VaultBackupSnapshot): String {
        return serializeSnapshot(snapshot)
    }

    /**
     * Imports and atomically applies a JSON backup to the Room database
     */
    suspend fun importVaultFromJson(
        jsonString: String,
        repository: VaultRepository
    ): VaultBackupSnapshot? = withContext(Dispatchers.IO) {
        try {
            val snapshot = deserializeSnapshot(jsonString) ?: return@withContext null
            repository.overwriteAllData(
                members = snapshot.members.map { it.toEntity() },
                creditCards = snapshot.creditCards.map { it.toEntity() },
                debitCards = snapshot.debitCards.map { it.toEntity() },
                bankAccounts = snapshot.bankAccounts.map { it.toEntity() },
                walletsAndGiftCards = snapshot.walletsAndGiftCards.map { it.toEntity() },
                documents = snapshot.documents.map { it.toEntity() },
                subscriptions = snapshot.subscriptions.map { it.toEntity() }
            )
            snapshot
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAvailableSnapshotsCount(context: Context): Int {
        val driveFolder = getDriveAppDataDir(context)
        return driveFolder.listFiles { file ->
            file.name.startsWith("vault_backup_") && file.name.endsWith(".enc") && file.name != "vault_backup_latest.enc"
        }?.size ?: 0
    }

    fun getAvailableBackupFilesInfo(context: Context): List<DriveBackupFileInfo> {
        val driveFolder = getDriveAppDataDir(context)
        val files = driveFolder.listFiles { file ->
            file.name.startsWith("vault_backup_") && file.name.endsWith(".enc")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
        return files.map { file ->
            DriveBackupFileInfo(
                fileName = file.name,
                fileSize = file.length(),
                lastModified = file.lastModified(),
                formattedDate = sdf.format(Date(file.lastModified()))
            )
        }
    }

    suspend fun restoreSpecificBackupFromDrive(
        context: Context,
        fileName: String,
        repository: VaultRepository
    ): VaultBackupSnapshot? = withContext(Dispatchers.IO) {
        delay(750)
        try {
            val driveFolder = getDriveAppDataDir(context)
            val targetEncFile = File(driveFolder, fileName)
            if (!targetEncFile.exists()) return@withContext null

            val encryptedBytes = targetEncFile.readBytes()
            val decryptedZipBytes = CryptoManager.decrypt(encryptedBytes)

            var jsonPayload: String? = null
            val attachmentsDir = AttachmentFileManager.getAttachmentsDir(context)

            ZipInputStream(decryptedZipBytes.inputStream()).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    val entryName = entry.name
                    if (entryName == "payload.json") {
                        jsonPayload = zipIn.bufferedReader(Charsets.UTF_8).readText()
                    } else if (entryName.startsWith("attachments/") && !entry.isDirectory) {
                        val fn = entryName.substringAfterLast("/")
                        val destFile = File(attachmentsDir, fn)
                        FileOutputStream(destFile).use { fileOut ->
                            zipIn.copyTo(fileOut)
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            if (jsonPayload.isNullOrBlank()) return@withContext null

            val snapshot = deserializeSnapshot(jsonPayload!!) ?: return@withContext null

            repository.overwriteAllData(
                members = snapshot.members.map { it.toEntity() },
                creditCards = snapshot.creditCards.map { it.toEntity() },
                debitCards = snapshot.debitCards.map { it.toEntity() },
                bankAccounts = snapshot.bankAccounts.map { it.toEntity() },
                walletsAndGiftCards = snapshot.walletsAndGiftCards.map { it.toEntity() },
                documents = snapshot.documents.map { it.toEntity() },
                subscriptions = snapshot.subscriptions.map { it.toEntity() }
            )

            snapshot
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun pruneOldSnapshots(dir: File, maxCount: Int) {
        val backupFiles = dir.listFiles { file ->
            file.name.startsWith("vault_backup_") && file.name.endsWith(".enc") && file.name != "vault_backup_latest.enc"
        }?.sortedByDescending { it.lastModified() } ?: return

        if (backupFiles.size > maxCount) {
            backupFiles.drop(maxCount).forEach { file ->
                if (file.name != "vault_backup_latest.enc") {
                    file.delete()
                }
            }
        }
    }

    fun serializeSnapshot(snapshot: VaultBackupSnapshot): String {
        val root = JSONObject()
        root.put("version", snapshot.version)
        root.put("timestamp", snapshot.timestamp)

        // Members
        val membersArr = JSONArray()
        snapshot.members.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("name", m.name)
                put("relationship", m.relationship)
                put("relationshipCategory", m.relationshipCategory)
                put("customRelationship", m.customRelationship)
                put("profilePictureUri", m.profilePictureUri ?: "")
                put("colorHex", m.colorHex)
                put("isEmergencyContact", m.isEmergencyContact)
                put("emergencyPhone", m.emergencyPhone)
                put("bloodGroup", m.bloodGroup)
            }
            membersArr.put(obj)
        }
        root.put("members", membersArr)

        // Credit Cards
        val ccArr = JSONArray()
        snapshot.creditCards.forEach { cc ->
            val obj = JSONObject().apply {
                put("id", cc.id)
                put("bankName", cc.bankName)
                put("cardName", cc.cardName)
                put("network", cc.network.name)
                put("cardNumber", cc.cardNumber)
                put("expiry", cc.expiry)
                put("cvv", cc.cvv)
                put("cardholderName", cc.cardholderName)
                put("issuanceDate", cc.issuanceDate)
                put("ccRewardPoints", cc.ccRewardPoints)
                put("statementDate", cc.statementDate)
                put("dueDate", cc.dueDate)
                put("isBillPaid", cc.isBillPaid)
                put("lastPaidDate", cc.lastPaidDate)
                put("domesticPosLimit", cc.domesticPosLimit)
                put("atmDailyLimit", cc.atmDailyLimit)
                put("internationalEnabled", cc.internationalEnabled)
                put("atmPin", cc.atmPin)
                put("cardPin", cc.cardPin)
                put("remindExpiry", cc.remindExpiry)
                put("remindBillDate", cc.remindBillDate)
                put("remindDueDate", cc.remindDueDate)
                put("status", cc.status.name)
                put("memberId", cc.memberId)
                put("colorHex", cc.colorHex)
                put("linkedEmail", cc.linkedEmail)
                put("linkedPhone", cc.linkedPhone)
                put("frontCardImagePath", cc.frontCardImagePath ?: "")
                put("backCardImagePath", cc.backCardImagePath ?: "")
                put("attachmentPaths", JSONArray(cc.attachmentPaths))
            }
            ccArr.put(obj)
        }
        root.put("creditCards", ccArr)

        // Debit Cards
        val dcArr = JSONArray()
        snapshot.debitCards.forEach { dc ->
            val obj = JSONObject().apply {
                put("id", dc.id)
                put("bankName", dc.bankName)
                put("cardName", dc.cardName)
                put("network", dc.network.name)
                put("cardNumber", dc.cardNumber)
                put("expiry", dc.expiry)
                put("cvv", dc.cvv)
                put("cardholderName", dc.cardholderName)
                put("issuanceDate", dc.issuanceDate)
                put("rewardPoints", dc.rewardPoints)
                put("domesticPosLimit", dc.domesticPosLimit)
                put("atmDailyLimit", dc.atmDailyLimit)
                put("internationalEnabled", dc.internationalEnabled)
                put("atmPin", dc.atmPin)
                put("cardPin", dc.cardPin)
                put("remindExpiry", dc.remindExpiry)
                put("status", dc.status.name)
                put("memberId", dc.memberId)
                put("colorHex", dc.colorHex)
                put("linkedEmail", dc.linkedEmail)
                put("linkedPhone", dc.linkedPhone)
                put("frontCardImagePath", dc.frontCardImagePath ?: "")
                put("backCardImagePath", dc.backCardImagePath ?: "")
                put("attachmentPaths", JSONArray(dc.attachmentPaths))
            }
            dcArr.put(obj)
        }
        root.put("debitCards", dcArr)

        // Bank Accounts
        val bankArr = JSONArray()
        snapshot.bankAccounts.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("bankName", b.bankName)
                put("accountType", b.accountType)
                put("accountNumber", b.accountNumber)
                put("ifscCode", b.ifscCode)
                put("micrCode", b.micrCode)
                put("cifOrClientCode", b.cifOrClientCode ?: "")
                put("accountHolderName", b.accountHolderName)
                put("branchName", b.branchName)
                put("memberId", b.memberId)
                put("colorHex", b.colorHex)
                put("linkedEmail", b.linkedEmail)
                put("linkedPhone", b.linkedPhone)
                put("netBankingUserId", b.netBankingUserId)
                put("netBankingPassword", b.netBankingPassword)
                put("mobileBankingUserId", b.mobileBankingUserId)
                put("mobileBankingPassword", b.mobileBankingPassword)
                put("chequeBookImagePath", b.chequeBookImagePath ?: "")
                put("passbookImagePath", b.passbookImagePath ?: "")
                put("attachmentPaths", JSONArray(b.attachmentPaths))
            }
            bankArr.put(obj)
        }
        root.put("bankAccounts", bankArr)

        // Wallets & Gift Cards
        val wArr = JSONArray()
        snapshot.walletsAndGiftCards.forEach { w ->
            val obj = JSONObject().apply {
                put("id", w.id)
                put("isGiftCard", w.isGiftCard)
                put("providerOrName", w.providerOrName)
                put("cardNumberOrUpi", w.cardNumberOrUpi)
                put("giftCardPin", w.giftCardPin)
                put("vendorName", w.vendorName)
                put("remindExpiry", w.remindExpiry)
                put("amount", w.amount)
                put("initialAmount", w.initialAmount)
                put("currentBalance", w.currentBalance)
                put("expiryDate", w.expiryDate)
                put("modeOfRedemption", w.modeOfRedemption)
                put("remarks", w.remarks)
                put("kycStatus", w.kycStatus)
                put("registeredMobile", w.registeredMobile)
                put("memberId", w.memberId)
                put("colorHex", w.colorHex)
                put("barcodeOrReceiptImagePath", w.barcodeOrReceiptImagePath ?: "")
                put("attachmentPaths", JSONArray(w.attachmentPaths))
            }
            wArr.put(obj)
        }
        root.put("walletsAndGiftCards", wArr)

        // Documents
        val docArr = JSONArray()
        snapshot.documents.forEach { d ->
            val obj = JSONObject().apply {
                put("id", d.id)
                put("title", d.title)
                put("docType", d.docType.name)
                put("customDocTypeName", d.customDocTypeName ?: "")
                put("docNumber", d.docNumber)
                put("issuanceDate", d.issuanceDate ?: -1L)
                put("expiryDate", d.expiryDate ?: -1L)
                put("pdfPassword", d.pdfPassword)
                put("memberId", d.memberId)
                put("notes", d.notes ?: "")
                put("colorHex", d.colorHex)
                put("attachmentPaths", JSONArray(d.attachmentPaths))
            }
            docArr.put(obj)
        }
        root.put("documents", docArr)

        // Subscriptions
        val subArr = JSONArray()
        snapshot.subscriptions.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("name", s.name)
                put("planName", s.planName)
                put("cost", s.cost)
                put("billingCycle", s.billingCycle)
                put("nextRenewalDate", s.nextRenewalDate)
                put("linkedPaymentMethod", s.linkedPaymentMethod)
                put("memberId", s.memberId)
                put("category", s.category)
                put("colorHex", s.colorHex)
                put("notes", s.notes)
            }
            subArr.put(obj)
        }
        root.put("subscriptions", subArr)

        return root.toString(2)
    }

    private fun deserializeSnapshot(json: String): VaultBackupSnapshot? {
        return try {
            val root = JSONObject(json)
            val version = root.optInt("version", 3)
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
                            colorHex = obj.optLong("colorHex", 0xFF4F46E5),
                            isEmergencyContact = obj.optBoolean("isEmergencyContact", false),
                            emergencyPhone = obj.optString("emergencyPhone"),
                            bloodGroup = obj.optString("bloodGroup")
                        )
                    )
                }
            }

            val ccList = mutableListOf<CreditCard>()
            val ccArr = root.optJSONArray("creditCards")
            if (ccArr != null) {
                for (i in 0 until ccArr.length()) {
                    val obj = ccArr.getJSONObject(i)
                    val atts = jsonArrayToList(obj.optJSONArray("attachmentPaths"))
                    ccList.add(
                        CreditCard(
                            id = obj.optString("id"),
                            bankName = obj.optString("bankName"),
                            cardName = obj.optString("cardName"),
                            network = runCatching { CardNetwork.valueOf(obj.optString("network", "VISA")) }.getOrDefault(CardNetwork.VISA),
                            cardNumber = obj.optString("cardNumber"),
                            expiry = obj.optString("expiry"),
                            cvv = obj.optString("cvv"),
                            cardholderName = obj.optString("cardholderName"),
                            issuanceDate = obj.optString("issuanceDate"),
                            ccRewardPoints = obj.optLong("ccRewardPoints", 0L),
                            statementDate = obj.optString("statementDate"),
                            dueDate = obj.optString("dueDate"),
                            isBillPaid = obj.optBoolean("isBillPaid", false),
                            lastPaidDate = obj.optString("lastPaidDate"),
                            domesticPosLimit = obj.optLong("domesticPosLimit", 0L),
                            atmDailyLimit = obj.optLong("atmDailyLimit", 0L),
                            internationalEnabled = obj.optBoolean("internationalEnabled", false),
                            atmPin = obj.optString("atmPin"),
                            cardPin = obj.optString("cardPin"),
                            remindExpiry = obj.optBoolean("remindExpiry", true),
                            remindBillDate = obj.optBoolean("remindBillDate", true),
                            remindDueDate = obj.optBoolean("remindDueDate", true),
                            status = runCatching { CardStatus.valueOf(obj.optString("status", "ACTIVE")) }.getOrDefault(CardStatus.ACTIVE),
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFF1E293B),
                            linkedEmail = obj.optString("linkedEmail"),
                            linkedPhone = obj.optString("linkedPhone"),
                            frontCardImagePath = obj.optString("frontCardImagePath").ifBlank { null },
                            backCardImagePath = obj.optString("backCardImagePath").ifBlank { null },
                            attachmentPaths = atts
                        )
                    )
                }
            }

            val dcList = mutableListOf<DebitCard>()
            val dcArr = root.optJSONArray("debitCards")
            if (dcArr != null) {
                for (i in 0 until dcArr.length()) {
                    val obj = dcArr.getJSONObject(i)
                    val atts = jsonArrayToList(obj.optJSONArray("attachmentPaths"))
                    dcList.add(
                        DebitCard(
                            id = obj.optString("id"),
                            bankName = obj.optString("bankName"),
                            cardName = obj.optString("cardName"),
                            network = runCatching { CardNetwork.valueOf(obj.optString("network", "RUPAY")) }.getOrDefault(CardNetwork.RUPAY),
                            cardNumber = obj.optString("cardNumber"),
                            expiry = obj.optString("expiry"),
                            cvv = obj.optString("cvv"),
                            cardholderName = obj.optString("cardholderName"),
                            issuanceDate = obj.optString("issuanceDate"),
                            rewardPoints = obj.optLong("rewardPoints", 0L),
                            domesticPosLimit = obj.optLong("domesticPosLimit", 0L),
                            atmDailyLimit = obj.optLong("atmDailyLimit", 0L),
                            internationalEnabled = obj.optBoolean("internationalEnabled", false),
                            atmPin = obj.optString("atmPin"),
                            cardPin = obj.optString("cardPin"),
                            remindExpiry = obj.optBoolean("remindExpiry", true),
                            status = runCatching { CardStatus.valueOf(obj.optString("status", "ACTIVE")) }.getOrDefault(CardStatus.ACTIVE),
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFF0D5C46),
                            linkedEmail = obj.optString("linkedEmail"),
                            linkedPhone = obj.optString("linkedPhone"),
                            frontCardImagePath = obj.optString("frontCardImagePath").ifBlank { null },
                            backCardImagePath = obj.optString("backCardImagePath").ifBlank { null },
                            attachmentPaths = atts
                        )
                    )
                }
            }

            val bankList = mutableListOf<BankAccount>()
            val bankArr = root.optJSONArray("bankAccounts")
            if (bankArr != null) {
                for (i in 0 until bankArr.length()) {
                    val obj = bankArr.getJSONObject(i)
                    val atts = jsonArrayToList(obj.optJSONArray("attachmentPaths"))
                    bankList.add(
                        BankAccount(
                            id = obj.optString("id"),
                            bankName = obj.optString("bankName"),
                            accountType = obj.optString("accountType", "Savings"),
                            accountNumber = obj.optString("accountNumber"),
                            ifscCode = obj.optString("ifscCode"),
                            micrCode = obj.optString("micrCode"),
                            cifOrClientCode = obj.optString("cifOrClientCode").ifBlank { null },
                            accountHolderName = obj.optString("accountHolderName"),
                            branchName = obj.optString("branchName"),
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFF004C8F),
                            linkedEmail = obj.optString("linkedEmail"),
                            linkedPhone = obj.optString("linkedPhone"),
                            netBankingUserId = obj.optString("netBankingUserId"),
                            netBankingPassword = obj.optString("netBankingPassword"),
                            mobileBankingUserId = obj.optString("mobileBankingUserId"),
                            mobileBankingPassword = obj.optString("mobileBankingPassword"),
                            chequeBookImagePath = obj.optString("chequeBookImagePath").ifBlank { null },
                            passbookImagePath = obj.optString("passbookImagePath").ifBlank { null },
                            attachmentPaths = atts
                        )
                    )
                }
            }

            val wList = mutableListOf<WalletOrGiftCard>()
            val wArr = root.optJSONArray("walletsAndGiftCards")
            if (wArr != null) {
                for (i in 0 until wArr.length()) {
                    val obj = wArr.getJSONObject(i)
                    val atts = jsonArrayToList(obj.optJSONArray("attachmentPaths"))
                    val amt = obj.optDouble("amount", 0.0)
                    val initAmt = obj.optDouble("initialAmount", amt)
                    val currBal = obj.optDouble("currentBalance", amt)
                    wList.add(
                        WalletOrGiftCard(
                            id = obj.optString("id"),
                            isGiftCard = obj.optBoolean("isGiftCard", false),
                            providerOrName = obj.optString("providerOrName"),
                            cardNumberOrUpi = obj.optString("cardNumberOrUpi"),
                            giftCardPin = obj.optString("giftCardPin"),
                            vendorName = obj.optString("vendorName"),
                            remindExpiry = obj.optBoolean("remindExpiry", true),
                            amount = amt,
                            initialAmount = initAmt,
                            currentBalance = currBal,
                            expiryDate = obj.optString("expiryDate"),
                            modeOfRedemption = obj.optString("modeOfRedemption", "Online / App"),
                            remarks = obj.optString("remarks"),
                            kycStatus = obj.optString("kycStatus", "Full KYC Verified"),
                            registeredMobile = obj.optString("registeredMobile"),
                            memberId = obj.optString("memberId"),
                            colorHex = obj.optLong("colorHex", 0xFFB45309),
                            barcodeOrReceiptImagePath = obj.optString("barcodeOrReceiptImagePath").ifBlank { null },
                            attachmentPaths = atts
                        )
                    )
                }
            }

            val docList = mutableListOf<Document>()
            val docArr = root.optJSONArray("documents") ?: root.optJSONArray("personalDocuments")
            if (docArr != null) {
                for (i in 0 until docArr.length()) {
                    val obj = docArr.getJSONObject(i)
                    val docType = runCatching { DocType.valueOf(obj.optString("docType", obj.optString("documentType", "OTHER"))) }.getOrDefault(DocType.OTHER)
                    val atts = jsonArrayToList(obj.optJSONArray("attachmentPaths"))
                    val issueLong = obj.optLong("issuanceDate", -1L).takeIf { it > 0 }
                    val expiryLong = obj.optLong("expiryDate", -1L).takeIf { it > 0 }

                    docList.add(
                        Document(
                            id = obj.optString("id"),
                            title = obj.optString("title", obj.optString("holderName")),
                            docType = docType,
                            customDocTypeName = obj.optString("customDocTypeName", obj.optString("customDocumentName")).ifBlank { null },
                            docNumber = obj.optString("docNumber", obj.optString("documentNumber")),
                            issuanceDate = issueLong,
                            expiryDate = expiryLong,
                            pdfPassword = obj.optString("pdfPassword"),
                            memberId = obj.optString("memberId"),
                            notes = obj.optString("notes").ifBlank { null },
                            attachmentPaths = atts,
                            colorHex = obj.optLong("colorHex", docType.defaultColorHex)
                        )
                    )
                }
            }

            val subList = mutableListOf<Subscription>()
            val subArr = root.optJSONArray("subscriptions")
            if (subArr != null) {
                for (i in 0 until subArr.length()) {
                    val obj = subArr.getJSONObject(i)
                    subList.add(
                        Subscription(
                            id = obj.optString("id"),
                            name = obj.optString("name"),
                            planName = obj.optString("planName"),
                            cost = obj.optDouble("cost", 0.0),
                            billingCycle = obj.optString("billingCycle", "Monthly"),
                            nextRenewalDate = obj.optString("nextRenewalDate"),
                            linkedPaymentMethod = obj.optString("linkedPaymentMethod"),
                            memberId = obj.optString("memberId"),
                            category = obj.optString("category", "Entertainment"),
                            colorHex = obj.optLong("colorHex", 0xFF6366F1),
                            notes = obj.optString("notes")
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
                walletsAndGiftCards = wList,
                documents = docList,
                subscriptions = subList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun copyOrCompressFileToZip(file: File, zipOut: ZipOutputStream) {
        val name = file.name.lowercase()
        val isImage = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")
        if (isImage && file.length() > 200 * 1024) { // Only compress images larger than 200 KB
            try {
                val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.absolutePath, boundsOptions)
                val maxDimension = 1280
                var sampleSize = 1
                var w = boundsOptions.outWidth
                var h = boundsOptions.outHeight
                while (w > maxDimension || h > maxDimension) {
                    sampleSize *= 2
                    w /= 2
                    h /= 2
                }
                val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, zipOut)
                    bitmap.recycle()
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        FileInputStream(file).use { fileIn ->
            fileIn.copyTo(zipOut)
        }
    }

    private fun jsonArrayToList(arr: JSONArray?): List<String> {
        val list = mutableListOf<String>()
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val str = arr.optString(i)
                if (str.isNotBlank()) list.add(str)
            }
        }
        return list
    }
}

// Alias for seamless backward compatibility
typealias GoogleDriveBackupManager = DriveBackupRepository
