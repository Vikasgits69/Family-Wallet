package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CardNetwork
import com.example.data.CardThemeColor
import com.example.data.FamilyMember

data class CardPreset(
    val bankName: String,
    val cardName: String,
    val network: CardNetwork,
    val themeColor: CardThemeColor,
    val creditLimit: Double = 300000.0,
    val annualFee: Double = 999.0,
    val waiverCondition: String = "Spend ₹1,50,000 annually to waive fee"
)

val defaultCardPresets = listOf(
    CardPreset("HDFC Bank", "Tata Neu Infinity RuPay", CardNetwork.RUPAY, CardThemeColor.CHARCOAL, 400000.0, 1499.0, "Spend ₹3,00,000 annually to waive fee"),
    CardPreset("ICICI Bank", "Coral RuPay Credit Card", CardNetwork.RUPAY, CardThemeColor.RUBY, 250000.0, 500.0, "Spend ₹1,50,000 annually to waive fee"),
    CardPreset("SBI Card", "SimplySAVE RuPay", CardNetwork.RUPAY, CardThemeColor.SAPPHIRE, 200000.0, 499.0, "Spend ₹1,00,000 annually to waive fee"),
    CardPreset("Axis Bank", "Axis Bank Magnus VISA", CardNetwork.VISA, CardNetwork.VISA.let { CardThemeColor.TITANIUM }, 750000.0, 12500.0, "Spend ₹15,00,000 for waiver"),
    CardPreset("HDFC Bank", "Regalia Gold RuPay", CardNetwork.RUPAY, CardThemeColor.INDIGO, 500000.0, 2500.0, "Spend ₹4,00,000 annually to waive fee"),
    CardPreset("Amex", "Platinum Travel Card", CardNetwork.AMEX, CardThemeColor.CHARCOAL, 500000.0, 5000.0, "Spend ₹4,00,000 for milestone benefits")
)

