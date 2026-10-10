package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Payments
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NavigationTab
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.RoseCrimson
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.FamilyWalletUiState
import com.example.ui.viewmodel.UpcomingCardAlert
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    uiState: FamilyWalletUiState,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenAddCreditCard: () -> Unit,
    onOpenAddDebitCard: () -> Unit,
    onOpenAddAccount: () -> Unit,
    onOpenAddWallet: () -> Unit,
    onOpenAddMember: () -> Unit,
    onOpenEmergencyIce: () -> Unit = {},
    onOpenSecurityCheckup: () -> Unit = {},
    onOpenHelplines: () -> Unit = {},
    onOpenWifiServer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Immediate rendering with zero delay for snappy border and card loading
    val isContentVisible = true

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Global Search Results (if user is searching)
        if (uiState.searchQuery.isNotBlank()) {
            item {
                GlobalSearchResultsSection(
                    uiState = uiState,
                    onNavigateTab = onNavigateTab
                )
            }
        }

        // Quick Action Bar for Emergency, Security, Helplines & Subscriptions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { onOpenEmergencyIce() },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE11D48).copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE11D48).copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ICE Vault", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFE11D48))
                    }
                }

                Surface(
                    onClick = { onOpenSecurityCheckup() },
                    shape = RoundedCornerShape(12.dp),
                    color = IndigoAccent.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Audit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = IndigoAccent)
                    }
                }

                Surface(
                    onClick = { onOpenHelplines() },
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldMint.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldMint.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldMint, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Helpline", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldMint)
                    }
                }

                Surface(
                    onClick = { onNavigateTab(NavigationTab.WALLETS) },
                    shape = RoundedCornerShape(12.dp),
                    color = AmberGold.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Subscriptions, contentDescription = null, tint = AmberGold, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${uiState.subscriptions.size} Subs", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AmberGold)
                    }
                }
            }
        }

        // Wi-Fi Web Companion Card
        item {
            Surface(
                onClick = onOpenWifiServer,
                shape = RoundedCornerShape(16.dp),
                color = if (uiState.isWifiServerRunning) EmeraldMint.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (uiState.isWifiServerRunning) EmeraldMint.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wifi_companion_dashboard_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.isWifiServerRunning) EmeraldMint.copy(alpha = 0.2f) else IndigoAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isWifiServerRunning) Icons.Default.Wifi else Icons.Default.Language,
                                contentDescription = null,
                                tint = if (uiState.isWifiServerRunning) EmeraldMint else IndigoAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (uiState.isWifiServerRunning) "Wi-Fi Server Live" else "Wi-Fi Web Companion",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (uiState.isWifiServerRunning) EmeraldMint else MaterialTheme.colorScheme.onSurface
                                )
                                if (uiState.isWifiServerRunning) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldMint)
                                    )
                                }
                            }
                            Text(
                                text = if (uiState.isWifiServerRunning)
                                    uiState.wifiServerUrl ?: "Connected"
                                else
                                    "Load & edit vault on browser over Wi-Fi",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (uiState.isWifiServerRunning) EmeraldMint else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.isWifiServerRunning) EmeraldMint else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text(
                            text = if (uiState.isWifiServerRunning) "CONNECTED" else "CONNECT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (uiState.isWifiServerRunning) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 1. Vault Overview with Side-by-Side Visual Donut Chart
        item {
            VaultOverviewWithChartCard(
                uiState = uiState,
                onNavigateTab = onNavigateTab
            )
        }

        // 2. Dynamic Upcoming Action Card (Card Bill Dates & Payment Due Dates + Explicit Family Member Name)
        item {
            UpcomingActionCard(
                alerts = uiState.upcomingAlerts,
                onNavigateToCards = { onNavigateTab(NavigationTab.CARDS) },
                onAddCard = onOpenAddCreditCard
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Vault Overview Card with Visual Donut Chart Side-by-Side
 */
@Composable
private fun VaultOverviewWithChartCard(
    uiState: FamilyWalletUiState,
    onNavigateTab: (NavigationTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_overview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VAULT OVERVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${uiState.totalVaultAssetsCount} Total Stored Assets",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    onClick = { onNavigateTab(NavigationTab.MEMBERS) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.Group, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "${uiState.members.size} Members",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Side-by-Side Content: Stat Breakdown (Left) + Visual Donut Chart (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left: Asset Stat Items
                Column(
                    modifier = Modifier.weight(1.1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OverviewStatRow(
                        label = "Credit Cards",
                        count = uiState.filteredCreditCards.size,
                        color = IndigoAccent,
                        icon = Icons.Outlined.CreditCard,
                        onClick = { onNavigateTab(NavigationTab.CARDS) }
                    )
                    OverviewStatRow(
                        label = "Debit Cards",
                        count = uiState.filteredDebitCards.size,
                        color = EmeraldMint,
                        icon = Icons.Outlined.Payments,
                        onClick = { onNavigateTab(NavigationTab.CARDS) }
                    )
                    OverviewStatRow(
                        label = "Bank Accounts",
                        count = uiState.filteredBankAccounts.size,
                        color = CyanAccent,
                        icon = Icons.Outlined.AccountBalance,
                        onClick = { onNavigateTab(NavigationTab.ACCOUNTS) }
                    )
                    OverviewStatRow(
                        label = "Personal Docs",
                        count = uiState.filteredPersonalDocuments.size,
                        color = Color(0xFF0284C7),
                        icon = Icons.Default.CreditCard,
                        onClick = { onNavigateTab(NavigationTab.MEMBERS) }
                    )
                    OverviewStatRow(
                        label = "Wallets & Gifts",
                        count = uiState.activeWalletsAndGiftCards.size,
                        color = AmberGold,
                        icon = Icons.Outlined.AccountBalanceWallet,
                        onClick = { onNavigateTab(NavigationTab.WALLETS) }
                    )
                }

                // Right: Donut Chart
                Box(
                    modifier = Modifier
                        .size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    VaultDonutChart(
                        creditCount = uiState.filteredCreditCards.size,
                        debitCount = uiState.filteredDebitCards.size,
                        bankCount = uiState.filteredBankAccounts.size,
                        personalDocCount = uiState.filteredPersonalDocuments.size,
                        walletGiftCount = uiState.activeWalletsAndGiftCards.size,
                        modifier = Modifier.fillMaxSize()
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.totalVaultAssetsCount}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Assets",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewStatRow(
    label: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

/**
 * Donut Chart for Vault Asset Distribution
 */
@Composable
private fun VaultDonutChart(
    creditCount: Int,
    debitCount: Int,
    bankCount: Int,
    personalDocCount: Int,
    walletGiftCount: Int,
    modifier: Modifier = Modifier
) {
    val total = creditCount + debitCount + bankCount + personalDocCount + walletGiftCount

    Canvas(modifier = modifier) {
        val strokeWidth = 18.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)

        if (total == 0) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
            return@Canvas
        }

        var startAngle = -90f

        val slices = listOf(
            Pair(creditCount, IndigoAccent),
            Pair(debitCount, EmeraldMint),
            Pair(bankCount, CyanAccent),
            Pair(personalDocCount, Color(0xFF0284C7)),
            Pair(walletGiftCount, AmberGold)
        )

        for ((count, color) in slices) {
            if (count > 0) {
                val sweep = (count.toFloat() / total) * 360f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep - 2f, // subtle gap
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweep
            }
        }
    }
}

/**
 * Dynamic Upcoming Action Card: Card Bill Dates & Payment Due Dates + Explicit Family Member Name
 */
@Composable
private fun UpcomingActionCard(
    alerts: List<UpcomingCardAlert>,
    onNavigateToCards: () -> Unit,
    onAddCard: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upcoming_action_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoseCrimson.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = RoseCrimson,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTION REQUIRED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.1.sp
                            ),
                            color = RoseCrimson
                        )
                        Text(
                            text = "Upcoming Payments",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                TextButton(
                    onClick = onNavigateToCards,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "View Cards",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (alerts.isNotEmpty()) {
                alerts.take(3).forEach { alert ->
                    UpcomingAlertItem(alert = alert, onClick = onNavigateToCards)
                }
            } else {
                Surface(
                    onClick = onAddCard,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No Card Reminders Set",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Add Bill Dates & Payment Due Dates on Credit Cards to track them here",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingAlertItem(
    alert: UpcomingCardAlert,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(alert.colorHex))
                    )
                    Column {
                        Text(
                            text = alert.cardName,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = alert.bankName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (alert.isDueDate) RoseCrimson.copy(alpha = 0.15f) else AmberGold.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (alert.isDueDate) "Due: ${alert.dueDate}" else "Bill: ${alert.statementDate}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (alert.isDueDate) RoseCrimson else AmberGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (alert.outstandingBalance > 0) {
                        Text(
                            text = "Bal: ₹${alert.outstandingBalance.toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = IndigoAccent,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Explicit Family Member Name Badge at the bottom of the card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Assigned Family Member",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Assigned Member: ${alert.memberName}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Quick Vault Action Buttons
 */
@Composable
private fun QuickVaultActionsRow(
    onAddCreditCard: () -> Unit,
    onAddDebitCard: () -> Unit,
    onAddAccount: () -> Unit,
    onAddWallet: () -> Unit,
    onAddMember: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "QUICK VAULT ACTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VaultActionButton(
                label = "+ Credit",
                icon = Icons.Outlined.CreditCard,
                color = IndigoAccent,
                modifier = Modifier.weight(1f),
                onClick = onAddCreditCard
            )
            VaultActionButton(
                label = "+ Debit",
                icon = Icons.Outlined.Payments,
                color = EmeraldMint,
                modifier = Modifier.weight(1f),
                onClick = onAddDebitCard
            )
            VaultActionButton(
                label = "+ Bank",
                icon = Icons.Outlined.AccountBalance,
                color = CyanAccent,
                modifier = Modifier.weight(1f),
                onClick = onAddAccount
            )
            VaultActionButton(
                label = "+ Wallet/Gift",
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
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

/**
 * Global Search Results Section on Dashboard
 */
@Composable
private fun GlobalSearchResultsSection(
    uiState: FamilyWalletUiState,
    onNavigateTab: (NavigationTab) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "GLOBAL SEARCH RESULTS FOR \"${uiState.searchQuery}\"",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Found ${uiState.filteredCreditCards.size} Credit Cards, ${uiState.filteredDebitCards.size} Debit Cards, ${uiState.filteredBankAccounts.size} Bank Accounts, ${uiState.filteredWalletsAndGiftCards.size} Wallets/Gifts, ${uiState.filteredMembers.size} Family Profiles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (uiState.filteredCreditCards.isNotEmpty() || uiState.filteredDebitCards.isNotEmpty()) {
                    OutlinedButton(onClick = { onNavigateTab(NavigationTab.CARDS) }) {
                        Text("View Cards (${uiState.filteredCreditCards.size + uiState.filteredDebitCards.size})")
                    }
                }
                if (uiState.filteredBankAccounts.isNotEmpty()) {
                    OutlinedButton(onClick = { onNavigateTab(NavigationTab.ACCOUNTS) }) {
                        Text("View Banks (${uiState.filteredBankAccounts.size})")
                    }
                }
                if (uiState.filteredWalletsAndGiftCards.isNotEmpty()) {
                    OutlinedButton(onClick = { onNavigateTab(NavigationTab.WALLETS) }) {
                        Text("View Wallets & Gifts")
                    }
                }
            }
        }
    }
}
