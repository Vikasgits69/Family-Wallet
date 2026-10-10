package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppThemeMode
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CreditCard
import com.example.data.CreditCardStatementLog
import com.example.data.DebitCard
import com.example.data.DisplayMode
import com.example.data.Document
import com.example.data.FamilyMember
import com.example.data.NavigationTab
import com.example.data.PersonalDocument
import com.example.data.WalletOrGiftCard
import com.example.data.local.AppDatabase
import com.example.data.local.BankAccountEntity
import com.example.data.local.CreditCardEntity
import com.example.data.local.DebitCardEntity
import com.example.data.local.DocumentEntity
import com.example.data.local.FamilyMemberEntity
import com.example.data.local.VaultRepository
import com.example.data.local.WalletOrGiftCardEntity
import com.example.data.toDomain
import com.example.data.toEntity
import com.example.sync.DriveBackupRepository
import com.example.sync.DriveSyncState
import com.example.sync.GoogleDriveBackupManager
import com.example.sync.DriveBackupFileInfo
import com.example.server.LocalVaultWebServer
import com.example.server.NetworkUtils
import com.example.util.NotificationHelper
import com.example.util.VaultPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

import com.example.data.Subscription
import com.example.data.local.SubscriptionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UpcomingCardAlert(
    val cardId: String,
    val bankName: String,
    val cardName: String,
    val statementDate: String,
    val dueDate: String,
    val memberId: String,
    val memberName: String,
    val colorHex: Long,
    val isDueDate: Boolean,
    val outstandingBalance: Double = 0.0,
    val rewardPoints: Long = 0L
)

