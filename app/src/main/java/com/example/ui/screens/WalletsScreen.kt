package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DisplayMode
import com.example.data.NavigationTab
import com.example.data.Subscription
import com.example.data.WalletOrGiftCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import com.example.util.SafeVaultShareManager
import com.example.util.VaultPreferencesManager
import com.example.ui.viewmodel.FamilyWalletUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsScreen(
    uiState: FamilyWalletUiState,
    onOpenAddWallet: () -> Unit,
    onOpenEditWalletOrGiftCard: (WalletOrGiftCard) -> Unit,
    onDeleteWalletOrGiftCard: (String) -> Unit,
    onOpenAddSubscription: () -> Unit,
    onOpenEditSubscription: (Subscription) -> Unit,
    onDeleteSubscription: (String) -> Unit,
    onOpenSpendGiftCard: (WalletOrGiftCard) -> Unit,
    onShowUpiQr: (String, String) -> Unit,
    onToggleMarkedAsUsed: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("All", "Online Wallets", "Gift Cards", "Subscriptions")
    var itemToDelete by remember { mutableStateOf<WalletOrGiftCard?>(null) }
    var subscriptionToDelete by remember { mutableStateOf<Subscription?>(null) }

    val displayedItems by remember(uiState.filteredWalletsAndGiftCards, selectedTabIndex) {
        derivedStateOf {
            when (selectedTabIndex) {
                1 -> uiState.filteredWalletsAndGiftCards.filter { !it.isGiftCard }
                2 -> uiState.filteredWalletsAndGiftCards.filter { it.isGiftCard }
                else -> uiState.filteredWalletsAndGiftCards
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs: All, Online Wallets, Gift Cards, Subscriptions
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 16.dp
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            if (selectedTabIndex == 3) {
                // Subscriptions Tab Content
                if (uiState.filteredSubscriptions.isEmpty()) {
                    EmptySubscriptionsView(onAddSubscription = onOpenAddSubscription)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("subscriptions_list"),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.filteredSubscriptions, key = { it.id }) { sub ->
                            val member = uiState.members.find { it.id == sub.memberId }
                            SubscriptionCard(
                                subscription = sub,
                                memberName = member?.name,
                                onEdit = { onOpenEditSubscription(sub) },
                                onDelete = { subscriptionToDelete = sub }
                            )
                        }
                    }
                }
            } else {
                // Wallets & Gift Cards Content
                if (displayedItems.isEmpty()) {
                    EmptyWalletsAndGiftCardsView(onAddItem = onOpenAddWallet)
                } else {
                    when (uiState.getDisplayModeForTab(NavigationTab.WALLETS)) {
                        DisplayMode.CAROUSEL -> {
                            var isUsedExpanded by remember { mutableStateOf(false) }
                            val activeItems = displayedItems.filter { !it.isGiftCard || !it.isMarkedAsUsed }
                            val usedItems = displayedItems.filter { it.isGiftCard && it.isMarkedAsUsed }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("wallets_screen"),
                                contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (activeItems.isNotEmpty()) {
                                    item(key = "active_header") {
                                        Text(
                                            text = "Active (${activeItems.size})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        )
                                    }
                                    item(key = "active_row") {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp)
                                        ) {
                                            items(activeItems, key = { it.id }) { item ->
                                                val member = uiState.members.find { it.id == item.memberId }
                                                Box(modifier = Modifier.width(320.dp)) {
                                                    WalletOrGiftCardItem(
                                                        item = item,
                                                        memberName = member?.name,
                                                        onEdit = { onOpenEditWalletOrGiftCard(item) },
                                                        onDelete = { itemToDelete = item },
                                                        onSpend = { onOpenSpendGiftCard(item) },
                                                        onShowQr = { onShowUpiQr(item.cardNumberOrUpi, item.providerOrName) },
                                                        onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                                        onToggleMarkedAsUsed = { onToggleMarkedAsUsed(item.id) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (usedItems.isNotEmpty()) {
                                    item(key = "used_header") {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .clickable { isUsedExpanded = !isUsedExpanded },
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isUsedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                        contentDescription = "Expand/Collapse Used"
                                                    )
                                                    Text(
                                                        text = "Used (${usedItems.size})",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                                    Text(
                                                        text = "${usedItems.size}",
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (isUsedExpanded) {
                                        item(key = "used_row") {
                                            LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp)
                                            ) {
                                                items(usedItems, key = { it.id }) { item ->
                                                    val member = uiState.members.find { it.id == item.memberId }
                                                    Box(modifier = Modifier.width(320.dp)) {
                                                        WalletOrGiftCardItem(
                                                            item = item,
                                                            memberName = member?.name,
                                                            onEdit = { onOpenEditWalletOrGiftCard(item) },
                                                            onDelete = { itemToDelete = item },
                                                            onSpend = { onOpenSpendGiftCard(item) },
                                                            onShowQr = { onShowUpiQr(item.cardNumberOrUpi, item.providerOrName) },
                                                            onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                                            onToggleMarkedAsUsed = { onToggleMarkedAsUsed(item.id) }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        DisplayMode.GRID -> {
                            var isUsedExpanded by remember { mutableStateOf(false) }
                            val activeItems = displayedItems.filter { !it.isGiftCard || !it.isMarkedAsUsed }
                            val usedItems = displayedItems.filter { it.isGiftCard && it.isMarkedAsUsed }

                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 300.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("wallets_screen"),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (activeItems.isNotEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            text = "Active (${activeItems.size})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                    items(activeItems, key = { it.id }) { item ->
                                        val member = uiState.members.find { it.id == item.memberId }
                                        WalletOrGiftCardItem(
                                            item = item,
                                            memberName = member?.name,
                                            onEdit = { onOpenEditWalletOrGiftCard(item) },
                                            onDelete = { itemToDelete = item },
                                            onSpend = { onOpenSpendGiftCard(item) },
                                            onShowQr = { onShowUpiQr(item.cardNumberOrUpi, item.providerOrName) },
                                            onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                            onToggleMarkedAsUsed = { onToggleMarkedAsUsed(item.id) }
                                        )
                                    }
                                }

                                if (usedItems.isNotEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isUsedExpanded = !isUsedExpanded },
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isUsedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                        contentDescription = "Expand/Collapse Used"
                                                    )
                                                    Text(
                                                        text = "Used (${usedItems.size})",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                                    Text(
                                                        text = "${usedItems.size}",
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (isUsedExpanded) {
                                        items(usedItems, key = { it.id }) { item ->
                                            val member = uiState.members.find { it.id == item.memberId }
                                            WalletOrGiftCardItem(
                                                item = item,
                                                memberName = member?.name,
                                                onEdit = { onOpenEditWalletOrGiftCard(item) },
                                                onDelete = { itemToDelete = item },
                                                onSpend = { onOpenSpendGiftCard(item) },
                                                onShowQr = { onShowUpiQr(item.cardNumberOrUpi, item.providerOrName) },
                                                onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                                onToggleMarkedAsUsed = { onToggleMarkedAsUsed(item.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        DisplayMode.LIST -> {
                            var isUsedExpanded by remember { mutableStateOf(false) }
                            val activeItems = displayedItems.filter { !it.isGiftCard || !it.isMarkedAsUsed }
                            val usedItems = displayedItems.filter { it.isGiftCard && it.isMarkedAsUsed }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("wallets_screen"),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                if (activeItems.isNotEmpty()) {
                                    item(key = "active_header") {
                                        Text(
                                            text = "Active (${activeItems.size})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                    items(activeItems, key = { it.id }) { item ->
                                        val member = uiState.members.find { it.id == item.memberId }
                                        WalletOrGiftCardItem(
                                            item = item,
                                            memberName = member?.name,
                                            onEdit = { onOpenEditWalletOrGiftCard(item) },
                                            onDelete = { itemToDelete = item },
                                            onSpend = { onOpenSpendGiftCard(item) },
                                            onShowQr = { onShowUpiQr(item.cardNumberOrUpi, item.providerOrName) },
                                            onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                            onToggleMarkedAsUsed = { onToggleMarkedAsUsed(item.id) }
                                        )
                                    }
                                }

                                if (usedItems.isNotEmpty()) {
                                    item(key = "used_header") {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isUsedExpanded = !isUsedExpanded },
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isUsedExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                        contentDescription = "Expand/Collapse Used"
                                                    )
                                                    Text(
                                                        text = "Used (${usedItems.size})",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                                    Text(
                                                        text = "${usedItems.size}",
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (isUsedExpanded) {
                                        items(usedItems, key = { it.id }) { item ->
                                            val member = uiState.members.find { it.id == item.memberId }
                                            WalletOrGiftCardItem(
                                                item = item,
                                                memberName = member?.name,
                                                onEdit = { onOpenEditWalletOrGiftCard(item) },
                                                onDelete = { itemToDelete = item },
                                                onSpend = { onOpenSpendGiftCard(item) },
                                                onShowQr = { onShowUpiQr(item.cardNumberOrUpi, item.providerOrName) },
                                                onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                                onToggleMarkedAsUsed = { onToggleMarkedAsUsed(item.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                if (selectedTabIndex == 3) {
                    onOpenAddSubscription()
                } else {
                    onOpenAddWallet()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("add_wallet_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }

    // Delete Confirmation Dialog for Wallet/GiftCard
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete ${if (itemToDelete?.isGiftCard == true) "Gift Card" else "Wallet"}") },
            text = { Text("Are you sure you want to remove \"${itemToDelete?.providerOrName}\" from your family vault?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { onDeleteWalletOrGiftCard(it.id) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog for Subscription
    if (subscriptionToDelete != null) {
        AlertDialog(
            onDismissRequest = { subscriptionToDelete = null },
            title = { Text("Delete Subscription") },
            text = { Text("Are you sure you want to remove \"${subscriptionToDelete?.name}\" subscription?") },
            confirmButton = {
                Button(
                    onClick = {
                        subscriptionToDelete?.let { onDeleteSubscription(it.id) }
                        subscriptionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { subscriptionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SubscriptionCard(
    subscription: Subscription,
    memberName: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = Color(subscription.colorHex)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Icon, Service Name, Plan, Edit & Delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Subscriptions,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = subscription.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (subscription.planName.isNotBlank()) {
                            Text(
                                text = subscription.planName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Cost & Billing Frequency Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("COST", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "₹${subscription.cost.toInt()} / ${subscription.billingCycle}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (subscription.nextRenewalDate.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text("RENEWAL", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(subscription.nextRenewalDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            }

            // Linked Payment & Member footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subscription.linkedPaymentMethod.isNotBlank()) {
                    Text(
                        text = "💳 Paid via: ${subscription.linkedPaymentMethod}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (!memberName.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = accentColor, modifier = Modifier.size(12.dp))
                        Text(memberName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = accentColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletOrGiftCardItem(
    item: WalletOrGiftCard,
    memberName: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSpend: () -> Unit,
    onShowQr: () -> Unit,
    onCopyText: (String, String) -> Unit,
    onToggleMarkedAsUsed: () -> Unit = {}
) {
    val accentColor = Color(item.colorHex)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (item.isMarkedAsUsed) MaterialTheme.colorScheme.outlineVariant else accentColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isMarkedAsUsed) 0.dp else 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Icon, Provider/Brand, Status Chip, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.surfaceVariant else accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (item.isGiftCard) Icons.Filled.CardGiftcard else Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                tint = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.onSurfaceVariant else accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = item.providerOrName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.surfaceVariant else accentColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (item.isMarkedAsUsed) MaterialTheme.colorScheme.outlineVariant else accentColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (item.isGiftCard) {
                                    if (item.isMarkedAsUsed) "Gift Card • USED"
                                    else "Gift Card • Balance ₹${item.currentBalance.toInt()}"
                                } else "Online Wallet • ${item.kycStatus}",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.onSurfaceVariant else accentColor
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!item.isGiftCard && item.cardNumberOrUpi.isNotBlank()) {
                        IconButton(onClick = onShowQr, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.QrCode, contentDescription = "Show QR", tint = accentColor, modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Gift Card Remaining Balance Tracker with Spend & Mark-Used Actions
            if (item.isGiftCard) {
                val totalAmt = if (item.initialAmount > 0.0) item.initialAmount else (if (item.amount > 0.0) item.amount else item.currentBalance)
                val balanceRatio = if (totalAmt > 0) (item.currentBalance / totalAmt).coerceIn(0.0, 1.0).toFloat() else 0f
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else accentColor.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, if (item.isMarkedAsUsed) MaterialTheme.colorScheme.outlineVariant else accentColor.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("REMAINING BALANCE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${item.currentBalance.toInt()} / ₹${totalAmt.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.onSurfaceVariant else accentColor)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Toggle Marked as Used Button
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(1.dp, if (item.isMarkedAsUsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.clickable { onToggleMarkedAsUsed() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (item.isMarkedAsUsed) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (item.isMarkedAsUsed) "Used ✓" else "Mark Used",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                            color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (!item.isMarkedAsUsed) {
                                    OutlinedButton(
                                        onClick = onSpend,
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("- Spend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { balanceRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (item.isMarkedAsUsed) MaterialTheme.colorScheme.outlineVariant else if (balanceRatio > 0.2f) accentColor else MaterialTheme.colorScheme.error,
                            trackColor = accentColor.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // Identifier / Code Row
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (item.isGiftCard && item.giftCardPin.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.1f)) {
                            Text(
                                text = "VOUCHER CODE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = item.cardNumberOrUpi,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { onCopyText("Voucher Code", item.cardNumberOrUpi) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Voucher", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        }

                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(accentColor.copy(alpha = 0.3f)))

                        Column(modifier = Modifier.weight(0.9f)) {
                            Text(
                                text = "REDEEM PIN",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = item.giftCardPin,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { onCopyText("Redeem PIN", item.giftCardPin) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy PIN", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (item.isGiftCard) "VOUCHER CODE" else "VPA / UPI ID / PHONE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = item.cardNumberOrUpi,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { onCopyText(if (item.isGiftCard) "Voucher Code" else "UPI ID", item.cardNumberOrUpi) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Vendor Name (if available)
            if (item.isGiftCard && item.vendorName.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Purchased from: ${item.vendorName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Additional details: Expiry, Mode of Redemption, Remarks
            if (item.isGiftCard) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (item.expiryDate.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("EXPIRES", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (item.remindExpiry) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = "Expiry Alert On", tint = accentColor, modifier = Modifier.size(11.dp))
                                    }
                                }
                                Text(item.expiryDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("REDEMPTION", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(item.modeOfRedemption, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                if (item.remarks.isNotBlank()) {
                    Text(
                        text = "Note: ${item.remarks}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                if (item.registeredMobile.isNotBlank()) {
                    Text(
                        text = "Linked Mobile: ${item.registeredMobile}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Member Badge Footer
            if (!memberName.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                    Text(memberName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = accentColor)
                }
            }
        }
    }
}

@Composable
private fun EmptyWalletsAndGiftCardsView(onAddItem: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CardGiftcard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Wallets or Gift Cards in Vault",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Keep your Amazon, Apple, Flipkart gift cards, voucher codes, and Paytm/PhonePe/GPay wallets organized in one unified vault.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(onClick = onAddItem, shape = RoundedCornerShape(12.dp)) {
                Text("+ Add Wallet / Gift Card")
            }
        }
    }
}

@Composable
private fun EmptySubscriptionsView(onAddSubscription: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Subscriptions,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Recurring Subscriptions Tracked",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Track shared family OTT, music, cloud storage and utility bills (Netflix, Spotify, Prime, YouTube, Broadband) with renewal alerts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(onClick = onAddSubscription, shape = RoundedCornerShape(12.dp)) {
                Text("+ Add Family Subscription")
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val prefs = VaultPreferencesManager(context)
    val autoClear = prefs.isClipboardAutoClearEnabled()
    val timeout = prefs.getClipboardClearTimeout()
    SafeVaultShareManager.copyWithSecurity(
        context = context,
        label = label,
        text = text,
        timeoutSeconds = timeout,
        enableAutoClear = autoClear
    )
}