@Composable
fun AddCardDialog(
    members: List<FamilyMember>,
    initialIsCredit: Boolean = true,
    onDismiss: () -> Unit,
    onAddCreditCard: (
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
        themeColor: CardThemeColor
    ) -> Unit,
    onAddDebitCard: (
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
        themeColor: CardThemeColor
    ) -> Unit
) {
    var isCreditCard by remember { mutableStateOf(initialIsCredit) }
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }

    var bankName by remember { mutableStateOf("HDFC Bank") }
    var cardName by remember { mutableStateOf("Tata Neu Infinity RuPay") }
    var linkedAccount by remember { mutableStateOf("HDFC Salary A/c •••• 5678") }
    var network by remember { mutableStateOf(CardNetwork.RUPAY) }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("10/29") }
    var cvv by remember { mutableStateOf("482") }
    var cardholderName by remember { mutableStateOf("VIKAS GUPTA") }
    var statementDate by remember { mutableStateOf("15th of every month") }
    var dueDate by remember { mutableStateOf("5th of every month") }
    var creditLimitText by remember { mutableStateOf("300000") }
    var annualFeeText by remember { mutableStateOf("999") }
    var waiverCondition by remember { mutableStateOf("Spend ₹1,50,000 annually to waive fee") }
    var atmLimitText by remember { mutableStateOf("50000") }
    var posLimitText by remember { mutableStateOf("200000") }
    var selectedThemeColor by remember { mutableStateOf(CardThemeColor.CHARCOAL) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isCreditCard) "Add Credit Card to Vault" else "Add Debit Card to Vault",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Google Wallet Material Design • ₹ RuPay & Global Cards",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Type Selector: Credit vs Debit
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = isCreditCard,
                        onClick = { isCreditCard = true },
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    ) {
                        Text("Credit Card", fontWeight = FontWeight.Bold)
                    }

                    SegmentedButton(
                        selected = !isCreditCard,
                        onClick = { isCreditCard = false },
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                    ) {
                        Text("Debit Card", fontWeight = FontWeight.Bold)
                    }
                }

                // Presets (RuPay Spotlight)
                if (isCreditCard) {
                    Text(
                        text = "POPULAR INDIAN PRESETS (RUPAY / VISA)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(defaultCardPresets) { preset ->
                            FilterChip(
                                selected = (cardName == preset.cardName),
                                onClick = {
                                    bankName = preset.bankName
                                    cardName = preset.cardName
                                    network = preset.network
                                    selectedThemeColor = preset.themeColor
                                    creditLimitText = preset.creditLimit.toLong().toString()
                                    annualFeeText = preset.annualFee.toLong().toString()
                                    waiverCondition = preset.waiverCondition
                                },
                                label = {
                                    Text(
                                        text = "${preset.cardName} (${preset.network.label})",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                // Family Member Picker
                Text(
                    text = "ASSIGN TO FAMILY MEMBER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(members) { member ->
                        FilterChip(
                            selected = (selectedMemberId == member.id),
                            onClick = { selectedMemberId = member.id },
                            label = { Text("${member.avatarEmoji} ${member.name}") }
                        )
                    }
                }

                // Bank & Card Details
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank / Issuer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isCreditCard) {
                    OutlinedTextField(
                        value = cardName,
                        onValueChange = { cardName = it },
                        label = { Text("Card Product Name") },
                        placeholder = { Text("e.g., Tata Neu Infinity RuPay") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = linkedAccount,
                        onValueChange = { linkedAccount = it },
                        label = { Text("Linked Bank Account") },
                        placeholder = { Text("e.g., HDFC Salary A/c •••• 9812") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Card Network Selection
                Text(
                    text = "PAYMENT NETWORK",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CardNetwork.values().forEach { net ->
                        FilterChip(
                            selected = network == net,
                            onClick = { network = net },
                            label = { Text(net.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Card Number & Cardholder
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 19) cardNumber = it },
                    label = { Text("Card Number (16 Digits)") },
                    placeholder = { Text("4532 8901 2345 7890") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cardholderName,
                    onValueChange = { cardholderName = it },
                    label = { Text("Cardholder Name (as printed on card)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Expiry & CVV
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it },
                        label = { Text("Expiry (MM/YY)") },
                        placeholder = { Text("08/29") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 4) cvv = it },
                        label = { Text("CVV / CVC") },
                        placeholder = { Text("842") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Specific fields for Credit vs Debit
                if (isCreditCard) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = statementDate,
                            onValueChange = { statementDate = it },
                            label = { Text("Statement Date") },
                            placeholder = { Text("15th of month") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Due Date") },
                            placeholder = { Text("5th of month") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = creditLimitText,
                            onValueChange = { creditLimitText = it },
                            label = { Text("Credit Limit (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = annualFeeText,
                            onValueChange = { annualFeeText = it },
                            label = { Text("Annual Fee (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = waiverCondition,
                        onValueChange = { waiverCondition = it },
                        label = { Text("Fee Waiver / Milestone Rule") },
                        placeholder = { Text("Spend ₹1,50,000 annually to waive renewal fee") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = atmLimitText,
                            onValueChange = { atmLimitText = it },
                            label = { Text("ATM Daily Limit (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = posLimitText,
                            onValueChange = { posLimitText = it },
                            label = { Text("POS/Online Limit (₹)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Theme Color Palette
                Text(
                    text = "CARD DESIGN COLOR BLOCK",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CardThemeColor.values()) { colorTheme ->
                        val isSelected = selectedThemeColor == colorTheme
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorTheme.hexCode))
                                .border(
                                    if (isSelected) 3.dp else 1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .clickable { selectedThemeColor = colorTheme }
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanNumber = cardNumber.replace(" ", "")
                    if (cleanNumber.length < 8) {
                        errorMessage = "Please enter a valid card number (min 8 digits)"
                        return@Button
                    }
                    if (bankName.isBlank()) {
                        errorMessage = "Please enter issuer bank name"
                        return@Button
                    }

                    if (isCreditCard) {
                        val limit = creditLimitText.toDoubleOrNull() ?: 200000.0
                        val fee = annualFeeText.toDoubleOrNull() ?: 0.0
                        onAddCreditCard(
                            bankName,
                            cardName,
                            network,
                            cleanNumber,
                            expiry,
                            cvv,
                            cardholderName,
                            statementDate,
                            dueDate,
                            limit,
                            fee,
                            waiverCondition,
                            selectedMemberId,
                            selectedThemeColor
                        )
                    } else {
                        val atmLimit = atmLimitText.toDoubleOrNull() ?: 50000.0
                        val posLimit = posLimitText.toDoubleOrNull() ?: 200000.0
                        onAddDebitCard(
                            bankName,
                            linkedAccount,
                            network,
                            cleanNumber,
                            expiry,
                            cvv,
                            cardholderName,
                            atmLimit,
                            posLimit,
                            selectedMemberId,
                            selectedThemeColor
                        )
                    }
                }
            ) {
                Text(if (isCreditCard) "Save Credit Card" else "Save Debit Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
