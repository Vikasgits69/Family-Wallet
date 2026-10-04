package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.CardNetwork
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.FamilyMember
import com.example.ui.components.CardNetworkBadge
import com.example.ui.components.EmvChipGraphic
import com.example.ui.components.getCardBackgroundBrush
import com.example.ui.theme.CardColorBlockOptions
import com.example.util.CardNumberVisualTransformation
import com.example.util.ExpiryDateVisualTransformation
import java.util.UUID

@Composable
fun AddCardDialog(
    members: List<FamilyMember>,
    creditCardToEdit: CreditCard? = null,
    debitCardToEdit: DebitCard? = null,
    initialIsCredit: Boolean = true,
    onDismiss: () -> Unit,
    onSaveCreditCard: (CreditCard) -> Unit,
    onSaveDebitCard: (DebitCard) -> Unit
) {
    val isEditingCredit = creditCardToEdit != null
    val isEditingDebit = debitCardToEdit != null
    val isEditing = isEditingCredit || isEditingDebit

    var isCreditCard by remember {
        mutableStateOf(if (isEditing) isEditingCredit else initialIsCredit)
    }

    var bankName by remember {
        mutableStateOf(creditCardToEdit?.bankName ?: debitCardToEdit?.bankName ?: "")
    }
    var cardName by remember {
        mutableStateOf(creditCardToEdit?.cardName ?: debitCardToEdit?.cardName ?: "")
    }
    var network by remember {
        mutableStateOf(creditCardToEdit?.network ?: debitCardToEdit?.network ?: CardNetwork.VISA)
    }

    // Pure digits stored in state - visual formatting handled by CardNumberVisualTransformation
    var cardNumberDigits by remember {
        val initialDigits = (creditCardToEdit?.cardNumber ?: debitCardToEdit?.cardNumber ?: "").filter { it.isDigit() }
        mutableStateOf(initialDigits)
    }

    var expiryDigits by remember {
        val initialExp = (creditCardToEdit?.expiry ?: debitCardToEdit?.expiry ?: "").filter { it.isDigit() }.take(4)
        mutableStateOf(initialExp)
    }
    var cvv by remember {
        mutableStateOf((creditCardToEdit?.cvv ?: debitCardToEdit?.cvv ?: "").filter { it.isDigit() }.take(4))
    }
    var isCvvVisible by remember { mutableStateOf(false) }

    var cardholderName by remember {
        mutableStateOf(creditCardToEdit?.cardholderName ?: debitCardToEdit?.cardholderName ?: "")
    }
    var linkedEmail by remember {
        mutableStateOf(creditCardToEdit?.linkedEmail ?: debitCardToEdit?.linkedEmail ?: "")
    }
    var linkedPhone by remember {
        mutableStateOf(creditCardToEdit?.linkedPhone ?: debitCardToEdit?.linkedPhone ?: "")
    }
    var issuanceDate by remember {
        mutableStateOf(creditCardToEdit?.issuanceDate ?: debitCardToEdit?.issuanceDate ?: "")
    }
    var ccRewardPointsText by remember {
        mutableStateOf(
            creditCardToEdit?.ccRewardPoints?.toString()
                ?: debitCardToEdit?.rewardPoints?.toString()
                ?: "0"
        )
    }
    var statementDate by remember {
        mutableStateOf(creditCardToEdit?.statementDate ?: "")
    }
    var dueDate by remember {
        mutableStateOf(creditCardToEdit?.dueDate ?: "")
    }
    var remindExpiry by remember {
        mutableStateOf(creditCardToEdit?.remindExpiry ?: debitCardToEdit?.remindExpiry ?: true)
    }
    var remindBillDate by remember {
        mutableStateOf(creditCardToEdit?.remindBillDate ?: true)
    }
    var remindDueDate by remember {
        mutableStateOf(creditCardToEdit?.remindDueDate ?: true)
    }
    var selectedMemberId by remember {
        mutableStateOf(creditCardToEdit?.memberId ?: debitCardToEdit?.memberId ?: "")
    }
    var selectedColor by remember {
        mutableLongStateOf(
            creditCardToEdit?.colorHex ?: debitCardToEdit?.colorHex ?: CardColorBlockOptions.first()
        )
    }

    // Error states
    var bankNameError by remember { mutableStateOf(false) }
    var cardNumberError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val popularBanks = listOf("HDFC Bank", "ICICI Bank", "SBI", "Axis Bank", "Amex", "Kotak", "Citi")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .widthIn(max = 620.dp)
                .imePadding(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Sticky Header
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when {
                                    isEditingCredit -> "Edit Credit Card"
                                    isEditingDebit -> "Edit Debit Card"
                                    isCreditCard -> "Add Credit Card"
                                    else -> "Add Debit Card"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Secure Vault • Encrypted Local Storage",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Segmented Button: Credit Card vs Debit Card
                    if (!isEditing) {
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = isCreditCard,
                                onClick = { isCreditCard = true },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            ) {
                                Text("Credit Card", fontWeight = FontWeight.Bold)
                            }
                            SegmentedButton(
                                selected = !isCreditCard,
                                onClick = { isCreditCard = false },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Payments,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            ) {
                                Text("Debit Card", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Real-time Digital Card Preview (Google Wallet Style)
                    val previewCardNumber = if (cardNumberDigits.isNotEmpty()) cardNumberDigits.chunked(4).joinToString(" ") else "•••• •••• •••• ••••"
                    val previewExpiry = when {
                        expiryDigits.length >= 4 -> "${expiryDigits.take(2)}/${expiryDigits.takeLast(2)}"
                        expiryDigits.length in 1..3 -> "${expiryDigits.take(2)}/••"
                        else -> "MM/YY"
                    }
                    LiveCardPreview(
                        bankName = bankName.ifBlank { "BANK NAME" },
                        cardName = cardName.ifBlank { if (isCreditCard) "Credit Card" else "Debit Card" },
                        network = network,
                        cardNumber = previewCardNumber,
                        cardholderName = cardholderName.ifBlank { "CARDHOLDER NAME" },
                        expiry = previewExpiry,
                        isCreditCard = isCreditCard,
                        colorHex = selectedColor
                    )

                    // SECTION 1: Bank & Card Model
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "BANK & NETWORK DETAILS",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Quick Bank Chips
                            Text(
                                text = "Quick Select Issuer:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(popularBanks) { bank ->
                                    val isSelected = bankName.equals(bank, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            bankName = bank
                                            bankNameError = false
                                            errorMessage = null
                                        },
                                        label = { Text(bank, fontSize = 12.sp) }
                                    )
                                }
                            }

                            // Bank Name TextField
                            OutlinedTextField(
                                value = bankName,
                                onValueChange = {
                                    bankName = it
                                    if (it.isNotBlank()) bankNameError = false
                                },
                                label = { Text("Bank / Issuer Name *") },
                                placeholder = { Text("e.g. HDFC Bank, ICICI, SBI, Axis, Amex") },
                                isError = bankNameError,
                                supportingText = if (bankNameError) {
                                    { Text("Bank name is required", color = MaterialTheme.colorScheme.error) }
                                } else null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Card Name TextField
                            OutlinedTextField(
                                value = cardName,
                                onValueChange = { cardName = it },
                                label = { Text("Card Model / Nickname") },
                                placeholder = { Text("e.g. Tata Neu Infinity, Millennia, Coral, Sapphiro") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Network Selection (RuPay, Visa, Mastercard, Amex)
                            Text(
                                text = "Card Network",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CardNetwork.values().forEach { net ->
                                    val isSelected = network == net
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { network = net }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            CardNetworkBadge(network = net)
                                            Text(
                                                text = net.label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 11.sp
                                                ),
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 2: Card Credentials (Number, Expiry, CVV, Cardholder)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "CARD CREDENTIALS & SECURITY",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Card Number with auto 4-digit formatting via VisualTransformation
                            OutlinedTextField(
                                value = cardNumberDigits,
                                onValueChange = { input ->
                                    val digitsOnly = input.filter { it.isDigit() }.take(16)
                                    cardNumberDigits = digitsOnly
                                    if (digitsOnly.length >= 4) {
                                        cardNumberError = false
                                    }
                                },
                                label = { Text("Card Number (Auto-grouped in 4s) *") },
                                placeholder = { Text("4532 8920 1192 3847") },
                                isError = cardNumberError,
                                supportingText = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        if (cardNumberError) {
                                            Text("Enter at least 4 digits", color = MaterialTheme.colorScheme.error)
                                        } else {
                                            Text("Formatted in 4-digit blocks", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text("${cardNumberDigits.length}/16", fontWeight = FontWeight.Bold)
                                    }
                                },
                                singleLine = true,
                                visualTransformation = CardNumberVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Expiry & CVV Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Expiry with auto "/" via ExpiryDateVisualTransformation
                                OutlinedTextField(
                                    value = expiryDigits,
                                    onValueChange = { input ->
                                        expiryDigits = input.filter { it.isDigit() }.take(4)
                                    },
                                    label = { Text("Expiry (MM/YY) *") },
                                    placeholder = { Text("12/28") },
                                    supportingText = { Text("Enter MMYY (e.g. 1228)", fontSize = 10.sp) },
                                    singleLine = true,
                                    visualTransformation = ExpiryDateVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1.1f)
                                )

                                // CVV with toggle visibility
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = {
                                        cvv = it.filter { char -> char.isDigit() }.take(4)
                                    },
                                    label = { Text("CVV / CVC") },
                                    placeholder = { Text("3 or 4 digits") },
                                    supportingText = { Text("Optional for vault", fontSize = 10.sp) },
                                    singleLine = true,
                                    visualTransformation = if (isCvvVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    trailingIcon = {
                                        IconButton(onClick = { isCvvVisible = !isCvvVisible }) {
                                            Icon(
                                                imageVector = if (isCvvVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle CVV visibility",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Cardholder Name
                            OutlinedTextField(
                                value = cardholderName,
                                onValueChange = { cardholderName = it },
                                label = { Text("Cardholder Name") },
                                placeholder = { Text("As printed on card") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Linked Contact (Email & Phone Number for Bank Alerts/OTP)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = linkedEmail,
                                    onValueChange = { linkedEmail = it },
                                    label = { Text("Linked Email") },
                                    placeholder = { Text("alerts@bank.com") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = linkedPhone,
                                    onValueChange = { linkedPhone = it },
                                    label = { Text("Linked Phone") },
                                    placeholder = { Text("+91 9876543210") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Issuance Date & Reward Points
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = issuanceDate,
                                    onValueChange = { issuanceDate = it },
                                    label = { Text("Issuance Date") },
                                    placeholder = { Text("e.g. 05/23") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = ccRewardPointsText,
                                    onValueChange = {
                                        ccRewardPointsText = it.filter { char -> char.isDigit() }
                                    },
                                    label = { Text("Reward Points") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Linked Contact Details: Email & Phone
                            Text(
                                text = "LINKED CONTACT INFO (EMAIL & PHONE)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = linkedEmail,
                                    onValueChange = { linkedEmail = it },
                                    label = { Text("Linked Email") },
                                    placeholder = { Text("alerts@bank.com") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = linkedPhone,
                                    onValueChange = { linkedPhone = it },
                                    label = { Text("Linked Phone") },
                                    placeholder = { Text("+91 9876543210") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // SECTION 3: Billing Cycle & Alerts (for Credit Cards)
                    if (isCreditCard) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "BILLING CYCLE & PAYMENT DUE DATE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = statementDate,
                                        onValueChange = { statementDate = it },
                                        label = { Text("Bill Statement Date") },
                                        placeholder = { Text("e.g. 12th of month") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = dueDate,
                                        onValueChange = { dueDate = it },
                                        label = { Text("Payment Due Date") },
                                        placeholder = { Text("e.g. 2nd of month") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // SECTION 4: Reminders
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "VAULT REMINDER PREFERENCES",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Remind Expiry Date", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = remindExpiry, onCheckedChange = { remindExpiry = it })
                            }

                            if (isCreditCard) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Remind Bill Generation Date", style = MaterialTheme.typography.bodyMedium)
                                    Switch(checked = remindBillDate, onCheckedChange = { remindBillDate = it })
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Remind Payment Due Date", style = MaterialTheme.typography.bodyMedium)
                                    Switch(checked = remindDueDate, onCheckedChange = { remindDueDate = it })
                                }
                            }
                        }
                    }

                    // SECTION 5: Solid Color Block (Google Wallet Style)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "CARD SOLID COLOR BLOCK (GOOGLE WALLET STYLE)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(CardColorBlockOptions) { colorValue ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(colorValue))
                                            .border(
                                                width = if (selectedColor == colorValue) 3.dp else 1.dp,
                                                color = if (selectedColor == colorValue) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedColor = colorValue },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedColor == colorValue) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 6: Family Member Assignment
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "ASSIGN TO FAMILY MEMBER",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    FilterChip(
                                        selected = selectedMemberId.isBlank(),
                                        onClick = { selectedMemberId = "" },
                                        label = { Text("Vault Unassigned") }
                                    )
                                }
                                items(members) { member ->
                                    FilterChip(
                                        selected = selectedMemberId == member.id,
                                        onClick = {
                                            selectedMemberId = member.id
                                            if (cardholderName.isBlank()) {
                                                cardholderName = member.name
                                            }
                                        },
                                        label = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                if (!member.profilePictureUri.isNullOrBlank()) {
                                                    AsyncImage(
                                                        model = com.example.util.ImageModelResolver.resolve(member.profilePictureUri),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(18.dp)
                                                            .clip(CircleShape)
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Text(member.name)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Prominent In-Line Error Banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = errorMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // 3. Sticky Action Footer
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                // Validation
                                if (bankName.isBlank()) {
                                    bankNameError = true
                                    errorMessage = "Please enter or select a Bank / Issuer Name"
                                    return@Button
                                }

                                val cleanCardNumber = cardNumberDigits.filter { it.isDigit() }
                                if (cleanCardNumber.length < 4) {
                                    cardNumberError = true
                                    errorMessage = "Please enter a valid card number (at least 4 digits)"
                                    return@Button
                                }

                                errorMessage = null
                                val points = ccRewardPointsText.toLongOrNull() ?: 0L

                                // Fallbacks for optional card fields
                                val finalCardName = cardName.trim().ifBlank {
                                    val member = members.find { it.id == selectedMemberId }
                                    if (member != null) {
                                        "${member.name}'s ${network.label} Card"
                                    } else {
                                        "${bankName.trim()} ${if (isCreditCard) "Credit" else "Debit"}"
                                    }
                                }

                                val finalHolderName = cardholderName.trim().ifBlank {
                                    members.find { it.id == selectedMemberId }?.name ?: "VAULT HOLDER"
                                }

                                val finalExpiry = when {
                                    expiryDigits.length >= 4 -> "${expiryDigits.take(2)}/${expiryDigits.takeLast(2)}"
                                    expiryDigits.isNotEmpty() -> expiryDigits
                                    else -> "12/28"
                                }
                                val finalCvv = cvv.trim().ifBlank { "" }

                                if (isCreditCard) {
                                    val card = CreditCard(
                                        id = creditCardToEdit?.id ?: "cc_${UUID.randomUUID().toString().take(8)}",
                                        bankName = bankName.trim(),
                                        cardName = finalCardName,
                                        network = network,
                                        cardNumber = cleanCardNumber,
                                        expiry = finalExpiry,
                                        cvv = finalCvv,
                                        cardholderName = finalHolderName,
                                        issuanceDate = issuanceDate.trim(),
                                        ccRewardPoints = points,
                                        statementDate = statementDate.trim(),
                                        dueDate = dueDate.trim(),
                                        remindExpiry = remindExpiry,
                                        remindBillDate = remindBillDate,
                                        remindDueDate = remindDueDate,
                                        memberId = selectedMemberId,
                                        colorHex = selectedColor,
                                        linkedEmail = linkedEmail.trim(),
                                        linkedPhone = linkedPhone.trim()
                                    )
                                    onSaveCreditCard(card)
                                } else {
                                    val card = DebitCard(
                                        id = debitCardToEdit?.id ?: "dc_${UUID.randomUUID().toString().take(8)}",
                                        bankName = bankName.trim(),
                                        cardName = finalCardName,
                                        network = network,
                                        cardNumber = cleanCardNumber,
                                        expiry = finalExpiry,
                                        cvv = finalCvv,
                                        cardholderName = finalHolderName,
                                        issuanceDate = issuanceDate.trim(),
                                        rewardPoints = points,
                                        remindExpiry = remindExpiry,
                                        memberId = selectedMemberId,
                                        colorHex = selectedColor,
                                        linkedEmail = linkedEmail.trim(),
                                        linkedPhone = linkedPhone.trim()
                                    )
                                    onSaveDebitCard(card)
                                }
                                onDismiss()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("save_card_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEditing) "Save Changes" else "Add to Vault",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Live Digital Card Preview Component (Google Wallet style with 24.dp rounded corners)
 */
@Composable
private fun LiveCardPreview(
    bankName: String,
    cardName: String,
    network: CardNetwork,
    cardNumber: String,
    cardholderName: String,
    expiry: String,
    isCreditCard: Boolean,
    colorHex: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.68f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getCardBackgroundBrush(colorHex))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Bank name, Card model, Network badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bankName.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = cardName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isCreditCard) "CREDIT" else "DEBIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        CardNetworkBadge(network = network)
                    }
                }

                // Middle Row: EMV chip
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmvChipGraphic()
                }

                // Card Number Row (formatted in 4s)
                Text(
                    text = cardNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color.White,
                    maxLines = 1
                )

                // Bottom Row: Cardholder & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARDHOLDER",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        Text(
                            text = cardholderName.uppercase(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            maxLines = 1
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        Text(
                            text = expiry,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
