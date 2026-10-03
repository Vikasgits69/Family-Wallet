package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.example.ui.screens.AddMemberDialog
import com.example.ui.screens.AddWalletDialog
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.CardsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FamilyMembersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WalletsScreen
import com.example.ui.theme.FamilyWalletTheme
import com.example.ui.viewmodel.FamilyWalletViewModel

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val walletViewModel: FamilyWalletViewModel = viewModel()
            val state by walletViewModel.uiState.collectAsStateWithLifecycle()

            FamilyWalletTheme(themeMode = state.themeMode) {
                FamilyWalletApp(
                    viewModel = walletViewModel,
                    onRequestBiometrics = {
                        val canAuth = BiometricAuthManager.checkAvailability(this)
                        if (canAuth.canPrompt) {
                            BiometricAuthManager.promptBiometricAuthentication(
                                activity = this,
                                title = "Family Financial Vault",
                                subtitle = "Authenticate to unlock vault",
                                onSuccess = {
                                    walletViewModel.unlockApp()
                                },
                                onError = { errorCode: Int, errString: CharSequence ->
                                    Toast.makeText(this, "Biometric error: $errString", Toast.LENGTH_SHORT).show()
                                },
                                onFailed = {
                                    Toast.makeText(this, "Fingerprint not recognized. Try again or enter PIN.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            walletViewModel.unlockApp()
                        }
                    },
                    biometricAvailability = BiometricAuthManager.checkAvailability(this)
                )
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
                    onSetNetworkFilter = { net -> viewModel.setNetworkFilter(net) }
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
                            onToggleCardFlip = { cardId -> viewModel.toggleCardFlip(cardId) },
                            onToggleItemMask = { itemId -> viewModel.toggleItemMask(itemId) }
                        )

                        NavigationTab.CARDS -> CardsScreen(
                            uiState = state,
                            onOpenAddCreditCard = { viewModel.setAddCreditCardDialogVisible(true) },
                            onOpenAddDebitCard = { viewModel.setAddDebitCardDialogVisible(true) },
                            onToggleCardFlip = { cardId -> viewModel.toggleCardFlip(cardId) },
                            onToggleItemMask = { itemId -> viewModel.toggleItemMask(itemId) },
                            onDeleteCreditCard = { id -> viewModel.deleteCreditCard(id) },
                            onDeleteDebitCard = { id -> viewModel.deleteDebitCard(id) }
                        )

                        NavigationTab.ACCOUNTS -> AccountsScreen(
                            uiState = state,
                            onOpenAddAccount = { viewModel.setAddAccountDialogVisible(true) },
                            onDeleteAccount = { id -> viewModel.deleteBankAccount(id) },
                            onToggleMask = { id -> viewModel.toggleItemMask(id) }
                        )

                        NavigationTab.WALLETS -> WalletsScreen(
                            uiState = state,
                            onOpenAddWallet = { viewModel.setAddWalletDialogVisible(true) },
                            onDeleteWallet = { id -> viewModel.deleteOnlineWallet(id) }
                        )

                        NavigationTab.MEMBERS -> FamilyMembersScreen(
                            uiState = state,
                            onSelectMember = { memberId -> viewModel.setSelectedMember(memberId) },
                            onOpenAddMember = { viewModel.setAddMemberDialogVisible(true) }
                        )

                        NavigationTab.SETTINGS -> SettingsScreen(
                            uiState = state,
                            onSetThemeMode = { mode -> viewModel.setThemeMode(mode) },
                            onToggleBiometric = { viewModel.toggleBiometricEnabled() },
                            onLockApp = { viewModel.lockApp() },
                            onSyncGoogleDrive = { viewModel.syncWithGoogleDrive() }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (state.showAddCreditCardDialog || state.showAddDebitCardDialog) {
        AddCardDialog(
            members = state.members,
            initialIsCredit = state.showAddCreditCardDialog,
            onDismiss = {
                viewModel.setAddCreditCardDialogVisible(false)
                viewModel.setAddDebitCardDialogVisible(false)
            },
            onAddCreditCard = { bank, card, net, num, exp, cvv, holder, stmt, due, limit, fee, waiver, mem, theme ->
                viewModel.addCreditCard(bank, card, net, num, exp, cvv, holder, stmt, due, limit, fee, waiver, mem, theme)
            },
            onAddDebitCard = { bank, acc, net, num, exp, cvv, holder, atm, pos, mem, theme ->
                viewModel.addDebitCard(bank, acc, net, num, exp, cvv, holder, atm, pos, mem, theme)
            }
        )
    }

    if (state.showAddAccountDialog) {
        AddAccountDialog(
            members = state.members,
            onDismiss = { viewModel.setAddAccountDialogVisible(false) },
            onAddAccount = { bank, type, num, ifsc, branch, holder, custId, mob, upi, minBal, mem ->
                viewModel.addBankAccount(bank, type, num, ifsc, branch, holder, custId, mob, upi, minBal, mem)
            }
        )
    }

    if (state.showAddWalletDialog) {
        AddWalletDialog(
            members = state.members,
            onDismiss = { viewModel.setAddWalletDialogVisible(false) },
            onAddWallet = { provider, mob, email, upi, kyc, limit, mem ->
                viewModel.addOnlineWallet(provider, mob, email, upi, kyc, limit, mem)
            }
        )
    }

    if (state.showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { viewModel.setAddMemberDialogVisible(false) },
            onAddMember = { name, rel, emoji, color ->
                viewModel.addFamilyMember(name, rel, emoji, color)
            }
        )
    }
}