data class FamilyWalletUiState(
    val members: List<FamilyMember> = emptyList(),
    val selectedMemberId: String? = null,
    val currentTab: NavigationTab = NavigationTab.DASHBOARD,
    val isMaskedGlobally: Boolean = true,
    val unmaskedItemIds: Set<String> = emptySet(),
    val flippedCardIds: Set<String> = emptySet(),
    val isDarkTheme: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val masterPin: String = "8421",
    val creditCards: List<CreditCard> = emptyList(),
    val debitCards: List<DebitCard> = emptyList(),
    val walletsAndGiftCards: List<WalletOrGiftCard> = emptyList(),
    val bankAccounts: List<BankAccount> = emptyList(),
    val documents: List<Document> = emptyList(),
    val subscriptions: List<Subscription> = emptyList(),
    val searchQuery: String = "",
    val networkFilter: CardNetwork? = null,
    val showAddCreditCardDialog: Boolean = false,
    val showAddDebitCardDialog: Boolean = false,
    val showAddAccountDialog: Boolean = false,
    val showAddWalletDialog: Boolean = false,
    val showAddMemberDialog: Boolean = false,
    val showAddDocumentDialog: Boolean = false,
    val showAddSubscriptionDialog: Boolean = false,
    val editingCreditCard: CreditCard? = null,
    val editingDebitCard: DebitCard? = null,
    val editingBankAccount: BankAccount? = null,
    val editingWalletOrGiftCard: WalletOrGiftCard? = null,
    val editingMember: FamilyMember? = null,
    val editingPersonalDocument: Document? = null,
    val editingSubscription: Subscription? = null,
    val showEmergencyIceDialog: Boolean = false,
    val showSecurityCheckupDialog: Boolean = false,
    val showHelplineDialog: Boolean = false,
    val helplineTargetBank: String = "",
    val activeUpiQrData: Pair<String, String>? = null,
    val spendingGiftCard: WalletOrGiftCard? = null,
    val loggingStatementCard: CreditCard? = null,
    val notificationMessage: String? = null,
    val isAppLocked: Boolean = true,
    val biometricStatusMessage: String? = null,
    val biometricEnabled: Boolean = true,
    val driveSync: DriveSyncState = DriveSyncState(),
    val autoBackupFrequency: String = "Daily", // "Manual", "Daily", "Weekly"
    val autoBackupOnOpen: Boolean = true,
    val autoBackupOnClose: Boolean = true,
    val includePhotosInBackup: Boolean = true,
    val availableDriveBackups: List<DriveBackupFileInfo> = emptyList(),
    val customAccentColorHex: String? = null,
    val clipboardAutoClearEnabled: Boolean = true,
    val clipboardClearTimeoutSeconds: Int = 30,
    val visualDensityMode: com.example.data.VisualDensityMode = com.example.data.VisualDensityMode.SPACIOUS,
    val cardSurfaceShader: com.example.data.CardSurfaceShader = com.example.data.CardSurfaceShader.CLASSIC_GRADIENT,
    val isWifiServerRunning: Boolean = false,
    val wifiServerUrl: String? = null,
    val wifiServerPort: Int = 8080,
    val wifiServerPin: String = "8492",
    val wifiServerRequirePin: Boolean = true,
    val wifiServerConnectedClients: Int = 0,
    val wifiServerLogs: List<String> = emptyList(),
    val showWifiServerDialog: Boolean = false,
    val wifiSsid: String = "",
    val sectionDisplayModes: Map<NavigationTab, DisplayMode> = mapOf(
        NavigationTab.DASHBOARD to DisplayMode.LIST,
        NavigationTab.CARDS to DisplayMode.LIST,
        NavigationTab.ACCOUNTS to DisplayMode.LIST,
        NavigationTab.WALLETS to DisplayMode.LIST,
        NavigationTab.DOCUMENTS to DisplayMode.LIST,
        NavigationTab.MEMBERS to DisplayMode.LIST,
        NavigationTab.SETTINGS to DisplayMode.LIST
    )
) {
    val displayMode: DisplayMode
        get() = sectionDisplayModes[currentTab] ?: DisplayMode.LIST

    fun getDisplayModeForTab(tab: NavigationTab): DisplayMode =
        sectionDisplayModes[tab] ?: DisplayMode.LIST

    val selectedMember: FamilyMember?
        get() = members.find { it.id == selectedMemberId }

    // Backward compatibility property
    val personalDocuments: List<Document>
        get() = documents

    // Global Search across ALL tables
    val filteredMembers: List<FamilyMember>
        get() = members.filter {
            searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.relationship.contains(searchQuery, ignoreCase = true) ||
                    it.customRelationship.contains(searchQuery, ignoreCase = true)
        }

    val filteredCreditCards: List<CreditCard>
        get() = creditCards
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter { networkFilter == null || it.network == networkFilter }
            .filter {
                searchQuery.isBlank() ||
                        it.bankName.contains(searchQuery, ignoreCase = true) ||
                        it.cardName.contains(searchQuery, ignoreCase = true) ||
                        it.cardholderName.contains(searchQuery, ignoreCase = true) ||
                        it.cardNumber.takeLast(4).contains(searchQuery)
            }

    val filteredDebitCards: List<DebitCard>
        get() = debitCards
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter { networkFilter == null || it.network == networkFilter }
            .filter {
                searchQuery.isBlank() ||
                        it.bankName.contains(searchQuery, ignoreCase = true) ||
                        it.cardName.contains(searchQuery, ignoreCase = true) ||
                        it.cardholderName.contains(searchQuery, ignoreCase = true) ||
                        it.cardNumber.takeLast(4).contains(searchQuery)
            }

    val filteredBankAccounts: List<BankAccount>
        get() = bankAccounts
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter {
                searchQuery.isBlank() ||
                        it.bankName.contains(searchQuery, ignoreCase = true) ||
                        it.accountHolderName.contains(searchQuery, ignoreCase = true) ||
                        it.ifscCode.contains(searchQuery, ignoreCase = true) ||
                        it.micrCode.contains(searchQuery, ignoreCase = true) ||
                        (it.cifOrClientCode?.contains(searchQuery, ignoreCase = true) == true) ||
                        it.accountNumber.takeLast(4).contains(searchQuery)
            }

    val filteredWalletsAndGiftCards: List<WalletOrGiftCard>
        get() = walletsAndGiftCards
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter {
                searchQuery.isBlank() ||
                        it.providerOrName.contains(searchQuery, ignoreCase = true) ||
                        it.cardNumberOrUpi.contains(searchQuery, ignoreCase = true) ||
                        it.remarks.contains(searchQuery, ignoreCase = true) ||
                        it.modeOfRedemption.contains(searchQuery, ignoreCase = true)
            }

    val filteredDocuments: List<Document>
        get() = documents
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter {
                searchQuery.isBlank() ||
                        it.displayTitle.contains(searchQuery, ignoreCase = true) ||
                        it.docNumber.contains(searchQuery, ignoreCase = true) ||
                        it.notes?.contains(searchQuery, ignoreCase = true) == true
            }

    val filteredPersonalDocuments: List<Document>
        get() = filteredDocuments

    val filteredSubscriptions: List<Subscription>
        get() = subscriptions
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter {
                searchQuery.isBlank() ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.planName.contains(searchQuery, ignoreCase = true) ||
                        it.linkedPaymentMethod.contains(searchQuery, ignoreCase = true) ||
                        it.notes.contains(searchQuery, ignoreCase = true)
            }

    // Informational Vault Counts
    val activeCreditCardsCount: Int
        get() = filteredCreditCards.count { it.status == CardStatus.ACTIVE }

    val activeDebitCardsCount: Int
        get() = filteredDebitCards.count { it.status == CardStatus.ACTIVE }

    val totalBankAccountsCount: Int
        get() = filteredBankAccounts.size

    val totalWalletsCount: Int
        get() = filteredWalletsAndGiftCards.count { !it.isGiftCard }

    val totalGiftCardsCount: Int
        get() = filteredWalletsAndGiftCards.count { it.isGiftCard && !it.isMarkedAsUsed }

    val activeWalletsAndGiftCards: List<WalletOrGiftCard>
        get() = filteredWalletsAndGiftCards.filter { !it.isGiftCard || !it.isMarkedAsUsed }

    val totalDocumentsCount: Int
        get() = filteredDocuments.size

    val totalPersonalDocumentsCount: Int
        get() = totalDocumentsCount

    val totalVaultAssetsCount: Int
        get() = filteredCreditCards.size + filteredDebitCards.size + filteredBankAccounts.size + activeWalletsAndGiftCards.size + filteredDocuments.size

    // Dynamic Masking Logic:
    // Masked if globally masked AND NOT individually toggled to unmasked
    fun isItemUnmasked(itemId: String): Boolean {
        return if (isMaskedGlobally) {
            unmaskedItemIds.contains(itemId)
        } else {
            !unmaskedItemIds.contains(itemId)
        }
    }

    fun isCardFlipped(cardId: String): Boolean = flippedCardIds.contains(cardId)

    // Bill & Due Date Notifications for the next 7 days
    val upcomingAlerts: List<UpcomingCardAlert>
        get() {
            val alerts = mutableListOf<UpcomingCardAlert>()
            creditCards.forEach { card ->
                val member = members.find { it.id == card.memberId }
                val memberName = member?.name ?: "Personal"
                if (card.statementDate.isNotBlank() && card.remindBillDate) {
                    alerts.add(
                        UpcomingCardAlert(
                            cardId = card.id,
                            bankName = card.bankName,
                            cardName = card.cardName,
                            statementDate = card.statementDate,
                            dueDate = card.dueDate,
                            memberId = card.memberId,
                            memberName = memberName,
                            colorHex = card.colorHex,
                            isDueDate = false,
                            outstandingBalance = card.currentOutstandingBalance,
                            rewardPoints = card.currentEffectiveRewardPoints
                        )
                    )
                }
                if (card.dueDate.isNotBlank() && card.remindDueDate) {
                    alerts.add(
                        UpcomingCardAlert(
                            cardId = card.id,
                            bankName = card.bankName,
                            cardName = card.cardName,
                            statementDate = card.statementDate,
                            dueDate = card.dueDate,
                            memberId = card.memberId,
                            memberName = memberName,
                            colorHex = card.colorHex,
                            isDueDate = true,
                            outstandingBalance = card.currentOutstandingBalance,
                            rewardPoints = card.currentEffectiveRewardPoints
                        )
                    )
                }
            }
            return alerts
        }
}

class FamilyWalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VaultRepository
    private val preferencesManager = VaultPreferencesManager(application)
    private var webServer: LocalVaultWebServer? = null

    private val _uiState = MutableStateFlow(FamilyWalletUiState())
    val uiState: StateFlow<FamilyWalletUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VaultRepository(database.familyWalletDao())

        // Load saved preferences (Master PIN, Dark Theme, Theme Mode, Biometric, Custom Accent, Section View Modes)
        val savedThemeMode = preferencesManager.getThemeMode()

        // Initialize Android system notification channels and reminder scheduler
        NotificationHelper.createNotificationChannels(application)
        NotificationHelper.scheduleDailyReminderAlarm(application)
        val savedTheme = preferencesManager.isDarkTheme()
        val savedCustomAccent = preferencesManager.getCustomAccentColor()
        val savedPin = preferencesManager.getMasterPin()
        val savedBiometric = preferencesManager.isBiometricEnabled()
        val savedSectionModes = preferencesManager.getAllSectionDisplayModes()
        val savedAutoFrequency = preferencesManager.getAutoBackupFrequency()
        val savedAutoOpen = preferencesManager.isAutoBackupOnOpen()
        val savedAutoClose = preferencesManager.isAutoBackupOnClose()
        val savedIncludePhotos = preferencesManager.isIncludePhotosInBackup()
        val savedClipboardAutoClear = preferencesManager.isClipboardAutoClearEnabled()
        val savedClipboardTimeout = preferencesManager.getClipboardClearTimeout()
        val savedDensity = preferencesManager.getVisualDensityMode()
        val savedShader = preferencesManager.getCardSurfaceShader()
        val savedWifiPort = preferencesManager.getWifiServerPort()
        val savedWifiRequirePin = preferencesManager.isWifiServerRequirePin()
        val savedWifiPin = preferencesManager.getWifiServerPin()
        val currentSsid = NetworkUtils.getWifiName(application)

        _uiState.update {
            it.copy(
                isDarkTheme = savedTheme,
                themeMode = savedThemeMode,
                customAccentColorHex = savedCustomAccent,
                masterPin = savedPin,
                biometricEnabled = savedBiometric,
                sectionDisplayModes = savedSectionModes,
                autoBackupFrequency = savedAutoFrequency,
                autoBackupOnOpen = savedAutoOpen,
                autoBackupOnClose = savedAutoClose,
                includePhotosInBackup = savedIncludePhotos,
                clipboardAutoClearEnabled = savedClipboardAutoClear,
                clipboardClearTimeoutSeconds = savedClipboardTimeout,
                visualDensityMode = savedDensity,
                cardSurfaceShader = savedShader,
                wifiServerPort = savedWifiPort,
                wifiServerRequirePin = savedWifiRequirePin,
                wifiServerPin = savedWifiPin,
                wifiSsid = currentSsid
            )
        }

        // Collect reactive flows from Room repository into UI State
        viewModelScope.launch {
            combine(
                repository.allMembers,
                repository.allCreditCards,
                repository.allDebitCards,
                repository.allBankAccounts,
                repository.allWalletsAndGiftCards,
                repository.allDocuments,
                repository.allSubscriptions
            ) { array ->
                @Suppress("UNCHECKED_CAST")
                val members = array[0] as List<FamilyMemberEntity>
                @Suppress("UNCHECKED_CAST")
                val creditCards = array[1] as List<CreditCardEntity>
                @Suppress("UNCHECKED_CAST")
                val debitCards = array[2] as List<DebitCardEntity>
                @Suppress("UNCHECKED_CAST")
                val bankAccounts = array[3] as List<BankAccountEntity>
                @Suppress("UNCHECKED_CAST")
                val walletsAndGifts = array[4] as List<WalletOrGiftCardEntity>
                @Suppress("UNCHECKED_CAST")
                val docs = array[5] as List<DocumentEntity>
                @Suppress("UNCHECKED_CAST")
                val subs = array[6] as List<SubscriptionEntity>

                _uiState.update { current ->
                    current.copy(
                        members = members.map { it.toDomain() },
                        creditCards = creditCards.map { it.toDomain() },
                        debitCards = debitCards.map { it.toDomain() },
                        bankAccounts = bankAccounts.map { it.toDomain() },
                        walletsAndGiftCards = walletsAndGifts.map { it.toDomain() },
                        documents = docs.map { it.toDomain() },
                        subscriptions = subs.map { it.toDomain() }
                    )
                }
                webServer?.notifyDataChanged()
                try {
                    NotificationHelper.checkAndTriggerDueReminders(
                        context = getApplication(),
                        creditCards = creditCards.map { it.toDomain() },
                        wallets = walletsAndGifts.map { it.toDomain() },
                        documents = docs.map { it.toDomain() }
                    )
                } catch (e: Exception) {
                    // Ignore non-fatal reminder background check errors
                }
            }.collect {}
        }
    }

    fun setSelectedMember(memberId: String?) {
        _uiState.update { it.copy(selectedMemberId = memberId) }
    }

    fun setNavigationTab(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    // Section-Specific View Toggles (List, Grid, Carousel)
    fun setDisplayMode(mode: DisplayMode) {
        val tab = _uiState.value.currentTab
        _uiState.update { current ->
            val updatedMap = current.sectionDisplayModes.toMutableMap()
            updatedMap[tab] = mode
            current.copy(sectionDisplayModes = updatedMap)
        }
        preferencesManager.saveSectionDisplayMode(tab, mode)
    }

    fun setDisplayModeForTab(tab: NavigationTab, mode: DisplayMode) {
        _uiState.update { current ->
            val updatedMap = current.sectionDisplayModes.toMutableMap()
            updatedMap[tab] = mode
            current.copy(sectionDisplayModes = updatedMap)
        }
        preferencesManager.saveSectionDisplayMode(tab, mode)
    }

    fun setCustomAccentColor(hex: String?) {
        _uiState.update { it.copy(customAccentColorHex = hex) }
        preferencesManager.saveCustomAccentColor(hex)
    }

    // Global Search
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setNetworkFilter(network: CardNetwork?) {
        _uiState.update { it.copy(networkFilter = network) }
    }

    fun toggleCardFlip(cardId: String) {
        _uiState.update { state ->
            val updated = if (state.flippedCardIds.contains(cardId)) {
                state.flippedCardIds - cardId
            } else {
                state.flippedCardIds + cardId
            }
            state.copy(flippedCardIds = updated)
        }
    }

    // Interactive Mask / Unmask Logic (CVV, Card Numbers, Account Numbers)
    fun toggleGlobalMask() {
        _uiState.update { state ->
            state.copy(
                isMaskedGlobally = !state.isMaskedGlobally,
                unmaskedItemIds = emptySet()
            )
        }
    }

    fun toggleItemMask(itemId: String) {
        _uiState.update { state ->
            val unmasked = state.unmaskedItemIds.toMutableSet()
            if (unmasked.contains(itemId)) {
                unmasked.remove(itemId)
            } else {
                unmasked.add(itemId)
            }
            state.copy(unmaskedItemIds = unmasked)
        }
    }

    // Theme toggles
    fun toggleDarkTheme() {
        val next = !_uiState.value.isDarkTheme
        _uiState.update { it.copy(isDarkTheme = next) }
        preferencesManager.saveDarkTheme(next)
    }

    fun toggleTheme() = toggleDarkTheme()

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.update {
            it.copy(
                themeMode = mode,
                isDarkTheme = if (mode == AppThemeMode.SYSTEM) it.isDarkTheme else mode.isDark
            )
        }
        preferencesManager.saveThemeMode(mode)
    }

    // Biometrics & PIN Security
    fun setMasterPin(newPin: String) {
        if (newPin.length == 4) {
            _uiState.update { it.copy(masterPin = newPin, notificationMessage = "Master PIN successfully updated") }
            preferencesManager.saveMasterPin(newPin)
        }
    }

    fun verifyPin(pin: String): Boolean {
        return pin == _uiState.value.masterPin
    }

    fun toggleBiometricEnabled() {
        val current = _uiState.value.biometricEnabled
        val next = !current
        _uiState.update { it.copy(biometricEnabled = next) }
        preferencesManager.saveBiometricEnabled(next)
    }

    fun toggleClipboardAutoClear() {
        val next = !_uiState.value.clipboardAutoClearEnabled
        _uiState.update { it.copy(clipboardAutoClearEnabled = next) }
        preferencesManager.saveClipboardAutoClearEnabled(next)
    }

    fun setClipboardClearTimeout(seconds: Int) {
        _uiState.update { it.copy(clipboardClearTimeoutSeconds = seconds) }
        preferencesManager.saveClipboardClearTimeout(seconds)
    }

    fun setVisualDensityMode(mode: com.example.data.VisualDensityMode) {
        _uiState.update { it.copy(visualDensityMode = mode) }
        preferencesManager.saveVisualDensityMode(mode)
    }

    fun setCardSurfaceShader(shader: com.example.data.CardSurfaceShader) {
        _uiState.update { it.copy(cardSurfaceShader = shader) }
        preferencesManager.saveCardSurfaceShader(shader)
    }

    fun setBiometricStatusMessage(msg: String?) {
        _uiState.update { it.copy(biometricStatusMessage = msg) }
    }

    // Dialog state management
    fun setAddCreditCardDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddCreditCardDialog = visible, editingCreditCard = if (!visible) null else it.editingCreditCard) }
    }

    fun openEditCreditCard(card: CreditCard) {
        _uiState.update { it.copy(showAddCreditCardDialog = true, editingCreditCard = card) }
    }

    fun setAddDebitCardDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddDebitCardDialog = visible, editingDebitCard = if (!visible) null else it.editingDebitCard) }
    }

    fun openEditDebitCard(card: DebitCard) {
        _uiState.update { it.copy(showAddDebitCardDialog = true, editingDebitCard = card) }
    }

    fun setAddAccountDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddAccountDialog = visible, editingBankAccount = if (!visible) null else it.editingBankAccount) }
    }

    fun openEditBankAccount(account: BankAccount) {
        _uiState.update { it.copy(showAddAccountDialog = true, editingBankAccount = account) }
    }

    fun setAddWalletDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddWalletDialog = visible, editingWalletOrGiftCard = if (!visible) null else it.editingWalletOrGiftCard) }
    }

    fun openEditWalletOrGiftCard(item: WalletOrGiftCard) {
        _uiState.update { it.copy(showAddWalletDialog = true, editingWalletOrGiftCard = item) }
    }

    fun setAddMemberDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddMemberDialog = visible, editingMember = if (!visible) null else it.editingMember) }
    }

    fun openEditMember(member: FamilyMember) {
        _uiState.update { it.copy(showAddMemberDialog = true, editingMember = member) }
    }

    fun setAddDocumentDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddDocumentDialog = visible, editingPersonalDocument = if (!visible) null else it.editingPersonalDocument) }
    }

    fun openEditPersonalDocument(doc: Document) {
        _uiState.update { it.copy(showAddDocumentDialog = true, editingPersonalDocument = doc) }
    }

    // CRUD: Credit Cards (Persisted in Room)
    fun saveCreditCard(card: CreditCard) {
        viewModelScope.launch {
            try {
                if (_uiState.value.creditCards.any { it.id == card.id }) {
                    repository.updateCreditCard(card.toEntity())
                    _uiState.update {
                        it.copy(
                            showAddCreditCardDialog = false,
                            showAddDebitCardDialog = false,
                            editingCreditCard = null,
                            editingDebitCard = null,
                            notificationMessage = "Updated ${card.cardName}"
                        )
                    }
                } else {
                    repository.insertCreditCard(card.toEntity())
                    _uiState.update {
                        it.copy(
                            showAddCreditCardDialog = false,
                            showAddDebitCardDialog = false,
                            editingCreditCard = null,
                            editingDebitCard = null,
                            notificationMessage = "Saved ${card.cardName} to vault"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(notificationMessage = "Failed to save card: ${e.message}") }
            }
        }
    }

    fun deleteCreditCard(cardId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCreditCard(cardId)
                _uiState.update { it.copy(notificationMessage = "Credit card removed from vault") }
            } catch (e: Exception) {
                _uiState.update { it.copy(notificationMessage = "Failed to remove card") }
            }
        }
    }

    // CRUD: Debit Cards (Persisted in Room)
    fun saveDebitCard(card: DebitCard) {
        viewModelScope.launch {
            try {
                if (_uiState.value.debitCards.any { it.id == card.id }) {
                    repository.updateDebitCard(card.toEntity())
                    _uiState.update {
                        it.copy(
                            showAddDebitCardDialog = false,
                            showAddCreditCardDialog = false,
                            editingDebitCard = null,
                            editingCreditCard = null,
                            notificationMessage = "Updated ${card.cardName}"
                        )
                    }
                } else {
                    repository.insertDebitCard(card.toEntity())
                    _uiState.update {
                        it.copy(
                            showAddDebitCardDialog = false,
                            showAddCreditCardDialog = false,
                            editingDebitCard = null,
                            editingCreditCard = null,
                            notificationMessage = "Saved ${card.cardName} to vault"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(notificationMessage = "Failed to save card: ${e.message}") }
            }
        }
    }

    fun deleteDebitCard(cardId: String) {
        viewModelScope.launch {
            try {
                repository.deleteDebitCard(cardId)
                _uiState.update { it.copy(notificationMessage = "Debit card removed from vault") }
            } catch (e: Exception) {
                _uiState.update { it.copy(notificationMessage = "Failed to remove card") }
            }
        }
    }

    // CRUD: Bank Accounts (Persisted in Room with Color Accent selection)
    fun saveBankAccount(account: BankAccount) {
        viewModelScope.launch {
            if (_uiState.value.bankAccounts.any { it.id == account.id }) {
                repository.updateBankAccount(account.toEntity())
                _uiState.update { it.copy(showAddAccountDialog = false, editingBankAccount = null, notificationMessage = "Updated ${account.bankName} Account") }
            } else {
                repository.insertBankAccount(account.toEntity())
                _uiState.update { it.copy(showAddAccountDialog = false, editingBankAccount = null, notificationMessage = "Saved ${account.bankName} Account to vault") }
            }
        }
    }

    fun deleteBankAccount(accountId: String) {
        viewModelScope.launch {
            repository.deleteBankAccount(accountId)
            _uiState.update { it.copy(notificationMessage = "Bank account removed from vault") }
        }
    }

    // CRUD: Wallets & Gift Cards (Combined section, Persisted in Room)
    fun saveWalletOrGiftCard(item: WalletOrGiftCard) {
        viewModelScope.launch {
            if (_uiState.value.walletsAndGiftCards.any { it.id == item.id }) {
                repository.updateWalletOrGiftCard(item.toEntity())
                val label = if (item.isGiftCard) "Gift Card" else "Wallet"
                _uiState.update { it.copy(showAddWalletDialog = false, editingWalletOrGiftCard = null, notificationMessage = "Updated $label ${item.providerOrName}") }
            } else {
                repository.insertWalletOrGiftCard(item.toEntity())
                val label = if (item.isGiftCard) "Gift Card" else "Wallet"
                _uiState.update { it.copy(showAddWalletDialog = false, editingWalletOrGiftCard = null, notificationMessage = "Saved $label ${item.providerOrName} to vault") }
            }
        }
    }

    fun deleteWalletOrGiftCard(itemId: String) {
        viewModelScope.launch {
            repository.deleteWalletOrGiftCard(itemId)
            _uiState.update { it.copy(notificationMessage = "Item removed from vault") }
        }
    }

    // CRUD: Family Members (Persisted in Room, with image Uri, vector category, custom relationship)
    fun saveFamilyMember(member: FamilyMember) {
        viewModelScope.launch {
            if (_uiState.value.members.any { it.id == member.id }) {
                repository.updateMember(member.toEntity())
                _uiState.update { it.copy(showAddMemberDialog = false, editingMember = null, notificationMessage = "Updated profile for ${member.name}") }
            } else {
                repository.insertMember(member.toEntity())
                _uiState.update { it.copy(showAddMemberDialog = false, editingMember = null, notificationMessage = "Added ${member.name} to Family Vault") }
            }
        }
    }

    fun deleteFamilyMember(memberId: String) {
        viewModelScope.launch {
            val memberName = _uiState.value.members.find { it.id == memberId }?.name ?: "Member"
            repository.deleteMember(memberId)
            _uiState.update {
                it.copy(
                    selectedMemberId = if (it.selectedMemberId == memberId) null else it.selectedMemberId,
                    notificationMessage = "Deleted $memberName from Family Vault"
                )
            }
        }
    }

    // CRUD: Personal Documents (Persisted in Room)
    fun saveDocument(doc: Document) {
        viewModelScope.launch {
            try {
                if (_uiState.value.documents.any { it.id == doc.id }) {
                    repository.updateDocument(doc.toEntity())
                    _uiState.update {
                        it.copy(
                            showAddDocumentDialog = false,
                            editingPersonalDocument = null,
                            notificationMessage = "Updated ${doc.displayTitle}"
                        )
                    }
                } else {
                    repository.insertDocument(doc.toEntity())
                    _uiState.update {
                        it.copy(
                            showAddDocumentDialog = false,
                            editingPersonalDocument = null,
                            notificationMessage = "Saved ${doc.displayTitle} to vault"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(notificationMessage = "Failed to save document: ${e.message}") }
            }
        }
    }

    fun savePersonalDocument(doc: Document) = saveDocument(doc)

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            val doc = _uiState.value.documents.find { it.id == docId }
            repository.deleteDocument(docId)
            _uiState.update { it.copy(notificationMessage = "Removed ${doc?.displayTitle ?: "Document"} from vault") }
        }
    }

    fun deletePersonalDocument(docId: String) = deleteDocument(docId)

    fun lockApp() {
        _uiState.update { it.copy(isAppLocked = true) }
    }

    fun unlockApp() {
        _uiState.update { it.copy(isAppLocked = false) }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    fun syncWithGoogleDrive() {
        viewModelScope.launch {
            _uiState.update { it.copy(driveSync = it.driveSync.copy(isSyncing = true)) }
            val current = _uiState.value
            val snapshot = DriveBackupRepository.backupVaultToDrive(
                context = getApplication(),
                members = current.members,
                creditCards = current.creditCards,
                debitCards = current.debitCards,
                bankAccounts = current.bankAccounts,
                walletsAndGiftCards = current.walletsAndGiftCards,
                documents = current.documents,
                subscriptions = current.subscriptions,
                includePhotos = current.includePhotosInBackup
            )
            val photoNote = if (current.includePhotosInBackup) " (photos included)" else " (photos excluded)"
            _uiState.update {
                it.copy(
                    driveSync = DriveSyncState(
                        isSyncing = false,
                        isRestoring = false,
                        lastSyncTimestamp = snapshot.timestamp,
                        lastSyncTime = snapshot.timestamp,
                        backupFileName = "vault_backup_${System.currentTimeMillis()}.enc",
                        isConfigured = true,
                        lastSyncStatus = "Backed up ${snapshot.members.size} members, ${snapshot.creditCards.size + snapshot.debitCards.size} cards, ${snapshot.bankAccounts.size} accounts, ${snapshot.documents.size} docs$photoNote",
                        lastBackupMemberCount = snapshot.members.size,
                        lastBackupCardCount = snapshot.creditCards.size + snapshot.debitCards.size,
                        lastBackupBankCount = snapshot.bankAccounts.size,
                        lastBackupWalletCount = snapshot.walletsAndGiftCards.size,
                        lastBackupDocumentCount = snapshot.documents.size,
                        lastBackupAttachmentCount = snapshot.attachmentFileNames.size
                    ),
                    notificationMessage = "Vault encrypted and backed up to Google Drive$photoNote (${snapshot.timestamp})"
                )
            }
        }
    }

    fun restoreFromGoogleDrive() {
        viewModelScope.launch {
            _uiState.update { it.copy(driveSync = it.driveSync.copy(isRestoring = true)) }
            val restoredSnapshot = DriveBackupRepository.restoreVaultFromDrive(
                context = getApplication(),
                repository = repository
            )
            if (restoredSnapshot != null) {
                _uiState.update {
                    it.copy(
                        driveSync = it.driveSync.copy(
                            isRestoring = false,
                            lastSyncStatus = "Restored ${restoredSnapshot.members.size} members, ${restoredSnapshot.creditCards.size + restoredSnapshot.debitCards.size} cards, ${restoredSnapshot.bankAccounts.size} accounts, ${restoredSnapshot.documents.size} docs"
                        ),
                        notificationMessage = "Successfully restored vault from Google Drive (${restoredSnapshot.timestamp})"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        driveSync = it.driveSync.copy(
                            isRestoring = false,
                            lastSyncStatus = "No Google Drive backup file found"
                        ),
                        notificationMessage = "No backup found on Google Drive to restore."
                    )
                }
            }
        }
    }

    fun loadAvailableDriveBackups() {
        viewModelScope.launch {
            val list = DriveBackupRepository.getAvailableBackupFilesInfo(getApplication())
            _uiState.update { it.copy(availableDriveBackups = list) }
        }
    }

    fun restoreSpecificDriveBackup(fileName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(driveSync = it.driveSync.copy(isRestoring = true)) }
            val restoredSnapshot = DriveBackupRepository.restoreSpecificBackupFromDrive(
                context = getApplication(),
                fileName = fileName,
                repository = repository
            )
            if (restoredSnapshot != null) {
                _uiState.update {
                    it.copy(
                        driveSync = it.driveSync.copy(
                            isRestoring = false,
                            lastSyncStatus = "Restored backup ($fileName)"
                        ),
                        notificationMessage = "Successfully restored backup: $fileName"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        driveSync = it.driveSync.copy(
                            isRestoring = false,
                            lastSyncStatus = "Failed to restore backup $fileName"
                        ),
                        notificationMessage = "Failed to restore backup $fileName"
                    )
                }
            }
        }
    }

    fun updateAutoBackupSettings(frequency: String, onOpen: Boolean, onClose: Boolean) {
        _uiState.update { it.copy(autoBackupFrequency = frequency, autoBackupOnOpen = onOpen, autoBackupOnClose = onClose) }
        preferencesManager.saveAutoBackupFrequency(frequency)
        preferencesManager.saveAutoBackupOnOpen(onOpen)
        preferencesManager.saveAutoBackupOnClose(onClose)
    }

    fun setIncludePhotosInBackup(include: Boolean) {
        _uiState.update { it.copy(includePhotosInBackup = include) }
        preferencesManager.saveIncludePhotosInBackup(include)
    }

    fun performAutoBackupOnOpen() {
        if (_uiState.value.autoBackupOnOpen) {
            syncWithGoogleDrive()
        }
    }

    fun performAutoBackupOnClose() {
        if (_uiState.value.autoBackupOnClose) {
            viewModelScope.launch {
                val current = _uiState.value
                DriveBackupRepository.backupVaultToDrive(
                    context = getApplication(),
                    members = current.members,
                    creditCards = current.creditCards,
                    debitCards = current.debitCards,
                    bankAccounts = current.bankAccounts,
                    walletsAndGiftCards = current.walletsAndGiftCards,
                    documents = current.documents,
                    subscriptions = current.subscriptions,
                    includePhotos = current.includePhotosInBackup
                )
            }
        }
    }

    // Subscriptions CRUD
    fun openAddSubscription() {
        _uiState.update { it.copy(showAddSubscriptionDialog = true, editingSubscription = null) }
    }

    fun openEditSubscription(sub: Subscription) {
        _uiState.update { it.copy(showAddSubscriptionDialog = true, editingSubscription = sub) }
    }

    fun closeSubscriptionDialog() {
        _uiState.update { it.copy(showAddSubscriptionDialog = false, editingSubscription = null) }
    }

    fun saveSubscription(sub: Subscription) {
        viewModelScope.launch {
            repository.insertSubscription(sub.toEntity())
            _uiState.update {
                it.copy(
                    showAddSubscriptionDialog = false,
                    editingSubscription = null,
                    notificationMessage = "Saved ${sub.name} subscription"
                )
            }
        }
    }

    fun deleteSubscription(subId: String) {
        viewModelScope.launch {
            val sub = _uiState.value.subscriptions.find { it.id == subId }
            repository.deleteSubscription(subId)
            _uiState.update { it.copy(notificationMessage = "Removed ${sub?.name ?: "Subscription"}") }
        }
    }

    // Credit Card Bill Tracker
    fun toggleCreditCardBillPaid(cardId: String) {
        viewModelScope.launch {
            val card = _uiState.value.creditCards.find { it.id == cardId } ?: return@launch
            val nowPaid = !card.isBillPaid
            val todayStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date())
            val updated = card.copy(
                isBillPaid = nowPaid,
                lastPaidDate = if (nowPaid) todayStr else ""
            )
            repository.updateCreditCard(updated.toEntity())
            _uiState.update {
                it.copy(notificationMessage = if (nowPaid) "Marked ${card.cardName} bill as Paid ✓" else "Marked ${card.cardName} bill as Unpaid")
            }
        }
    }

    // Gift Card Balance Spend/Deduct
    fun deductGiftCardBalance(cardId: String, spentAmount: Double, remarks: String = "") {
        viewModelScope.launch {
            val card = _uiState.value.walletsAndGiftCards.find { it.id == cardId } ?: return@launch
            val initialAmt = if (card.initialAmount > 0.0) card.initialAmount else (if (card.amount > 0.0) card.amount else card.currentBalance)
            val newBalance = (card.currentBalance - spentAmount).coerceAtLeast(0.0)
            val updatedRemarks = if (remarks.isNotBlank()) {
                val entry = "Spent ₹${spentAmount.toInt()}: $remarks"
                if (card.remarks.isNotBlank()) "${card.remarks}\n$entry" else entry
            } else card.remarks
            val updated = card.copy(
                amount = newBalance,
                currentBalance = newBalance,
                initialAmount = initialAmt,
                remarks = updatedRemarks,
                isMarkedAsUsed = if (newBalance <= 0.0) true else card.isMarkedAsUsed
            )
            repository.updateWalletOrGiftCard(updated.toEntity())
            _uiState.update {
                it.copy(
                    spendingGiftCard = null,
                    notificationMessage = "Deducted ₹${spentAmount.toInt()} from ${card.providerOrName}. Remaining Balance: ₹${newBalance.toInt()}"
                )
            }
        }
    }

    // Toggle Gift Card Marked as Used
    fun toggleGiftCardMarkedAsUsed(cardId: String) {
        viewModelScope.launch {
            val card = _uiState.value.walletsAndGiftCards.find { it.id == cardId } ?: return@launch
            val newMarked = !card.isMarkedAsUsed
            val initialAmt = if (card.initialAmount > 0.0) card.initialAmount else (if (card.amount > 0.0) card.amount else card.currentBalance)
            val updated = card.copy(
                isMarkedAsUsed = newMarked,
                currentBalance = if (newMarked) 0.0 else (if (card.currentBalance > 0.0) card.currentBalance else initialAmt),
                amount = if (newMarked) 0.0 else (if (card.currentBalance > 0.0) card.currentBalance else initialAmt),
                initialAmount = initialAmt
            )
            repository.updateWalletOrGiftCard(updated.toEntity())
            _uiState.update {
                it.copy(
                    notificationMessage = if (updated.isMarkedAsUsed)
                        "Marked ${card.providerOrName} as Used (hidden from dashboard)"
                    else
                        "Marked ${card.providerOrName} as Active"
                )
            }
        }
    }

    // Statement Cycle Management for Credit Card
    fun openLogStatement(card: CreditCard) {
        _uiState.update { it.copy(loggingStatementCard = card) }
    }

    fun closeLogStatement() {
        _uiState.update { it.copy(loggingStatementCard = null) }
    }

    fun saveCreditCardStatementLog(cardId: String, log: CreditCardStatementLog) {
        viewModelScope.launch {
            val card = _uiState.value.creditCards.find { it.id == cardId } ?: return@launch
            val existingLogs = card.parsedStatementLogs.filter { it.id != log.id }
            val updatedLogs = (listOf(log) + existingLogs).sortedByDescending { it.timestamp }

            val jsonArray = org.json.JSONArray()
            updatedLogs.forEach { item ->
                val obj = org.json.JSONObject().apply {
                    put("id", item.id)
                    put("statementMonth", item.statementMonth)
                    put("openingBalance", item.openingBalance)
                    put("totalExpenses", item.totalExpenses)
                    put("totalPayments", item.totalPayments)
                    put("closingBalance", item.closingBalance)
                    put("openingRewardPoints", item.openingRewardPoints)
                    put("rewardPointsEarned", item.rewardPointsEarned)
                    put("rewardPointsRedeemedOrLapsed", item.rewardPointsRedeemedOrLapsed)
                    put("closingRewardPoints", item.closingRewardPoints)
                    put("notes", item.notes)
                    put("timestamp", item.timestamp)
                }
                jsonArray.put(obj)
            }

            val updatedCard = card.copy(
                currentStatementMonth = log.statementMonth,
                statementOpeningBalance = log.openingBalance,
                statementTotalExpenses = log.totalExpenses,
                statementTotalPayments = log.totalPayments,
                statementClosingBalance = log.closingBalance,
                statementOpeningRewardPoints = log.openingRewardPoints,
                statementRewardPointsEarned = log.rewardPointsEarned,
                statementRewardPointsRedeemed = log.rewardPointsRedeemedOrLapsed,
                statementClosingRewardPoints = log.closingRewardPoints,
                ccRewardPoints = log.closingRewardPoints,
                statementLogsJson = jsonArray.toString()
            )

            repository.updateCreditCard(updatedCard.toEntity())
            _uiState.update {
                it.copy(
                    loggingStatementCard = null,
                    notificationMessage = "Logged ${log.statementMonth} statement: Balance ₹${log.closingBalance.toInt()}, Reward Points: ${log.closingRewardPoints} pts"
                )
            }
        }
    }

    // Notifications & Reminders Actions
    fun triggerTestNotification() {
        NotificationHelper.sendTestNotification(getApplication())
        _uiState.update { it.copy(notificationMessage = "Test notification sent! Check your notification shade 🔔") }
    }

    fun scanAndTriggerDueReminders() {
        val current = _uiState.value
        val count = NotificationHelper.checkAndTriggerDueReminders(
            context = getApplication(),
            creditCards = current.creditCards,
            wallets = current.walletsAndGiftCards,
            documents = current.documents
        )
        _uiState.update {
            it.copy(
                notificationMessage = if (count > 0)
                    "Dispatched $count due date & expiry reminder notification(s) 🔔"
                else
                    "Scanned all cards & docs: No upcoming due dates or expiries today ✓"
            )
        }
    }

    // UPI QR Code Generator
    fun showUpiQr(vpa: String, payeeName: String) {
        _uiState.update { it.copy(activeUpiQrData = Pair(vpa, payeeName)) }
    }

    fun closeUpiQr() {
        _uiState.update { it.copy(activeUpiQrData = null) }
    }

    // Emergency Helplines Sheet
    fun openHelplines(bankName: String = "") {
        _uiState.update { it.copy(showHelplineDialog = true, helplineTargetBank = bankName) }
    }

    fun closeHelplines() {
        _uiState.update { it.copy(showHelplineDialog = false, helplineTargetBank = "") }
    }

    // Emergency ICE Dialog
    fun openEmergencyIce() {
        _uiState.update { it.copy(showEmergencyIceDialog = true) }
    }

    fun closeEmergencyIce() {
        _uiState.update { it.copy(showEmergencyIceDialog = false) }
    }

    // Vault Security Checkup Dialog
    fun openSecurityCheckup() {
        _uiState.update { it.copy(showSecurityCheckupDialog = true) }
    }

    fun closeSecurityCheckup() {
        _uiState.update { it.copy(showSecurityCheckupDialog = false) }
    }

    // Gift Card Spend Dialog
    fun openSpendGiftCard(item: WalletOrGiftCard) {
        _uiState.update { it.copy(spendingGiftCard = item) }
    }

    fun closeSpendGiftCard() {
        _uiState.update { it.copy(spendingGiftCard = null) }
    }

    // Encrypted Local JSON Export / Import
    fun exportVaultJson(onSuccess: (String) -> Unit) {
        val current = _uiState.value
        val snapshot = com.example.sync.VaultBackupSnapshot(
            version = 3,
            timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
            members = current.members,
            creditCards = current.creditCards,
            debitCards = current.debitCards,
            bankAccounts = current.bankAccounts,
            walletsAndGiftCards = current.walletsAndGiftCards,
            documents = current.documents,
            subscriptions = current.subscriptions
        )
        val json = DriveBackupRepository.exportVaultToJson(snapshot)
        onSuccess(json)
    }

    fun importVaultJson(json: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val restored = DriveBackupRepository.importVaultFromJson(json, repository)
            if (restored != null) {
                _uiState.update {
                    it.copy(notificationMessage = "Successfully restored vault backup (${restored.timestamp})")
                }
                onComplete(true)
            } else {
                _uiState.update {
                    it.copy(notificationMessage = "Failed to restore backup: Invalid file format")
                }
                onComplete(false)
            }
        }
    }

    // ==================== WI-FI WEB SERVER MANAGEMENT ====================

    fun setWifiServerDialogVisible(visible: Boolean) {
        if (visible) {
            refreshWifiInfo()
        }
        _uiState.update { it.copy(showWifiServerDialog = visible) }
    }

    fun refreshWifiInfo() {
        val app = getApplication<Application>()
        val ip = NetworkUtils.getLocalIpAddress(app)
        val ssid = NetworkUtils.getWifiName(app)
        val port = _uiState.value.wifiServerPort
        val url = if (ip != null) "http://$ip:$port" else null

        _uiState.update {
            it.copy(
                wifiSsid = ssid,
                wifiServerUrl = if (it.isWifiServerRunning) url else it.wifiServerUrl
            )
        }
    }

    fun startWifiServer() {
        if (_uiState.value.isWifiServerRunning) return
        val app = getApplication<Application>()
        val ip = NetworkUtils.getLocalIpAddress(app)
        val port = _uiState.value.wifiServerPort
        val ssid = NetworkUtils.getWifiName(app)

        if (ip == null) {
            _uiState.update {
                it.copy(
                    notificationMessage = "Please connect to Wi-Fi or turn on Hotspot to start server",
                    wifiSsid = ssid
                )
            }
            return
        }

        webServer?.stop()
        val server = LocalVaultWebServer(
            context = app,
            port = port,
            requirePin = _uiState.value.wifiServerRequirePin,
            sessionPin = _uiState.value.wifiServerPin,
            dataProvider = { _uiState.value },
            onSaveCreditCard = { card -> saveCreditCard(card) },
            onDeleteCreditCard = { id -> deleteCreditCard(id) },
            onSaveDebitCard = { card -> saveDebitCard(card) },
            onDeleteDebitCard = { id -> deleteDebitCard(id) },
            onSaveBankAccount = { bank -> saveBankAccount(bank) },
            onDeleteBankAccount = { id -> deleteBankAccount(id) },
            onSaveWalletOrGiftCard = { wallet -> saveWalletOrGiftCard(wallet) },
            onDeleteWalletOrGiftCard = { id -> deleteWalletOrGiftCard(id) },
            onSaveMember = { member -> saveFamilyMember(member) },
            onDeleteMember = { id -> deleteFamilyMember(id) },
            onSaveDocument = { doc -> saveDocument(doc) },
            onDeleteDocument = { id -> deleteDocument(id) },
            onSetThemeMode = { mode -> setThemeMode(mode) },
            onSetCustomAccent = { hex -> setCustomAccentColor(hex) },
            onLogActivity = { log ->
                _uiState.update { current ->
                    val updated = (listOf(log) + current.wifiServerLogs).take(30)
                    current.copy(
                        wifiServerLogs = updated,
                        wifiServerConnectedClients = webServer?.getConnectedClientCount() ?: current.wifiServerConnectedClients
                    )
                }
            }
        )

        val success = server.start()
        if (success) {
            webServer = server
            val url = "http://$ip:$port"
            _uiState.update {
                it.copy(
                    isWifiServerRunning = true,
                    wifiServerUrl = url,
                    wifiSsid = ssid,
                    notificationMessage = "Wi-Fi Server running: $url"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isWifiServerRunning = false,
                    notificationMessage = "Failed to bind to port $port. Try another port."
                )
            }
        }
    }

    fun stopWifiServer() {
        webServer?.stop()
        webServer = null
        _uiState.update {
            it.copy(
                isWifiServerRunning = false,
                wifiServerUrl = null,
                wifiServerConnectedClients = 0,
                notificationMessage = "Wi-Fi Server stopped"
            )
        }
    }

    fun toggleWifiServer() {
        if (_uiState.value.isWifiServerRunning) {
            stopWifiServer()
        } else {
            startWifiServer()
        }
    }

    fun setWifiServerPort(port: Int) {
        preferencesManager.saveWifiServerPort(port)
        val wasRunning = _uiState.value.isWifiServerRunning
        if (wasRunning) {
            stopWifiServer()
        }
        _uiState.update { it.copy(wifiServerPort = port) }
        if (wasRunning) {
            startWifiServer()
        }
    }

    fun setWifiServerRequirePin(require: Boolean) {
        preferencesManager.saveWifiServerRequirePin(require)
        webServer?.requirePin = require
        _uiState.update { it.copy(wifiServerRequirePin = require) }
    }

    fun generateNewWifiServerPin() {
        val newPin = (1000..9999).random().toString()
        preferencesManager.saveWifiServerPin(newPin)
        webServer?.sessionPin = newPin
        _uiState.update { it.copy(wifiServerPin = newPin) }
    }

    fun clearWifiLogs() {
        _uiState.update { it.copy(wifiServerLogs = emptyList()) }
    }

    override fun onCleared() {
        super.onCleared()
        webServer?.stop()
        webServer = null
    }
}
