package com.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.NavigationTab
import com.example.security.BiometricAuthManager
import com.example.security.BiometricAvailability
import com.example.ui.components.FamilyWalletBottomBar
import com.example.ui.components.FamilyWalletTopBar
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AddAccountDialog
import com.example.ui.screens.AddCardDialog
import com.example.ui.screens.AddDocumentSheet
import com.example.ui.screens.AddMemberDialog
import com.example.ui.screens.AddWalletDialog
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.CardsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocsVaultScreen
import com.example.ui.screens.FamilyMembersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WalletsScreen
import com.example.ui.screens.AddSubscriptionDialog
import com.example.ui.components.EmergencyHelplineSheet
import com.example.ui.components.EmergencyIceDialog
import com.example.ui.components.SecurityCheckupDialog
import com.example.ui.components.SpendGiftCardDialog
import com.example.ui.components.UpiQrDialog
import com.example.ui.theme.FamilyWalletTheme
import com.example.ui.viewmodel.FamilyWalletViewModel

class MainActivity : FragmentActivity() {

    private var walletViewModel: FamilyWalletViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val vm: FamilyWalletViewModel = viewModel()
            walletViewModel = vm
            val state by vm.uiState.collectAsStateWithLifecycle()

            FamilyWalletTheme(
                themeMode = state.themeMode,
                customAccentHex = state.customAccentColorHex
            ) {
                FamilyWalletApp(
                    viewModel = vm,
                    onRequestBiometrics = {
                        val canAuth = BiometricAuthManager.checkAvailability(this)
                        if (canAuth.canPrompt) {
                            BiometricAuthManager.promptBiometricAuthentication(
                                activity = this,
                                title = "Family Financial Vault",
                                subtitle = "Authenticate to unlock vault",
                                onSuccess = {
                                    vm.unlockApp()
                                },
                                onError = { errorCode: Int, errString: CharSequence ->
                                    Toast.makeText(this, "Biometric error: $errString", Toast.LENGTH_SHORT).show()
                                },
                                onFailed = {
                                    Toast.makeText(this, "Fingerprint not recognized. Try again or enter PIN.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            vm.unlockApp()
                        }
                    },
                    biometricAvailability = BiometricAuthManager.checkAvailability(this)
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        walletViewModel?.performAutoBackupOnClose()
    }

    override fun onStop() {
        super.onStop()
        walletViewModel?.performAutoBackupOnClose()
    }

    override fun onResume() {
        super.onResume()
        walletViewModel?.let { vm ->
            vm.performAutoBackupOnOpen()
            if (vm.uiState.value.biometricEnabled && vm.uiState.value.isAppLocked) {
                val canAuth = BiometricAuthManager.checkAvailability(this)
                if (canAuth.canPrompt) {
                    BiometricAuthManager.promptBiometricAuthentication(
                        activity = this,
                        title = "Family Financial Vault",
                        subtitle = "Authenticate to unlock vault",
                        onSuccess = {
                            vm.unlockApp()
                        },
                        onError = { _, _ -> },
                        onFailed = {}
                    )
                }
            }
        }
    }
}

@Composable
fun FamilyWalletApp(
    viewModel: FamilyWalletViewModel,
    onRequestBiometrics: () -> Unit,
    biometricAvailability: BiometricAvailability
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Display Notification messages via Snackbar
    LaunchedEffect(state.notificationMessage) {
        state.notificationMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.dismissNotification()
        }
    }

    // Handle Back Press when not on Dashboard tab
    BackHandler(enabled = state.currentTab != NavigationTab.DASHBOARD && !state.isAppLocked) {
        viewModel.setNavigationTab(NavigationTab.DASHBOARD)
    }

    if (state.isAppLocked) {
        BiometricLockScreen(
            biometricAvailability = biometricAvailability,
            statusMessage = state.biometricStatusMessage,
            onRequestBiometrics = onRequestBiometrics,
            onVerifyPin = { pin ->
                if (pin == state.masterPin) {
                    viewModel.unlockApp()
                    true
                } else {
                    false
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                FamilyWalletTopBar(
                    uiState = state,
                    onSelectMember = { memberId -> viewModel.setSelectedMember(memberId) },
                    onToggleGlobalMask = { viewModel.toggleGlobalMask() },
                    onToggleTheme = { viewModel.toggleTheme() },
                    onLockApp = { viewModel.lockApp() },
                    onSearchQueryChange = { query -> viewModel.setSearchQuery(query) },
                    onSetDisplayMode = { mode -> viewModel.setDisplayMode(mode) },
                    onSetThemeMode = { mode -> viewModel.setThemeMode(mode) },
                    onSetCustomAccent = { hex -> viewModel.setCustomAccentColor(hex) },
                    onNavigateTab = { tab -> viewModel.setNavigationTab(tab) }
                )
            },
            bottomBar = {
                FamilyWalletBottomBar(
                    currentTab = state.currentTab,
                    onTabSelected = { tab -> viewModel.setNavigationTab(tab) }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedContent(
                    targetState = state.currentTab,
                    transitionSpec = {
                        fadeIn(tween(100))
                            .togetherWith(fadeOut(tween(100)))
                    },
                    label = "tab_content_transition"
                ) { targetTab ->
                    when (targetTab) {
                        NavigationTab.DASHBOARD -> DashboardScreen(
                            uiState = state,
                            onNavigateTab = { tab -> viewModel.setNavigationTab(tab) },
                            onOpenAddCreditCard = { viewModel.setAddCreditCardDialogVisible(true) },
                            onOpenAddDebitCard = { viewModel.setAddDebitCardDialogVisible(true) },
                            onOpenAddAccount = { viewModel.setAddAccountDialogVisible(true) },
                            onOpenAddWallet = { viewModel.setAddWalletDialogVisible(true) },
                            onOpenAddMember = { viewModel.setAddMemberDialogVisible(true) },
                            onOpenEmergencyIce = { viewModel.openEmergencyIce() },
                            onOpenSecurityCheckup = { viewModel.openSecurityCheckup() },
                            onOpenHelplines = { viewModel.openHelplines() }
                        )

                        NavigationTab.CARDS -> CardsScreen(
                            uiState = state,
                            onOpenAddCreditCard = { viewModel.setAddCreditCardDialogVisible(true) },
                            onOpenAddDebitCard = { viewModel.setAddDebitCardDialogVisible(true) },
                            onOpenEditCreditCard = { card -> viewModel.openEditCreditCard(card) },
                            onOpenEditDebitCard = { card -> viewModel.openEditDebitCard(card) },
                            onToggleCardFlip = { cardId -> viewModel.toggleCardFlip(cardId) },
                            onToggleItemMask = { itemId -> viewModel.toggleItemMask(itemId) },
                            onDeleteCreditCard = { id -> viewModel.deleteCreditCard(id) },
                            onDeleteDebitCard = { id -> viewModel.deleteDebitCard(id) },
                            onToggleBillPaid = { cardId -> viewModel.toggleCreditCardBillPaid(cardId) },
                            onOpenHelpline = { bankName -> viewModel.openHelplines(bankName) }
                        )

                        NavigationTab.ACCOUNTS -> AccountsScreen(
                            uiState = state,
                            onOpenAddAccount = { viewModel.setAddAccountDialogVisible(true) },
                            onOpenEditAccount = { account -> viewModel.openEditBankAccount(account) },
                            onDeleteAccount = { id -> viewModel.deleteBankAccount(id) },
                            onToggleMask = { id -> viewModel.toggleItemMask(id) }
                        )

                        NavigationTab.WALLETS -> WalletsScreen(
                            uiState = state,
                            onOpenAddWallet = { viewModel.setAddWalletDialogVisible(true) },
                            onOpenEditWalletOrGiftCard = { item -> viewModel.openEditWalletOrGiftCard(item) },
                            onDeleteWalletOrGiftCard = { id -> viewModel.deleteWalletOrGiftCard(id) },
                            onOpenAddSubscription = { viewModel.openAddSubscription() },
                            onOpenEditSubscription = { sub -> viewModel.openEditSubscription(sub) },
                            onDeleteSubscription = { id -> viewModel.deleteSubscription(id) },
                            onOpenSpendGiftCard = { item -> viewModel.openSpendGiftCard(item) },
                            onShowUpiQr = { vpa, name -> viewModel.showUpiQr(vpa, name) }
                        )

                        NavigationTab.DOCUMENTS -> DocsVaultScreen(
                            uiState = state,
                            onOpenAddDocument = { viewModel.setAddDocumentDialogVisible(true) },
                            onOpenEditDocument = { doc -> viewModel.openEditPersonalDocument(doc) },
                            onDeleteDocument = { id -> viewModel.deletePersonalDocument(id) },
                            onToggleMask = { id -> viewModel.toggleItemMask(id) }
                        )

                        NavigationTab.MEMBERS -> FamilyMembersScreen(
                            uiState = state,
                            onSelectMember = { memberId -> viewModel.setSelectedMember(memberId) },
                            onOpenAddMember = { viewModel.setAddMemberDialogVisible(true) },
                            onOpenEditMember = { member -> viewModel.openEditMember(member) },
                            onDeleteMember = { memberId -> viewModel.deleteFamilyMember(memberId) },
                            onOpenAddDocument = { memberId ->
                                if (memberId != null) viewModel.setSelectedMember(memberId)
                                viewModel.setAddDocumentDialogVisible(true)
                            },
                            onOpenEditDocument = { doc -> viewModel.openEditPersonalDocument(doc) },
                            onDeleteDocument = { id -> viewModel.deletePersonalDocument(id) },
                            onToggleDocumentMask = { id -> viewModel.toggleItemMask(id) },
                            onOpenEmergencyIce = { viewModel.openEmergencyIce() }
                        )

                        NavigationTab.SETTINGS -> SettingsScreen(
                            uiState = state,
                            onSetThemeMode = { mode -> viewModel.setThemeMode(mode) },
                            onSetCustomAccent = { hex -> viewModel.setCustomAccentColor(hex) },
                            onToggleBiometric = { viewModel.toggleBiometricEnabled() },
                            onLockApp = { viewModel.lockApp() },
                            onSyncGoogleDrive = { viewModel.syncWithGoogleDrive() },
                            onRestoreGoogleDrive = { viewModel.restoreFromGoogleDrive() },
                            onUpdateAutoBackupSettings = { freq, open, close -> viewModel.updateAutoBackupSettings(freq, open, close) },
                            onToggleIncludePhotosInBackup = { include -> viewModel.setIncludePhotosInBackup(include) },
                            onToggleClipboardAutoClear = { viewModel.toggleClipboardAutoClear() },
                            onSetClipboardTimeout = { sec -> viewModel.setClipboardClearTimeout(sec) },
                            onOpenSecurityCheckup = { viewModel.openSecurityCheckup() },
                            onOpenEmergencyIce = { viewModel.openEmergencyIce() },
                            onOpenHelplines = { viewModel.openHelplines() },
                            onExportJson = {
                                viewModel.exportVaultJson { json ->
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, json)
                                        type = "application/json"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "Export Encrypted Vault Backup")
                                    context.startActivity(shareIntent)
                                }
                            },
                            onImportJson = { json ->
                                viewModel.importVaultJson(json) { success ->
                                    val msg = if (success) "Vault restored successfully" else "Failed to restore vault"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onLoadBackups = { viewModel.loadAvailableDriveBackups() },
                            onRestoreSpecificBackup = { fileName -> viewModel.restoreSpecificDriveBackup(fileName) }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs for Complete CRUD Operations
    if (state.showAddCreditCardDialog || state.showAddDebitCardDialog) {
        AddCardDialog(
            members = state.members,
            existingCreditCards = state.creditCards,
            existingDebitCards = state.debitCards,
            existingBankAccounts = state.bankAccounts,
            creditCardToEdit = state.editingCreditCard,
            debitCardToEdit = state.editingDebitCard,
            initialIsCredit = state.showAddCreditCardDialog,
            onDismiss = {
                viewModel.setAddCreditCardDialogVisible(false)
                viewModel.setAddDebitCardDialogVisible(false)
            },
            onSaveCreditCard = { card -> viewModel.saveCreditCard(card) },
            onSaveDebitCard = { card -> viewModel.saveDebitCard(card) }
        )
    }

    if (state.showAddAccountDialog) {
        AddAccountDialog(
            members = state.members,
            existingCreditCards = state.creditCards,
            existingDebitCards = state.debitCards,
            existingBankAccounts = state.bankAccounts,
            accountToEdit = state.editingBankAccount,
            onDismiss = { viewModel.setAddAccountDialogVisible(false) },
            onSaveAccount = { account -> viewModel.saveBankAccount(account) }
        )
    }

    if (state.showAddWalletDialog) {
        AddWalletDialog(
            members = state.members,
            itemToEdit = state.editingWalletOrGiftCard,
            onDismiss = { viewModel.setAddWalletDialogVisible(false) },
            onSaveItem = { item -> viewModel.saveWalletOrGiftCard(item) }
        )
    }

    if (state.showAddMemberDialog) {
        AddMemberDialog(
            memberToEdit = state.editingMember,
            onDismiss = { viewModel.setAddMemberDialogVisible(false) },
            onConfirm = { member -> viewModel.saveFamilyMember(member) }
        )
    }

    if (state.showAddDocumentDialog) {
        AddDocumentSheet(
            members = state.members,
            initialMemberId = state.selectedMemberId,
            documentToEdit = state.editingPersonalDocument,
            onDismiss = { viewModel.setAddDocumentDialogVisible(false) },
            onSaveDocument = { doc -> viewModel.savePersonalDocument(doc) }
        )
    }

    if (state.showAddSubscriptionDialog) {
        AddSubscriptionDialog(
            members = state.members,
            subscriptionToEdit = state.editingSubscription,
            onDismiss = { viewModel.closeSubscriptionDialog() },
            onSaveSubscription = { sub -> viewModel.saveSubscription(sub) }
        )
    }

    if (state.showEmergencyIceDialog) {
        EmergencyIceDialog(
            uiState = state,
            onDismiss = { viewModel.closeEmergencyIce() }
        )
    }

    if (state.showSecurityCheckupDialog) {
        SecurityCheckupDialog(
            uiState = state,
            onDismiss = { viewModel.closeSecurityCheckup() }
        )
    }

    if (state.showHelplineDialog) {
        EmergencyHelplineSheet(
            uiState = state,
            initialSearchBank = state.helplineTargetBank,
            onDismiss = { viewModel.closeHelplines() }
        )
    }

    state.activeUpiQrData?.let { (vpa, name) ->
        UpiQrDialog(
            vpaOrUpiId = vpa,
            payeeName = name,
            bankNameOrWallet = "",
            onDismiss = { viewModel.closeUpiQr() }
        )
    }

    state.spendingGiftCard?.let { giftCard ->
        SpendGiftCardDialog(
            giftCard = giftCard,
            onDismiss = { viewModel.closeSpendGiftCard() },
            onConfirmSpend = { amount -> viewModel.deductGiftCardBalance(giftCard.id, amount) }
        )
    }
}
