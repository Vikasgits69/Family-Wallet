package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppThemeMode
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.DisplayMode
import com.example.data.FamilyMember
import com.example.data.NavigationTab
import com.example.data.WalletOrGiftCard
import com.example.data.local.AppDatabase
import com.example.data.local.VaultRepository
import com.example.data.toDomain
import com.example.data.toEntity
import com.example.sync.DriveSyncState
import com.example.sync.GoogleDriveBackupManager
import com.example.util.VaultPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class UpcomingCardAlert(
    val cardId: String,
    val bankName: String,
    val cardName: String,
    val statementDate: String,
    val dueDate: String,
    val memberId: String,
    val memberName: String,
    val colorHex: Long,
    val isDueDate: Boolean
)

data class FamilyWalletUiState(
    val members: List<FamilyMember> = emptyList(),
    val selectedMemberId: String? = null,
    val displayMode: DisplayMode = DisplayMode.LIST,
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
    val searchQuery: String = "",
    val networkFilter: CardNetwork? = null,
    val showAddCreditCardDialog: Boolean = false,
    val showAddDebitCardDialog: Boolean = false,
    val showAddAccountDialog: Boolean = false,
    val showAddWalletDialog: Boolean = false,
    val showAddMemberDialog: Boolean = false,
    val editingCreditCard: CreditCard? = null,
    val editingDebitCard: DebitCard? = null,
    val editingBankAccount: BankAccount? = null,
    val editingWalletOrGiftCard: WalletOrGiftCard? = null,
    val editingMember: FamilyMember? = null,
    val notificationMessage: String? = null,
    val isAppLocked: Boolean = false,
    val biometricStatusMessage: String? = null,
    val biometricEnabled: Boolean = true,
    val driveSync: DriveSyncState = DriveSyncState()
) {
    val selectedMember: FamilyMember?
        get() = members.find { it.id == selectedMemberId }

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
        get() = filteredWalletsAndGiftCards.count { it.isGiftCard }

    val totalVaultAssetsCount: Int
        get() = filteredCreditCards.size + filteredDebitCards.size + filteredBankAccounts.size + filteredWalletsAndGiftCards.size

    // Upcoming Action Card Items: Bill Dates & Payment Due Dates with explicit Member Name
    val upcomingAlerts: List<UpcomingCardAlert>
        get() = creditCards.filter { it.remindBillDate || it.remindDueDate }.mapNotNull { card ->
            val member = members.find { it.id == card.memberId }
            val memberName = member?.name ?: "Vault Unassigned"
            if (card.dueDate.isNotBlank()) {
                UpcomingCardAlert(
                    cardId = card.id,
                    bankName = card.bankName,
                    cardName = card.cardName,
                    statementDate = card.statementDate,
                    dueDate = card.dueDate,
                    memberId = card.memberId,
                    memberName = memberName,
                    colorHex = card.colorHex,
                    isDueDate = true
                )
            } else if (card.statementDate.isNotBlank()) {
                UpcomingCardAlert(
                    cardId = card.id,
                    bankName = card.bankName,
                    cardName = card.cardName,
                    statementDate = card.statementDate,
                    dueDate = card.dueDate,
                    memberId = card.memberId,
                    memberName = memberName,
                    colorHex = card.colorHex,
                    isDueDate = false
                )
            } else null
        }

    fun isItemUnmasked(id: String): Boolean {
        return !isMaskedGlobally || unmaskedItemIds.contains(id)
    }

    fun isCardFlipped(id: String): Boolean {
        return flippedCardIds.contains(id)
    }
}

class FamilyWalletViewModel(application: Application) : AndroidViewModel(application) {

    // Room Database & Repository
    private val database = AppDatabase.getDatabase(application)
    private val repository = VaultRepository(database.familyWalletDao())

    private val _uiState = MutableStateFlow(
        FamilyWalletUiState(
            themeMode = VaultPreferencesManager.loadThemeMode(application),
            isDarkTheme = (VaultPreferencesManager.loadThemeMode(application) != AppThemeMode.LIGHT && VaultPreferencesManager.loadThemeMode(application) != AppThemeMode.DOODLE && VaultPreferencesManager.loadThemeMode(application) != AppThemeMode.PAPERLIKE),
            biometricEnabled = VaultPreferencesManager.loadBiometricEnabled(application),
            isAppLocked = VaultPreferencesManager.loadBiometricEnabled(application),
            isMaskedGlobally = VaultPreferencesManager.loadGlobalMask(application),
            masterPin = VaultPreferencesManager.loadMasterPin(application)
        )
    )
    val uiState: StateFlow<FamilyWalletUiState> = _uiState.asStateFlow()

