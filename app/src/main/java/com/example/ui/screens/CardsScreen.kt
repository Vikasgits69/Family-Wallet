package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CardNetwork
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.DisplayMode
import com.example.ui.components.InteractiveCreditCardItem
import com.example.ui.components.InteractiveDebitCardItem
import com.example.ui.viewmodel.FamilyWalletUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    uiState: FamilyWalletUiState,
    onOpenAddCreditCard: () -> Unit,
    onOpenAddDebitCard: () -> Unit,
    onOpenEditCreditCard: (CreditCard) -> Unit,
    onOpenEditDebitCard: (DebitCard) -> Unit,
    onToggleCardFlip: (String) -> Unit,
    onToggleItemMask: (String) -> Unit,
    onDeleteCreditCard: (String) -> Unit,
    onDeleteDebitCard: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("All Cards", "Credit", "Debit", "RuPay")

    var cardToDelete by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // (id, isCredit)
    val context = LocalContext.current

    // Immediate zero-latency rendering
    val isLoaded = true

    // Cards Content
    val creditList by remember(uiState.filteredCreditCards, selectedTabIndex) {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> uiState.filteredCreditCards
                1 -> uiState.filteredCreditCards
                2 -> emptyList()
                3 -> uiState.filteredCreditCards.filter { it.network == CardNetwork.RUPAY }
                else -> uiState.filteredCreditCards
            }
        }
    }

    val debitList by remember(uiState.filteredDebitCards, selectedTabIndex) {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> uiState.filteredDebitCards
                1 -> emptyList()
                2 -> uiState.filteredDebitCards
                3 -> uiState.filteredDebitCards.filter { it.network == CardNetwork.RUPAY }
                else -> uiState.filteredDebitCards
            }
        }
    }

    val isVaultEmpty by remember(creditList, debitList) {
        derivedStateOf { creditList.isEmpty() && debitList.isEmpty() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Selector
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
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

            if (isVaultEmpty) {
                EmptyCardsView(
                    onAddCredit = onOpenAddCreditCard,
                    onAddDebit = onOpenAddDebitCard
                )
            } else {
                when (uiState.displayMode) {
                    DisplayMode.CAROUSEL -> {
                        // Material 3 HorizontalPager Carousel View with 3D Flip Cards & Page Indicators
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            if (creditList.isNotEmpty()) {
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "CREDIT CARDS (${creditList.size})",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Swipe • Tap to Flip 3D",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        val creditPagerState = rememberPagerState(pageCount = { creditList.size })
                                        HorizontalPager(
                                            state = creditPagerState,
                                            contentPadding = PaddingValues(horizontal = 24.dp),
                                            pageSpacing = 16.dp,
                                            modifier = Modifier.fillMaxWidth()
                                        ) { page ->
                                            val card = creditList[page]
                                            val member = uiState.members.find { it.id == card.memberId }
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                InteractiveCreditCardItem(
                                                    card = card,
                                                    isFlipped = uiState.isCardFlipped(card.id),
                                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                                    onFlip = { onToggleCardFlip(card.id) },
                                                    onToggleMask = { onToggleItemMask(card.id) },
                                                    memberName = member?.name,
                                                    onEdit = { onOpenEditCreditCard(card) },
                                                    onDelete = { cardToDelete = Pair(card.id, true) }
                                                )
                                            }
                                        }

                                        // Page Indicator Dots
                                        if (creditList.size > 1) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                repeat(creditList.size) { index ->
                                                    val isSelected = creditPagerState.currentPage == index
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(horizontal = 3.dp)
                                                            .size(if (isSelected) 8.dp else 6.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                                else MaterialTheme.colorScheme.outlineVariant
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (debitList.isNotEmpty()) {
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "DEBIT CARDS (${debitList.size})",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Swipe • Tap to Flip 3D",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        val debitPagerState = rememberPagerState(pageCount = { debitList.size })
                                        HorizontalPager(
                                            state = debitPagerState,
                                            contentPadding = PaddingValues(horizontal = 24.dp),
                                            pageSpacing = 16.dp,
                                            modifier = Modifier.fillMaxWidth()
                                        ) { page ->
                                            val card = debitList[page]
                                            val member = uiState.members.find { it.id == card.memberId }
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                InteractiveDebitCardItem(
                                                    card = card,
                                                    isFlipped = uiState.isCardFlipped(card.id),
                                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                                    onFlip = { onToggleCardFlip(card.id) },
                                                    onToggleMask = { onToggleItemMask(card.id) },
                                                    memberName = member?.name,
                                                    onEdit = { onOpenEditDebitCard(card) },
                                                    onDelete = { cardToDelete = Pair(card.id, false) }
                                                )
                                            }
                                        }

                                        // Page Indicator Dots
                                        if (debitList.size > 1) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                repeat(debitList.size) { index ->
                                                    val isSelected = debitPagerState.currentPage == index
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(horizontal = 3.dp)
                                                            .size(if (isSelected) 8.dp else 6.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                                else MaterialTheme.colorScheme.outlineVariant
                                                            )
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
                        // Grid View
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 320.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(creditList, key = { it.id }) { card ->
                                val member = uiState.members.find { it.id == card.memberId }
                                InteractiveCreditCardItem(
                                    card = card,
                                    isFlipped = uiState.isCardFlipped(card.id),
                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                    onFlip = { onToggleCardFlip(card.id) },
                                    onToggleMask = { onToggleItemMask(card.id) },
                                    memberName = member?.name,
                                    onEdit = { onOpenEditCreditCard(card) },
                                    onDelete = { cardToDelete = Pair(card.id, true) }
                                )
                            }
                            items(debitList, key = { it.id }) { card ->
                                val member = uiState.members.find { it.id == card.memberId }
                                InteractiveDebitCardItem(
                                    card = card,
                                    isFlipped = uiState.isCardFlipped(card.id),
                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                    onFlip = { onToggleCardFlip(card.id) },
                                    onToggleMask = { onToggleItemMask(card.id) },
                                    memberName = member?.name,
                                    onEdit = { onOpenEditDebitCard(card) },
                                    onDelete = { cardToDelete = Pair(card.id, false) }
                                )
                            }
                        }
                    }

                    DisplayMode.LIST -> {
                        // List View: Full vertical layout
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            if (creditList.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "CREDIT CARDS (${creditList.size})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(creditList, key = { it.id }) { card ->
                                    val member = uiState.members.find { it.id == card.memberId }
                                    InteractiveCreditCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name,
                                        onEdit = { onOpenEditCreditCard(card) },
                                        onDelete = { cardToDelete = Pair(card.id, true) }
                                    )
                                }
                            }

                            if (debitList.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "DEBIT CARDS (${debitList.size})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(debitList, key = { it.id }) { card ->
                                    val member = uiState.members.find { it.id == card.memberId }
                                    InteractiveDebitCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name,
                                        onEdit = { onOpenEditDebitCard(card) },
                                        onDelete = { cardToDelete = Pair(card.id, false) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Add Card Floating Action Button
        if (!isVaultEmpty) {
            var showAddOptions by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showAddOptions) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            showAddOptions = false
                            onOpenAddCreditCard()
                        },
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                        text = { Text("+ Credit Card", fontWeight = FontWeight.Bold) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    ExtendedFloatingActionButton(
                        onClick = {
                            showAddOptions = false
                            onOpenAddDebitCard()
                        },
                        icon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
                        text = { Text("+ Debit Card", fontWeight = FontWeight.Bold) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                FloatingActionButton(
                    onClick = {
                        when (selectedTabIndex) {
                            1 -> onOpenAddCreditCard()
                            2 -> onOpenAddDebitCard()
                            else -> showAddOptions = !showAddOptions
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_card_fab")
                ) {
                    Icon(
                        imageVector = if (showAddOptions) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Card"
                    )
                }
            }
        }
    }

    // Delete Card Confirmation Dialog
    cardToDelete?.let { (id, isCredit) ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text(if (isCredit) "Delete Credit Card?" else "Delete Debit Card?") },
            text = { Text("Are you sure you want to delete this card from the vault? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (isCredit) onDeleteCreditCard(id) else onDeleteDebitCard(id)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyCardsView(
    onAddCredit: () -> Unit,
    onAddDebit: () -> Unit
) {
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
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Cards in Vault",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Securely vault RuPay, Visa, Mastercard, or Amex credit and debit cards with 3D flip interactive previews and reminder alerts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onAddCredit, shape = RoundedCornerShape(12.dp)) {
                    Text("+ Add Credit Card")
                }
                FilledTonalButton(onClick = onAddDebit, shape = RoundedCornerShape(12.dp)) {
                    Text("+ Add Debit Card")
                }
            }
        }
    }
}
