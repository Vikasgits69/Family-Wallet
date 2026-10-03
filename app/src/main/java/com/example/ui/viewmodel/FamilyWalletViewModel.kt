package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccountStatus
import com.example.data.AccountType
import com.example.data.AppThemeMode
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CardThemeColor
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.DisplayMode
import com.example.data.FamilyMember
import com.example.data.KycStatus
import com.example.data.MockData
import com.example.data.NavigationTab
import com.example.data.OnlineWallet
import com.example.data.WalletStatus
import com.example.sync.DriveSyncState
import com.example.sync.GoogleDriveBackupManager
import com.example.util.VaultPreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class FamilyWalletUiState(
    val members: List<FamilyMember> = MockData.familyMembers,
    val selectedMemberId: String? = null, // null means "All Family Members"
    val displayMode: DisplayMode = DisplayMode.CAROUSEL,
    val currentTab: NavigationTab = NavigationTab.DASHBOARD,
    val isMaskedGlobally: Boolean = true,
    val unmaskedItemIds: Set<String> = emptySet(),
    val flippedCardIds: Set<String> = emptySet(),
    val isDarkTheme: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val masterPin: String = "8421",
    val creditCards: List<CreditCard> = MockData.creditCards,
    val debitCards: List<DebitCard> = MockData.debitCards,
    val onlineWallets: List<OnlineWallet> = MockData.onlineWallets,
    val bankAccounts: List<BankAccount> = MockData.bankAccounts,
    val searchQuery: String = "",
    val networkFilter: CardNetwork? = null,
    val showAddCreditCardDialog: Boolean = false,
    val showAddDebitCardDialog: Boolean = false,
    val showAddAccountDialog: Boolean = false,
    val showAddWalletDialog: Boolean = false,
    val showAddMemberDialog: Boolean = false,
    val notificationMessage: String? = null,
    val isAppLocked: Boolean = false,
    val biometricStatusMessage: String? = null,
    val biometricEnabled: Boolean = true,
    val driveSync: DriveSyncState = DriveSyncState()
) {
    val selectedMember: FamilyMember?
        get() = members.find { it.id == selectedMemberId }

    // Filtered by selected member & search query
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
                        it.linkedAccount.contains(searchQuery, ignoreCase = true) ||
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
                        it.accountNumber.takeLast(4).contains(searchQuery)
            }

    val filteredOnlineWallets: List<OnlineWallet>
        get() = onlineWallets
            .filter { selectedMemberId == null || it.memberId == selectedMemberId }
            .filter {
                searchQuery.isBlank() ||
                        it.providerName.contains(searchQuery, ignoreCase = true) ||
                        it.upiId.contains(searchQuery, ignoreCase = true) ||
                        it.registeredMobile.contains(searchQuery)
            }

    // Informational Vault Counts
    val activeCreditCardsCount: Int
        get() = filteredCreditCards.count { it.status == CardStatus.ACTIVE }

    val activeDebitCardsCount: Int
        get() = filteredDebitCards.count { it.status == CardStatus.ACTIVE }

    val totalBankAccountsCount: Int
        get() = filteredBankAccounts.count { it.status == AccountStatus.ACTIVE }

    val totalOnlineWalletsCount: Int
        get() = filteredOnlineWallets.count { it.status == WalletStatus.ACTIVE }

    val ruPayCardsCount: Int
        get() = (filteredCreditCards.count { it.network == CardNetwork.RUPAY } +
                filteredDebitCards.count { it.network == CardNetwork.RUPAY })

    fun isItemUnmasked(id: String): Boolean {
        return !isMaskedGlobally || unmaskedItemIds.contains(id)
    }

    fun isCardFlipped(id: String): Boolean {
        return flippedCardIds.contains(id)
    }
}

class FamilyWalletViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(
        FamilyWalletUiState(
            themeMode = VaultPreferencesManager.loadThemeMode(application),
            isDarkTheme = (VaultPreferencesManager.loadThemeMode(application) == AppThemeMode.DARK || VaultPreferencesManager.loadThemeMode(application) == AppThemeMode.PITCH_BLACK),
            biometricEnabled = VaultPreferencesManager.loadBiometricEnabled(application),
            isMaskedGlobally = VaultPreferencesManager.loadGlobalMask(application),
            masterPin = VaultPreferencesManager.loadMasterPin(application)
        )
    )
    val uiState: StateFlow<FamilyWalletUiState> = _uiState.asStateFlow()

    fun setSelectedMember(memberId: String?) {
        _uiState.update { it.copy(selectedMemberId = memberId) }
    }

    fun setNavigationTab(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setDisplayMode(mode: DisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
    }

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
            state.copy(
                isMaskedGlobally = newMask,
                unmaskedItemIds = if (newMask) emptySet() else state.unmaskedItemIds
            )
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        VaultPreferencesManager.saveThemeMode(getApplication(), mode)
        _uiState.update {
            it.copy(
                themeMode = mode,
                isDarkTheme = (mode == AppThemeMode.DARK || mode == AppThemeMode.PITCH_BLACK)
            )
        }
    }

    fun toggleTheme() {
        _uiState.update { state ->
            val nextTheme = when (state.themeMode) {
                AppThemeMode.DARK -> AppThemeMode.LIGHT
                AppThemeMode.LIGHT -> AppThemeMode.PITCH_BLACK
                AppThemeMode.PITCH_BLACK -> AppThemeMode.PAPERLIKE
                AppThemeMode.PAPERLIKE -> AppThemeMode.DARK
                AppThemeMode.SYSTEM -> AppThemeMode.DARK
            }
            VaultPreferencesManager.saveThemeMode(getApplication(), nextTheme)
            state.copy(
                themeMode = nextTheme,
                isDarkTheme = (nextTheme == AppThemeMode.DARK || nextTheme == AppThemeMode.PITCH_BLACK)
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

    // Dialog Toggles
    fun setAddCreditCardDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddCreditCardDialog = visible) }
    }

    fun setAddDebitCardDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddDebitCardDialog = visible) }
    }

    fun setAddAccountDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddAccountDialog = visible) }
    }

    fun setAddWalletDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddWalletDialog = visible) }
    }

    fun setAddMemberDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAddMemberDialog = visible) }
    }

    // Adding vault items
    fun addCreditCard(
        bankName: String,
        cardName: String,
        network: CardNetwork,
        cardNumber: String,
        expiry: String,
        cvv: String,
        cardholderName: String,
        statementDate: String,
        dueDate: String,
        creditLimit: Double,
        annualFee: Double,
        waiverCondition: String,
        memberId: String,
        themeColor: CardThemeColor = CardThemeColor.CHARCOAL
    ) {
        val newCard = CreditCard(
            id = "cc_${UUID.randomUUID().toString().take(8)}",
            bankName = bankName,
            cardName = cardName,
            network = network,
            cardNumber = cardNumber.replace(" ", ""),
            expiry = expiry,
            cvv = cvv,
            cardholderName = cardholderName.uppercase(),
            statementDate = statementDate,
            dueDate = dueDate,
            creditLimit = creditLimit,
            annualFee = annualFee,
            waiverCondition = waiverCondition,
            status = CardStatus.ACTIVE,
            memberId = memberId,
            themeColor = themeColor
        )

        _uiState.update {
            it.copy(
                creditCards = listOf(newCard) + it.creditCards,
                showAddCreditCardDialog = false,
                notificationMessage = "Credit Card '$cardName' securely added to vault!"
            )
        }
    }

    fun addDebitCard(
        bankName: String,
        linkedAccount: String,
        network: CardNetwork,
        cardNumber: String,
        expiry: String,
        cvv: String,
        cardholderName: String,
        atmLimit: Double,
        posLimit: Double,
        memberId: String,
        themeColor: CardThemeColor = CardThemeColor.EMERALD
    ) {
        val newCard = DebitCard(
            id = "dc_${UUID.randomUUID().toString().take(8)}",
            bankName = bankName,
            linkedAccount = linkedAccount,
            network = network,
            cardNumber = cardNumber.replace(" ", ""),
            expiry = expiry,
            cvv = cvv,
            cardholderName = cardholderName.uppercase(),
            atmLimit = atmLimit,
            posLimit = posLimit,
            status = CardStatus.ACTIVE,
            memberId = memberId,
            themeColor = themeColor
        )

        _uiState.update {
            it.copy(
                debitCards = listOf(newCard) + it.debitCards,
                showAddDebitCardDialog = false,
                notificationMessage = "Debit Card for '$bankName' securely added to vault!"
            )
        }
    }

    fun addBankAccount(
        bankName: String,
        accountType: AccountType,
        accountNumber: String,
        ifscCode: String,
        branchName: String,
        accountHolderName: String,
        customerId: String,
        linkedMobile: String,
        linkedUpi: String,
        minBalance: Double,
        memberId: String
    ) {
        val newAccount = BankAccount(
            id = "acc_${UUID.randomUUID().toString().take(8)}",
            bankName = bankName,
            accountType = accountType,
            accountNumber = accountNumber,
            ifscCode = ifscCode.uppercase(),
            branchName = branchName,
            accountHolderName = accountHolderName.uppercase(),
            customerId = customerId,
            linkedMobile = linkedMobile,
            linkedUpi = linkedUpi,
            minBalance = minBalance,
            status = AccountStatus.ACTIVE,
            memberId = memberId
        )

        _uiState.update {
            it.copy(
                bankAccounts = listOf(newAccount) + it.bankAccounts,
                showAddAccountDialog = false,
                notificationMessage = "Bank Account '$bankName - $accountNumber' linked!"
            )
        }
    }

    fun addOnlineWallet(
        providerName: String,
        registeredMobile: String,
        registeredEmail: String,
        upiId: String,
        kycStatus: KycStatus,
        walletLimit: Double,
        memberId: String
    ) {
        val newWallet = OnlineWallet(
            id = "wal_${UUID.randomUUID().toString().take(8)}",
            providerName = providerName,
            registeredMobile = registeredMobile,
            registeredEmail = registeredEmail,
            upiId = upiId,
            kycStatus = kycStatus,
            walletLimit = walletLimit,
            status = WalletStatus.ACTIVE,
            memberId = memberId
        )

        _uiState.update {
            it.copy(
                onlineWallets = listOf(newWallet) + it.onlineWallets,
                showAddWalletDialog = false,
                notificationMessage = "Wallet '$providerName' linked to vault!"
            )
        }
    }

    fun addFamilyMember(name: String, relationship: String, emoji: String, colorHex: Long) {
        val newMember = FamilyMember(
            id = "mem_${UUID.randomUUID().toString().take(6)}",
            name = name,
            relationship = relationship,
            avatarEmoji = emoji,
            colorHex = colorHex,
            initials = name.take(2).uppercase()
        )
        _uiState.update {
            it.copy(
                members = it.members + newMember,
                showAddMemberDialog = false,
                notificationMessage = "Family Member '${name}' added!"
            )
        }
    }

    fun deleteCreditCard(cardId: String) {
        _uiState.update {
            it.copy(
                creditCards = it.creditCards.filterNot { card -> card.id == cardId },
                notificationMessage = "Card removed from vault"
            )
        }
    }

    fun deleteDebitCard(cardId: String) {
        _uiState.update {
            it.copy(
                debitCards = it.debitCards.filterNot { card -> card.id == cardId },
                notificationMessage = "Debit card removed from vault"
            )
        }
    }

    fun deleteBankAccount(accountId: String) {
        _uiState.update {
            it.copy(
                bankAccounts = it.bankAccounts.filterNot { acc -> acc.id == accountId },
                notificationMessage = "Bank account removed"
            )
        }
    }

    fun deleteOnlineWallet(walletId: String) {
        _uiState.update {
            it.copy(
                onlineWallets = it.onlineWallets.filterNot { wal -> wal.id == walletId },
                notificationMessage = "Wallet removed from vault"
            )
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    // App Lock & Biometrics
    fun lockApp() {
        _uiState.update { it.copy(isAppLocked = true) }
    }

    fun unlockApp() {
        _uiState.update { it.copy(isAppLocked = false, biometricStatusMessage = null) }
    }

    // Cloud Backup
    fun syncWithGoogleDrive() {
        viewModelScope.launch {
            GoogleDriveBackupManager.simulateBackup(
                onProgress = { syncState ->
                    _uiState.update { it.copy(driveSync = syncState) }
                },
                onComplete = { success, msg ->
                    _uiState.update {
                        it.copy(
                            notificationMessage = if (success) "Family Vault encrypted & synced to Google Drive" else "Sync failed: $msg"
                        )
                    }
                }
            )
        }
    }
}
