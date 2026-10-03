package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CardNetwork
import com.example.data.NavigationTab
import com.example.ui.components.CardNetworkBadge
import com.example.ui.components.FloatingStatCard
import com.example.ui.components.InteractiveCreditCardItem
import com.example.ui.components.InteractiveDebitCardItem
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.RoseCrimson
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.FamilyWalletUiState
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    uiState: FamilyWalletUiState,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenAddCreditCard: () -> Unit,
    onOpenAddDebitCard: () -> Unit,
    onOpenAddAccount: () -> Unit,
    onOpenAddWallet: () -> Unit,
    onToggleCardFlip: (String) -> Unit,
    onToggleItemMask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isContentVisible = true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Staggered Hero Vault Status Header
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(350)) + fadeIn(tween(350))
            ) {
                VaultSecurityHeroHeader(uiState = uiState)
            }
        }

        // 2. Informational Summary Cards Grid (2x2)
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(initialOffsetY = { 60 }, animationSpec = tween(400)) + fadeIn(tween(400))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "VAULT OVERVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingStatCard(
                            title = "Credit Cards",
                            value = "${uiState.activeCreditCardsCount}",
                            subtitle = "${uiState.filteredCreditCards.count { it.network == CardNetwork.RUPAY }} on RuPay",
                            icon = Icons.Outlined.CreditCard,
                            accentColor = IndigoAccent,
                            badgeText = "Active",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(NavigationTab.CARDS) }
                        )

                        FloatingStatCard(
                            title = "Debit Cards",
                            value = "${uiState.activeDebitCardsCount}",
                            subtitle = "ATM & POS limits",
                            icon = Icons.Outlined.Payments,
                            accentColor = EmeraldMint,
                            badgeText = "Verified",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(NavigationTab.CARDS) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingStatCard(
                            title = "Bank Accounts",
                            value = "${uiState.totalBankAccountsCount}",
                            subtitle = "IFSC & UPI linked",
                            icon = Icons.Outlined.AccountBalance,
                            accentColor = CyanAccent,
                            badgeText = "Savings/Salary",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(NavigationTab.ACCOUNTS) }
                        )

                        FloatingStatCard(
                            title = "Online Wallets",
                            value = "${uiState.totalOnlineWalletsCount}",
                            subtitle = "Paytm, PhonePe, GPay",
                            icon = Icons.Outlined.AccountBalanceWallet,
                            accentColor = AmberGold,
                            badgeText = "KYC Full",
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(NavigationTab.WALLETS) }
                        )
                    }
                }
            }
        }

        // 3. Primary Informational Actions Bar (Strictly Vault Actions: + Card, + Account, + Wallet)
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(initialOffsetY = { 80 }, animationSpec = tween(450)) + fadeIn(tween(450))
            ) {
                QuickVaultActionsGrid(
                    onAddCreditCard = onOpenAddCreditCard,
                    onAddDebitCard = onOpenAddDebitCard,
                    onAddAccount = onOpenAddAccount,
                    onAddWallet = onOpenAddWallet
                )
            }
        }

        // 4. RuPay National Payment Spotlight Banner
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(500)) + fadeIn(tween(500))
            ) {
                RuPaySpotlightBanner(ruPayCount = uiState.ruPayCardsCount)
            }
        }

        // 5. Interactive 3D Card Stack Carousel
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(initialOffsetY = { 120 }, animationSpec = tween(550)) + fadeIn(tween(550))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "QUICK CARD ACCESS (TAP TO FLIP 3D)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "3D Interactive Digital Cards",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "View All (${uiState.filteredCreditCards.size + uiState.filteredDebitCards.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onNavigateTab(NavigationTab.CARDS) }
                        )
                    }

                    if (uiState.filteredCreditCards.isNotEmpty() || uiState.filteredDebitCards.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(uiState.filteredCreditCards, key = { it.id }) { card ->
                                val member = uiState.members.find { it.id == card.memberId }
                                Box(modifier = Modifier.width(320.dp)) {
                                    InteractiveCreditCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name
                                    )
                                }
                            }

                            items(uiState.filteredDebitCards, key = { it.id }) { card ->
                                val member = uiState.members.find { it.id == card.memberId }
                                Box(modifier = Modifier.width(320.dp)) {
                                    InteractiveDebitCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Linked Bank Accounts Quick Glance
        item {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = slideInVertically(initialOffsetY = { 140 }, animationSpec = tween(600)) + fadeIn(tween(600))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LINKED BANK ACCOUNTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Manage",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onNavigateTab(NavigationTab.ACCOUNTS) }
                        )
                    }

                    uiState.filteredBankAccounts.take(2).forEach { account ->
                        QuickBankItem(account = account, onClick = { onNavigateTab(NavigationTab.ACCOUNTS) })
                    }
                }
            }
        }

        // Bottom padding spacer
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun VaultSecurityHeroHeader(uiState: FamilyWalletUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EmeraldMint)
                    )
                    Text(
                        text = "END-TO-END ENCRYPTED VAULT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldMint
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val memberName = uiState.selectedMember?.name ?: "All Family Members"
                Text(
                    text = "$memberName's Vault",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Storing ${uiState.creditCards.size} Credit, ${uiState.debitCards.size} Debit, ${uiState.bankAccounts.size} Banks & ${uiState.onlineWallets.size} Wallets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security Shield",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickVaultActionsGrid(
    onAddCreditCard: () -> Unit,
    onAddDebitCard: () -> Unit,
    onAddAccount: () -> Unit,
    onAddWallet: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "QUICK VAULT ACTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VaultActionButton(
                title = "+ Credit Card",
                subtitle = "RuPay / VISA / Amex",
                icon = Icons.Outlined.CreditCard,
                color = IndigoAccent,
                modifier = Modifier.weight(1f),
                onClick = onAddCreditCard
            )

            VaultActionButton(
                title = "+ Debit Card",
                subtitle = "ATM & POS limits",
                icon = Icons.Outlined.Payments,
                color = EmeraldMint,
                modifier = Modifier.weight(1f),
                onClick = onAddDebitCard
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VaultActionButton(
                title = "+ Bank Account",
                subtitle = "IFSC & Branch details",
                icon = Icons.Outlined.AccountBalance,
                color = CyanAccent,
                modifier = Modifier.weight(1f),
                onClick = onAddAccount
            )

            VaultActionButton(
                title = "+ Online Wallet",
                subtitle = "Paytm, PhonePe, GPay",
                icon = Icons.Outlined.AccountBalanceWallet,
                color = AmberGold,
                modifier = Modifier.weight(1f),
                onClick = onAddWallet
            )
        }
    }
}

@Composable
private fun VaultActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("action_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RuPaySpotlightBanner(ruPayCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C2340) // RuPay Navy Blue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF27922)))
                    Text(
                        text = "RuPay Priority Vault",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF0F9D58)))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$ruPayCount RuPay Cards Ready for UPI & Tap",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White.copy(alpha = 0.95f)
                )

                Text(
                    text = "Zero MDR on domestic UPI transactions & enhanced airport lounge access.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF27922))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "NPCI",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuickBankItem(
    account: com.example.data.BankAccount,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = account.bankName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "A/c •••• ${account.accountNumber.takeLast(4)} • IFSC: ${account.ifscCode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = account.accountType.label.take(7),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
