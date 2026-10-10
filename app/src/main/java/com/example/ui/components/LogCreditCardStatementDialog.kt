package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CreditCard
import com.example.data.CreditCardStatementLog
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun LogCreditCardStatementDialog(
    card: CreditCard,
    onDismiss: () -> Unit,
    onSaveStatement: (CreditCardStatementLog) -> Unit
) {
    val previousLog = card.parsedStatementLogs.firstOrNull()
    val defaultOpeningBalance = previousLog?.closingBalance ?: card.statementClosingBalance
    val defaultOpeningPoints = previousLog?.closingRewardPoints ?: (if (card.ccRewardPoints > 0) card.ccRewardPoints else card.statementClosingRewardPoints)

    // Current month suggestion
    val currentMonthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    var statementMonth by remember { mutableStateOf(card.currentStatementMonth.ifBlank { currentMonthFormat }) }
    var openingBalanceText by remember { mutableStateOf(if (defaultOpeningBalance > 0) defaultOpeningBalance.toInt().toString() else "0") }
    var totalExpensesText by remember { mutableStateOf(if (card.statementTotalExpenses > 0) card.statementTotalExpenses.toInt().toString() else "") }
    var totalPaymentsText by remember { mutableStateOf(if (card.statementTotalPayments > 0) card.statementTotalPayments.toInt().toString() else "") }

    var openingPointsText by remember { mutableStateOf(defaultOpeningPoints.toString()) }
    var rewardPointsEarnedText by remember { mutableStateOf(if (card.statementRewardPointsEarned > 0) card.statementRewardPointsEarned.toString() else "") }
    var rewardPointsRedeemedText by remember { mutableStateOf(if (card.statementRewardPointsRedeemed > 0) card.statementRewardPointsRedeemed.toString() else "") }

    var notesText by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }

    // Numeric calculations
    val openingBal = openingBalanceText.toDoubleOrNull() ?: 0.0
    val expenses = totalExpensesText.toDoubleOrNull() ?: 0.0
    val payments = totalPaymentsText.toDoubleOrNull() ?: 0.0
    val closingBal = openingBal + expenses - payments

    val openingPts = openingPointsText.toLongOrNull() ?: 0L
    val earnedPts = rewardPointsEarnedText.toLongOrNull() ?: 0L
    val redeemedPts = rewardPointsRedeemedText.toLongOrNull() ?: 0L
    val closingPts = (openingPts + earnedPts - redeemedPts).coerceAtLeast(0L)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 480.dp)
                .padding(vertical = 20.dp)
                .imePadding(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
            border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
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
                            color = IndigoAccent.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = IndigoAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Log Statement Cycle",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${card.bankName} ${card.cardName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = IndigoAccent
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Quick Month Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "STATEMENT MONTH / CYCLE *",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = statementMonth,
                        onValueChange = { statementMonth = it },
                        placeholder = { Text("e.g. October 2026") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Month shortcut pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("October 2026", "November 2026", "December 2026").forEach { monthPill ->
                            Surface(
                                onClick = { statementMonth = monthPill },
                                shape = RoundedCornerShape(8.dp),
                                color = if (statementMonth == monthPill) IndigoAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (statementMonth == monthPill) IndigoAccent else Color.Transparent)
                            ) {
                                Text(
                                    text = monthPill,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (statementMonth == monthPill) IndigoAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // SECTION 1: Expenses & Payments
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "EXPENSES & PAYMENTS ACCOUNTING",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = openingBalanceText,
                                onValueChange = { openingBalanceText = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("Carried Fwd (₹)") },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = totalExpensesText,
                                onValueChange = { totalExpensesText = it.filter { c -> c.isDigit() || c == '.' } },
                                label = { Text("Expenses (₹) *") },
                                placeholder = { Text("e.g. 24000") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        OutlinedTextField(
                            value = totalPaymentsText,
                            onValueChange = { totalPaymentsText = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Total Payments Made This Cycle (₹)") },
                            placeholder = { Text("e.g. 20000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Resulting Closing Balance Display Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (closingBal > 0) IndigoAccent.copy(alpha = 0.12f) else EmeraldMint.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (closingBal > 0) IndigoAccent.copy(alpha = 0.4f) else EmeraldMint.copy(alpha = 0.5f)),
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
                                    Text(
                                        text = "NET OUTSTANDING BALANCE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = if (closingBal > 0) IndigoAccent else EmeraldMint
                                    )
                                    Text(
                                        text = "Carries over to next statement cycle",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "₹${closingBal.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = if (closingBal > 0) IndigoAccent else EmeraldMint
                                )
                            }
                        }
                    }
                }

                // SECTION 2: Reward Points Accounting
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                            Text(
                                text = "REWARD POINTS RECONCILIATION",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                                color = Color(0xFFD97706)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = openingPointsText,
                                onValueChange = { openingPointsText = it.filter { c -> c.isDigit() } },
                                label = { Text("Opening Pts") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = rewardPointsEarnedText,
                                onValueChange = { rewardPointsEarnedText = it.filter { c -> c.isDigit() } },
                                label = { Text("+ Earned") },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = rewardPointsRedeemedText,
                                onValueChange = { rewardPointsRedeemedText = it.filter { c -> c.isDigit() } },
                                label = { Text("- Redeemed") },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.1f)
                            )
                        }

                        // Closing Points Summary
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFD97706).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CLOSING REWARD POINTS BALANCE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = "$closingPts pts",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }

                // Optional Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Remarks / Statement Notes (Optional)") },
                    placeholder = { Text("e.g. Paid full bill via CRED; includes flight tickets") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // History Toggle
                if (card.parsedStatementLogs.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHistory = !showHistory }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Previous Statement Cycles (${card.parsedStatementLogs.size})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = if (showHistory) "Hide ▲" else "View ▼",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = showHistory) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            card.parsedStatementLogs.forEach { logItem ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(logItem.statementMonth, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text("Balance: ₹${logItem.closingBalance.toInt()}", fontWeight = FontWeight.Black, color = IndigoAccent)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Expenses: ₹${logItem.totalExpenses.toInt()} | Paid: ₹${logItem.totalPayments.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${logItem.closingRewardPoints} pts", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFD97706))
                                        }
                                        if (logItem.notes.isNotBlank()) {
                                            Text("Note: ${logItem.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (statementMonth.isNotBlank()) {
                                val log = CreditCardStatementLog(
                                    statementMonth = statementMonth.trim(),
                                    openingBalance = openingBal,
                                    totalExpenses = expenses,
                                    totalPayments = payments,
                                    closingBalance = closingBal,
                                    openingRewardPoints = openingPts,
                                    rewardPointsEarned = earnedPts,
                                    rewardPointsRedeemedOrLapsed = redeemedPts,
                                    closingRewardPoints = closingPts,
                                    notes = notesText.trim()
                                )
                                onSaveStatement(log)
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Statement Cycle", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