    init {
        // Collect reactive flows from Room repository into UI State
        viewModelScope.launch {
            combine(
                repository.allMembers,
                repository.allCreditCards,
                repository.allDebitCards,
                repository.allBankAccounts,
                repository.allWalletsAndGiftCards
            ) { members, creditCards, debitCards, bankAccounts, walletsAndGifts ->
                _uiState.update { current ->
                    current.copy(
                        members = members.map { it.toDomain() },
                        creditCards = creditCards.map { it.toDomain() },
                        debitCards = debitCards.map { it.toDomain() },
                        bankAccounts = bankAccounts.map { it.toDomain() },
                        walletsAndGiftCards = walletsAndGifts.map { it.toDomain() }
                    )
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

    // Functional View Toggles (List, Grid, Carousel)
    fun setDisplayMode(mode: DisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
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

    fun toggleMaskForItem(itemId: String) {
        _uiState.update { state ->
            val updated = if (state.unmaskedItemIds.contains(itemId)) {
                state.unmaskedItemIds - itemId
            } else {
                state.unmaskedItemIds + itemId
            }
            state.copy(unmaskedItemIds = updated)
        }
    }

    fun toggleItemMask(itemId: String) {
        toggleMaskForItem(itemId)
    }

    fun toggleGlobalMask() {
        _uiState.update { state ->
            val newMask = !state.isMaskedGlobally
            VaultPreferencesManager.saveGlobalMask(getApplication(), newMask)
            state.copy(isMaskedGlobally = newMask, unmaskedItemIds = emptySet())
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        VaultPreferencesManager.saveThemeMode(getApplication(), mode)
        _uiState.update {
            it.copy(
                themeMode = mode,
                isDarkTheme = (mode != AppThemeMode.LIGHT && mode != AppThemeMode.DOODLE && mode != AppThemeMode.PAPERLIKE)
            )
        }
    }

    fun toggleTheme() {
        _uiState.update { state ->
            val nextTheme = when (state.themeMode) {
                AppThemeMode.DOODLE -> AppThemeMode.DOODLE_DARK
                AppThemeMode.DOODLE_DARK -> AppThemeMode.LIGHT
                AppThemeMode.LIGHT -> AppThemeMode.DARK
                AppThemeMode.DARK -> AppThemeMode.PITCH_BLACK
                AppThemeMode.PITCH_BLACK -> AppThemeMode.HIGH_CONTRAST
                AppThemeMode.HIGH_CONTRAST -> AppThemeMode.PAPERLIKE
                AppThemeMode.PAPERLIKE -> AppThemeMode.DOODLE
                AppThemeMode.SYSTEM -> AppThemeMode.DOODLE
            }
            VaultPreferencesManager.saveThemeMode(getApplication(), nextTheme)
            state.copy(
                themeMode = nextTheme,
                isDarkTheme = (nextTheme != AppThemeMode.LIGHT && nextTheme != AppThemeMode.DOODLE && nextTheme != AppThemeMode.PAPERLIKE)
            )
        }
    }

    fun toggleBiometricEnabled() {
        _uiState.update { state ->
            val newBio = !state.biometricEnabled
            VaultPreferencesManager.saveBiometricEnabled(getApplication(), newBio)
            state.copy(biometricEnabled = newBio)
        }
    }

    // Dialog Visibilities & Editing Setters
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
            val snapshot = GoogleDriveBackupManager.backupVaultToDrive(
                context = getApplication(),
                members = current.members,
                creditCards = current.creditCards,
                debitCards = current.debitCards,
                bankAccounts = current.bankAccounts,
                walletsAndGiftCards = current.walletsAndGiftCards
            )
            _uiState.update {
                it.copy(
                    driveSync = DriveSyncState(
                        isSyncing = false,
                        isRestoring = false,
                        lastSyncTimestamp = snapshot.timestamp,
                        lastSyncTime = snapshot.timestamp,
                        backupFileName = "family_wallet_vault_backup.json",
                        isConfigured = true,
                        lastSyncStatus = "Backed up ${snapshot.members.size} members, ${snapshot.creditCards.size + snapshot.debitCards.size} cards, ${snapshot.bankAccounts.size} accounts",
                        lastBackupMemberCount = snapshot.members.size,
                        lastBackupCardCount = snapshot.creditCards.size + snapshot.debitCards.size,
                        lastBackupBankCount = snapshot.bankAccounts.size,
                        lastBackupWalletCount = snapshot.walletsAndGiftCards.size
                    ),
                    notificationMessage = "Vault encrypted and backed up to Google Drive (${snapshot.timestamp})"
                )
            }
        }
    }

    fun restoreFromGoogleDrive() {
        viewModelScope.launch {
            _uiState.update { it.copy(driveSync = it.driveSync.copy(isRestoring = true)) }
            val restoredSnapshot = GoogleDriveBackupManager.restoreVaultFromDrive(
                context = getApplication(),
                repository = repository
            )
            if (restoredSnapshot != null) {
                _uiState.update {
                    it.copy(
                        driveSync = it.driveSync.copy(
                            isRestoring = false,
                            lastSyncStatus = "Restored ${restoredSnapshot.members.size} members, ${restoredSnapshot.creditCards.size + restoredSnapshot.debitCards.size} cards, ${restoredSnapshot.bankAccounts.size} accounts"
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
}
