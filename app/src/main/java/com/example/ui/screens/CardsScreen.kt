package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CardNetwork
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.ui.components.InteractiveCreditCardItem
import com.example.ui.components.InteractiveDebitCardItem
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import com.example.ui.viewmodel.FamilyWalletUiState
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.delay

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    uiState: FamilyWalletUiState,
    onOpenAddCreditCard: () -> Unit,
    onOpenAddDebitCard: () -> Unit,
    onToggleCardFlip: (String) -> Unit,
    onToggleItemMask: (String) -> Unit,
    onDeleteCreditCard: (String) -> Unit,
    onDeleteDebitCard: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("All Cards", "Credit Cards", "Debit Cards", "RuPay Only")

    var cardToDelete by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // (id, isCredit)
    val context = LocalContext.current

    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        isLoaded = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Selector
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
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

            // Cards Content (optimized with remember & derivedStateOf)
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

            if (isVaultEmpty) {
                EmptyCardsView(
                    onAddCredit = onOpenAddCreditCard,
                    onAddDebit = onOpenAddDebitCard
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Credit Cards Section
                    if (creditList.isNotEmpty()) {
                        item {
                            Text(
                                text = "CREDIT CARDS (${creditList.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(creditList, key = { it.id }) { card ->
                            AnimatedVisibility(
                                visible = isLoaded,
                                enter = slideInVertically(initialOffsetY = { 40 }) + fadeIn()
                            ) {
                                CreditCardDetailedItem(
                                    card = card,
                                    isFlipped = uiState.isCardFlipped(card.id),
                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                    onFlip = { onToggleCardFlip(card.id) },
                                    onToggleMask = { onToggleItemMask(card.id) },
                                    onDelete = { cardToDelete = Pair(card.id, true) },
                                    memberName = uiState.members.find { it.id == card.memberId }?.name
                                )
                            }
                        }
                    }

                    // Debit Cards Section
                    if (debitList.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "DEBIT CARDS (${debitList.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(debitList, key = { it.id }) { card ->
                            AnimatedVisibility(
                                visible = isLoaded,
                                enter = slideInVertically(initialOffsetY = { 40 }) + fadeIn()
                            ) {
                                DebitCardDetailedItem(
                                    card = card,
                                    isFlipped = uiState.isCardFlipped(card.id),
                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                    onFlip = { onToggleCardFlip(card.id) },
                                    onToggleMask = { onToggleItemMask(card.id) },
                                    onDelete = { cardToDelete = Pair(card.id, false) },
                                    memberName = uiState.members.find { it.id == card.memberId }?.name
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Card
        FloatingActionButton(
            onClick = {
                if (selectedTabIndex == 2) onOpenAddDebitCard() else onOpenAddCreditCard()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_card_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
                Text(
                    text = if (selectedTabIndex == 2) "Add Debit" else "Add Card",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (cardToDelete != null) {
        val (id, isCredit) = cardToDelete!!
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Remove Card from Vault?") },
            text = { Text("Are you sure you want to remove this card? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (isCredit) onDeleteCreditCard(id) else onDeleteDebitCard(id)
                        cardToDelete = null
                        Toast.makeText(context, "Card removed from vault", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
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
private fun CreditCardDetailedItem(
    card: CreditCard,
    isFlipped: Boolean,
    isUnmasked: Boolean,
    onFlip: () -> Unit,
    onToggleMask: () -> Unit,
    onDelete: () -> Unit,
    memberName: String?
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 3D Flippable Graphic Card
        InteractiveCreditCardItem(
            card = card,
            isFlipped = isFlipped,
            isUnmasked = isUnmasked,
            onFlip = onFlip,
            onToggleMask = onToggleMask,
            memberName = memberName
        )

        // Informational Details Card (Below Graphic)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Billing: ${card.statementDate} • Due: ${card.dueDate}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Card",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Credit Limit: ${CurrencyFormatter.formatRupees(card.creditLimit, false)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Annual Fee: ${CurrencyFormatter.formatRupees(card.annualFee, false)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (card.waiverCondition.isNotBlank()) {
                    Text(
                        text = "💡 ${card.waiverCondition}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DebitCardDetailedItem(
    card: DebitCard,
    isFlipped: Boolean,
    isUnmasked: Boolean,
    onFlip: () -> Unit,
    onToggleMask: () -> Unit,
    onDelete: () -> Unit,
    memberName: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InteractiveDebitCardItem(
            card = card,
            isFlipped = isFlipped,
            isUnmasked = isUnmasked,
            onFlip = onFlip,
            onToggleMask = onToggleMask,
            memberName = memberName
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Text(
                        text = "Linked to ${card.linkedAccount}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Card",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ATM Limit: ${CurrencyFormatter.formatRupees(card.atmLimit, false)}/day",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldMint
                    )

                    Text(
                        text = "POS Limit: ${CurrencyFormatter.formatRupees(card.posLimit, false)}/day",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCardsView(
    onAddCredit: () -> Unit,
    onAddDebit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CreditCard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Cards Found in Vault",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Store your RuPay, VISA, Mastercard and Amex cards with 3D flip interactive details.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onAddCredit) {
                Text("+ Add Credit Card")
            }

            OutlinedButton(onClick = onAddDebit) {
                Text("+ Add Debit Card")
            }
        }
    }
}
