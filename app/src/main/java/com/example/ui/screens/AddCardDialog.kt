package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.components.CustomColorPickerDialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.data.BankAccount
import com.example.data.CardNetwork
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.FamilyMember
import com.example.ui.components.AttachmentViewerSheet
import com.example.ui.components.CardNetworkBadge
import com.example.ui.components.EmvChipGraphic
import com.example.ui.components.getCardBackgroundBrush
import com.example.ui.theme.CardColorBlockOptions
import com.example.util.AttachmentFileManager
import com.example.util.CardNumberVisualTransformation
import com.example.util.ExpiryDateVisualTransformation
import java.util.UUID

@Composable
fun AddCardDialog(
    members: List<FamilyMember>,
    existingCreditCards: List<CreditCard> = emptyList(),
    existingDebitCards: List<DebitCard> = emptyList(),
    existingBankAccounts: List<BankAccount> = emptyList(),
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

    // Remembered values from previous cards and accounts
    val rememberedEmails = remember(existingCreditCards, existingDebitCards, existingBankAccounts) {
        val list = mutableListOf<String>()
        existingCreditCards.forEach { c -> if (c.linkedEmail.isNotBlank()) list.add(c.linkedEmail) }
        existingDebitCards.forEach { d -> if (d.linkedEmail.isNotBlank()) list.add(d.linkedEmail) }
        existingBankAccounts.forEach { a -> if (a.linkedEmail.isNotBlank()) list.add(a.linkedEmail) }
        list.distinct()
    }

    val rememberedPhones = remember(existingCreditCards, existingDebitCards, existingBankAccounts) {
        val list = mutableListOf<String>()
        existingCreditCards.forEach { c -> if (c.linkedPhone.isNotBlank()) list.add(c.linkedPhone) }
        existingDebitCards.forEach { d -> if (d.linkedPhone.isNotBlank()) list.add(d.linkedPhone) }
        existingBankAccounts.forEach { a -> if (a.linkedPhone.isNotBlank()) list.add(a.linkedPhone) }
        list.distinct()
    }

    val rememberedNames = remember(existingCreditCards, existingDebitCards, existingBankAccounts, members) {
        val list = mutableListOf<String>()
        existingCreditCards.forEach { c -> if (c.cardholderName.isNotBlank()) list.add(c.cardholderName) }
        existingDebitCards.forEach { d -> if (d.cardholderName.isNotBlank()) list.add(d.cardholderName) }
        existingBankAccounts.forEach { a -> if (a.accountHolderName.isNotBlank()) list.add(a.accountHolderName) }
        members.forEach { m -> if (m.name.isNotBlank()) list.add(m.name) }
        list.distinct()
    }

    val rememberedBanks = remember(existingCreditCards, existingDebitCards, existingBankAccounts) {
        val list = mutableListOf("HDFC Bank", "State Bank of India", "ICICI Bank", "Axis Bank", "Kotak Mahindra Bank", "Bank of Baroda", "Punjab National Bank")
        existingCreditCards.forEach { c -> if (c.bankName.isNotBlank()) list.add(c.bankName) }
        existingDebitCards.forEach { d -> if (d.bankName.isNotBlank()) list.add(d.bankName) }
        existingBankAccounts.forEach { a -> if (a.bankName.isNotBlank()) list.add(a.bankName) }
        list.distinct()
    }

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
    var atmPin by remember {
        mutableStateOf(creditCardToEdit?.atmPin ?: debitCardToEdit?.atmPin ?: "")
    }
    var cardPin by remember {
        mutableStateOf(creditCardToEdit?.cardPin ?: debitCardToEdit?.cardPin ?: "")
    }

    var cardholderName by remember {
        mutableStateOf(creditCardToEdit?.cardholderName ?: debitCardToEdit?.cardholderName ?: rememberedNames.firstOrNull() ?: "")
    }
    var linkedEmail by remember {
        mutableStateOf(creditCardToEdit?.linkedEmail ?: debitCardToEdit?.linkedEmail ?: rememberedEmails.firstOrNull() ?: "")
    }
    var linkedPhone by remember {
        mutableStateOf(creditCardToEdit?.linkedPhone ?: debitCardToEdit?.linkedPhone ?: rememberedPhones.firstOrNull() ?: "")
    }
    var customerCareNumber by remember {
        mutableStateOf(creditCardToEdit?.customerCareNumber ?: debitCardToEdit?.customerCareNumber ?: "")
    }
    var supportEmail by remember {
        mutableStateOf(creditCardToEdit?.supportEmail ?: debitCardToEdit?.supportEmail ?: "")
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
    var isBillPaid by remember { mutableStateOf(creditCardToEdit?.isBillPaid ?: false) }
    var domesticPosLimitText by remember {
        mutableStateOf(
            creditCardToEdit?.let { if (it.domesticPosLimit > 0) it.domesticPosLimit.toString() else "" }
                ?: debitCardToEdit?.let { if (it.domesticPosLimit > 0) it.domesticPosLimit.toString() else "" }
                ?: ""
        )
    }
    var dailyAtmLimitText by remember {
        mutableStateOf(
            creditCardToEdit?.let { if (it.atmDailyLimit > 0) it.atmDailyLimit.toString() else "" }
                ?: debitCardToEdit?.let { if (it.atmDailyLimit > 0) it.atmDailyLimit.toString() else "" }
                ?: ""
        )
    }
    var internationalUsage by remember {
        mutableStateOf(
            creditCardToEdit?.internationalEnabled ?: debitCardToEdit?.internationalEnabled ?: false
        )
    }
    var selectedMemberId by remember {
        mutableStateOf(creditCardToEdit?.memberId ?: debitCardToEdit?.memberId ?: "")
    }
    var selectedColor by remember {
        mutableLongStateOf(
            creditCardToEdit?.colorHex ?: debitCardToEdit?.colorHex ?: CardColorBlockOptions.first()
        )
    }
    var showCustomColorPicker by remember { mutableStateOf(false) }

    var frontCardImagePath by remember {
        mutableStateOf(creditCardToEdit?.frontCardImagePath ?: debitCardToEdit?.frontCardImagePath)
    }
    var backCardImagePath by remember {
        mutableStateOf(creditCardToEdit?.backCardImagePath ?: debitCardToEdit?.backCardImagePath)
    }
    var attachmentPaths by remember {
        mutableStateOf(creditCardToEdit?.attachmentPaths ?: debitCardToEdit?.attachmentPaths ?: emptyList())
    }
    var activePreviewPath by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val frontPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = AttachmentFileManager.copyUriToInternalStorage(context, uri)
            if (path != null) {
                frontCardImagePath = path
                Toast.makeText(context, "Front card image attached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val backPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = AttachmentFileManager.copyUriToInternalStorage(context, uri)
            if (path != null) {
                backCardImagePath = path
                Toast.makeText(context, "Back card image attached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val extraAttachmentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val savedPaths = uris.mapNotNull { uri ->
                AttachmentFileManager.copyUriToInternalStorage(context, uri)
            }
            if (savedPaths.isNotEmpty()) {
                attachmentPaths = (attachmentPaths + savedPaths).distinct()
                Toast.makeText(context, "Added ${savedPaths.size} attachment(s)", Toast.LENGTH_SHORT).show()
            }
        }
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

                            // Quick Bank Suggestions & Issuer Input
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (rememberedBanks.isNotEmpty()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(rememberedBanks.take(6)) { bank ->
                                            val isSelected = bankName.equals(bank, ignoreCase = true)
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    bankName = bank
                                                    bankNameError = false
                                                    errorMessage = null
                                                },
                                                label = { Text(bank, fontSize = 11.sp) }
                                            )
                                        }
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
                                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                                    isError = bankNameError,
                                    supportingText = if (bankNameError) {
                                        { Text("Bank name is required", color = MaterialTheme.colorScheme.error) }
                                    } else null,
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Card Name TextField
                            OutlinedTextField(
                                value = cardName,
                                onValueChange = { cardName = it },
                                label = { Text("Card Model / Nickname") },
                                placeholder = { Text("e.g. Tata Neu Infinity, Millennia, Coral, Sapphiro") },
                                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Network Selection (RuPay, Visa, Mastercard, Amex)
                            Text(
                                text = "CARD NETWORK",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
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

                    // SECTION 2: Card Credentials (Number, Expiry, CVV, Cardholder, Linked Contacts)
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
                                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
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
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = CardNumberVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Expiry & CVV Row (equal width, clean alignment, no eye icon)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Expiry with auto "/" via ExpiryDateVisualTransformation
                                OutlinedTextField(
                                    value = expiryDigits,
                                    onValueChange = { input ->
                                        expiryDigits = input.filter { it.isDigit() }.take(4)
                                    },
                                    label = { Text("Expiry (MM/YY) *") },
                                    placeholder = { Text("12/28") },
                                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    visualTransformation = ExpiryDateVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )

                                // CVV without eye icon, clean aligned box
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = {
                                        cvv = it.filter { char -> char.isDigit() }.take(4)
                                    },
                                    label = { Text("CVV / CVC") },
                                    placeholder = { Text("3 or 4 digits") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // ATM PIN & Card PIN Row (Encrypted / Protected)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = atmPin,
                                    onValueChange = { atmPin = it.filter { char -> char.isDigit() }.take(6) },
                                    label = { Text("ATM PIN") },
                                    placeholder = { Text("4 digits") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = cardPin,
                                    onValueChange = { cardPin = it.filter { char -> char.isDigit() }.take(6) },
                                    label = { Text("Card / POS PIN") },
                                    placeholder = { Text("4-6 digits") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Cardholder Name with auto-suggestions
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedTextField(
                                    value = cardholderName,
                                    onValueChange = { cardholderName = it },
                                    label = { Text("Cardholder Name") },
                                    placeholder = { Text("As printed on card") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (rememberedNames.isNotEmpty() && (cardholderName.isBlank() || !rememberedNames.contains(cardholderName))) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(rememberedNames) { name ->
                                            AssistChip(
                                                onClick = { cardholderName = name },
                                                label = { Text(name, fontSize = 11.sp) },
                                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                            )
                                        }
                                    }
                                }
                            }

                            // Linked Contact (Email & Phone Number with suggestions)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = linkedEmail,
                                        onValueChange = { linkedEmail = it },
                                        label = { Text("Linked Email") },
                                        placeholder = { Text("alerts@bank.com") },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                            focusedContainerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = linkedPhone,
                                        onValueChange = { linkedPhone = it },
                                        label = { Text("Linked Phone") },
                                        placeholder = { Text("+91 9876543210") },
                                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                            focusedContainerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (rememberedEmails.isNotEmpty() && linkedEmail.isBlank()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(rememberedEmails) { email ->
                                            AssistChip(
                                                onClick = { linkedEmail = email },
                                                label = { Text(email, fontSize = 11.sp) },
                                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                            )
                                        }
                                    }
                                }

                                if (rememberedPhones.isNotEmpty() && linkedPhone.isBlank()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(rememberedPhones) { phone ->
                                            AssistChip(
                                                onClick = { linkedPhone = phone },
                                                label = { Text(phone, fontSize = 11.sp) },
                                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                            )
                                        }
                                    }
                                }
                                 Spacer(modifier = Modifier.height(4.dp))

                                 Row(
                                     modifier = Modifier.fillMaxWidth(),
                                     horizontalArrangement = Arrangement.spacedBy(10.dp)
                                 ) {
                                     OutlinedTextField(
                                         value = customerCareNumber,
                                         onValueChange = { customerCareNumber = it },
                                         label = { Text("Customer Care No.") },
                                         placeholder = { Text("1800-XXX-XXXX") },
                                         leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                         singleLine = true,
                                         shape = RoundedCornerShape(12.dp),
                                         colors = OutlinedTextFieldDefaults.colors(
                                             unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                             focusedContainerColor = MaterialTheme.colorScheme.surface
                                         ),
                                         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                         modifier = Modifier.weight(1f)
                                     )

                                     OutlinedTextField(
                                         value = supportEmail,
                                         onValueChange = { supportEmail = it },
                                         label = { Text("Support Email") },
                                         placeholder = { Text("support@bank.com") },
                                         leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                         singleLine = true,
                                         shape = RoundedCornerShape(12.dp),
                                         colors = OutlinedTextFieldDefaults.colors(
                                             unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                             focusedContainerColor = MaterialTheme.colorScheme.surface
                                         ),
                                         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                         modifier = Modifier.weight(1f)
                                     )
                                 }
                            }

                            // Issuance Date & Reward Points
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = issuanceDate,
                                    onValueChange = { issuanceDate = it },
                                    label = { Text("Issuance Date") },
                                    placeholder = { Text("e.g. 05/23") },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = ccRewardPointsText,
                                    onValueChange = {
                                        ccRewardPointsText = it.filter { char -> char.isDigit() }
                                    },
                                    label = { Text("Reward Points") },
                                    placeholder = { Text("0") },
                                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                    // SECTION: Card Limits & Controls
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
                                text = "CARD LIMITS & SPEND CONTROLS",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = domesticPosLimitText,
                                    onValueChange = { domesticPosLimitText = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Daily POS Limit (₹)") },
                                    placeholder = { Text("e.g. 50000") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = dailyAtmLimitText,
                                    onValueChange = { dailyAtmLimitText = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Daily ATM Limit (₹)") },
                                    placeholder = { Text("e.g. 25000") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("International Usage", style = MaterialTheme.typography.bodyMedium)
                                    Text("Set on if enabled for global purchases", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(checked = internationalUsage, onCheckedChange = { internationalUsage = it })
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
                                text = "Select Color Accent",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    val isCustomSelected = !CardColorBlockOptions.contains(selectedColor)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isCustomSelected) Color(selectedColor) else MaterialTheme.colorScheme.surfaceVariant)
                                            .border(
                                                width = if (isCustomSelected) 3.dp else 1.dp,
                                                color = if (isCustomSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { showCustomColorPicker = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ColorLens,
                                            contentDescription = "Custom Accent Color",
                                            tint = if (isCustomSelected) Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

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

                    // SECTION 7: Multi-Attachments & Physical Card Scans
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ATTACHMENTS & CARD SCANS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Attach Front/Back photos & PDF statements",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Quick Action Buttons: Front Image, Back Image, Extra Files/PDF
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        frontPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = if (frontCardImagePath != null) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (frontCardImagePath != null) "Front ✓" else "Front", fontSize = 11.sp, maxLines = 1)
                                }

                                OutlinedButton(
                                    onClick = {
                                        backPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = if (backCardImagePath != null) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (backCardImagePath != null) "Back ✓" else "Back", fontSize = 11.sp, maxLines = 1)
                                }

                                OutlinedButton(
                                    onClick = {
                                        extraAttachmentLauncher.launch(arrayOf("image/*", "application/pdf"))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Files", fontSize = 11.sp, maxLines = 1)
                                }
                            }

                            // Thumbnails Preview Row
                            val allAttached = listOfNotNull(
                                frontCardImagePath?.let { Pair("Front", it) },
                                backCardImagePath?.let { Pair("Back", it) }
                            ) + attachmentPaths.map { Pair("Doc", it) }

                            if (allAttached.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(allAttached) { (tag, path) ->
                                        val file = AttachmentFileManager.getFile(context, path)
                                        val isPdf = AttachmentFileManager.isPdf(file)

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { activePreviewPath = path }
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                if (isPdf) {
                                                    Column(
                                                        modifier = Modifier.fillMaxSize(),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(24.dp))
                                                        Text("PDF", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                } else {
                                                    AsyncImage(
                                                        model = file,
                                                        contentDescription = tag,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                }

                                                // Tag badge
                                                Surface(
                                                    color = Color.Black.copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                                                    modifier = Modifier.align(Alignment.TopStart)
                                                ) {
                                                    Text(tag, color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                                }

                                                // Remove Button
                                                IconButton(
                                                    onClick = {
                                                        if (path == frontCardImagePath) frontCardImagePath = null
                                                        else if (path == backCardImagePath) backCardImagePath = null
                                                        else attachmentPaths = attachmentPaths.filter { it != path }
                                                    },
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
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
                                        isBillPaid = isBillPaid,
                                        domesticPosLimit = domesticPosLimitText.toLongOrNull() ?: 0L,
                                        atmDailyLimit = dailyAtmLimitText.toLongOrNull() ?: 0L,
                                        internationalEnabled = internationalUsage,
                                        atmPin = atmPin.trim(),
                                        cardPin = cardPin.trim(),
                                        remindExpiry = remindExpiry,
                                        remindBillDate = remindBillDate,
                                        remindDueDate = remindDueDate,
                                        memberId = selectedMemberId,
                                        colorHex = selectedColor,
                                        linkedEmail = linkedEmail.trim(),
                                        linkedPhone = linkedPhone.trim(),
                                        frontCardImagePath = frontCardImagePath,
                                        backCardImagePath = backCardImagePath,
                                        attachmentPaths = attachmentPaths
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
                                        domesticPosLimit = domesticPosLimitText.toLongOrNull() ?: 0L,
                                        atmDailyLimit = dailyAtmLimitText.toLongOrNull() ?: 0L,
                                        internationalEnabled = internationalUsage,
                                        atmPin = atmPin.trim(),
                                        cardPin = cardPin.trim(),
                                        remindExpiry = remindExpiry,
                                        memberId = selectedMemberId,
                                        colorHex = selectedColor,
                                        linkedEmail = linkedEmail.trim(),
                                        linkedPhone = linkedPhone.trim(),
                                        frontCardImagePath = frontCardImagePath,
                                        backCardImagePath = backCardImagePath,
                                        attachmentPaths = attachmentPaths
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

    if (activePreviewPath != null) {
        AttachmentViewerSheet(
            attachmentPath = activePreviewPath,
            documentTitle = "Card Attachment Viewer",
            onDismiss = { activePreviewPath = null }
        )
    }

    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColorHex = String.format("#%06X", 0xFFFFFF and selectedColor.toInt()),
            title = "Custom Card Color Accent",
            onColorSelected = { colorLong, _ ->
                selectedColor = colorLong
            },
            onDismiss = { showCustomColorPicker = false }
        )
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
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = bankName.ifBlank { "BANK NAME" }.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                        if (cardName.isNotBlank()) {
                            Text(
                                text = cardName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                modifier = Modifier.basicMarquee()
                            )
                        }
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
                    Column(modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)) {
                        Text(
                            text = "CARDHOLDER",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                            color = Color.White.copy(alpha = 0.65f)
                        )
                        Text(
                            text = cardholderName.ifBlank { "YOUR NAME" }.uppercase(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
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
