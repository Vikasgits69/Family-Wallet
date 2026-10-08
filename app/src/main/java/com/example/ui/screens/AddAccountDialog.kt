package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.CustomColorPickerDialog
import com.example.data.BankAccount
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.FamilyMember
import com.example.ui.components.AttachmentViewerSheet
import com.example.ui.theme.BankColorOptions
import com.example.util.AttachmentFileManager
import java.util.UUID

val bankAccountTypeOptions = listOf("Savings", "Current", "Overdraft", "Loan Account")

@Composable
fun AddAccountDialog(
    members: List<FamilyMember>,
    existingCreditCards: List<CreditCard> = emptyList(),
    existingDebitCards: List<DebitCard> = emptyList(),
    existingBankAccounts: List<BankAccount> = emptyList(),
    accountToEdit: BankAccount? = null,
    onDismiss: () -> Unit,
    onSaveAccount: (BankAccount) -> Unit
) {
    val context = LocalContext.current
    val isEditing = accountToEdit != null

    // Remember previous data from cards & accounts
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

    var bankName by remember { mutableStateOf(accountToEdit?.bankName ?: "") }
    var accountType by remember { mutableStateOf(accountToEdit?.accountType ?: "Savings") }
    var accountNumber by remember { mutableStateOf(accountToEdit?.accountNumber ?: "") }
    var ifscCode by remember { mutableStateOf(accountToEdit?.ifscCode ?: "") }
    var micrCode by remember { mutableStateOf(accountToEdit?.micrCode ?: "") }
    var cifOrClientCode by remember { mutableStateOf(accountToEdit?.cifOrClientCode ?: "") }
    var accountHolderName by remember { mutableStateOf(accountToEdit?.accountHolderName ?: rememberedNames.firstOrNull() ?: "") }
    var branchName by remember { mutableStateOf(accountToEdit?.branchName ?: "") }
    var linkedEmail by remember { mutableStateOf(accountToEdit?.linkedEmail ?: rememberedEmails.firstOrNull() ?: "") }
    var linkedPhone by remember { mutableStateOf(accountToEdit?.linkedPhone ?: rememberedPhones.firstOrNull() ?: "") }
    var customerCareNumber by remember { mutableStateOf(accountToEdit?.customerCareNumber ?: "") }
    var supportEmail by remember { mutableStateOf(accountToEdit?.supportEmail ?: "") }
    var netBankingUserId by remember { mutableStateOf(accountToEdit?.netBankingUserId ?: "") }
    var netBankingPassword by remember { mutableStateOf(accountToEdit?.netBankingPassword ?: "") }
    var mobileBankingUserId by remember { mutableStateOf(accountToEdit?.mobileBankingUserId ?: "") }
    var mobileBankingPassword by remember { mutableStateOf(accountToEdit?.mobileBankingPassword ?: "") }
    var selectedMemberId by remember { mutableStateOf(accountToEdit?.memberId ?: "") }
    var selectedColor by remember { mutableLongStateOf(accountToEdit?.colorHex ?: BankColorOptions.first()) }
    var showCustomColorPicker by remember { mutableStateOf(false) }
    
    var chequeBookImagePath by remember { mutableStateOf(accountToEdit?.chequeBookImagePath) }
    var passbookImagePath by remember { mutableStateOf(accountToEdit?.passbookImagePath) }
    var attachmentPaths by remember { mutableStateOf(accountToEdit?.attachmentPaths ?: emptyList()) }
    var activePreviewPath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val chequePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = AttachmentFileManager.copyUriToInternalStorage(context, uri)
            if (path != null) {
                chequeBookImagePath = path
                Toast.makeText(context, "Cheque book photo attached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val passbookPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = AttachmentFileManager.copyUriToInternalStorage(context, uri)
            if (path != null) {
                passbookImagePath = path
                Toast.makeText(context, "Passbook photo attached", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Added ${savedPaths.size} document(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isEditing) "Edit Bank Account" else "Link Bank Account",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Vault account details, IFSC, and attachments securely",
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
                // Bank Name with auto-suggestions
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name *") },
                        placeholder = { Text("e.g. HDFC Bank, State Bank of India") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (rememberedBanks.isNotEmpty() && bankName.isBlank()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(rememberedBanks.take(5)) { b ->
                                AssistChip(
                                    onClick = { bankName = b },
                                    label = { Text(b, fontSize = 11.sp) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                )
                            }
                        }
                    }
                }

                // Account Type Selector: Savings, Current, Overdraft, Loan Account
                Text(
                    text = "ACCOUNT TYPE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(bankAccountTypeOptions) { type ->
                        FilterChip(
                            selected = accountType == type,
                            onClick = { accountType = type },
                            label = { Text(type) }
                        )
                    }
                }

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it.filter { c -> c.isDigit() } },
                    label = { Text("Account Number *") },
                    placeholder = { Text("•••• •••• ••••") },
                    leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ifscCode,
                        onValueChange = { ifscCode = it.uppercase().take(11) },
                        label = { Text("IFSC Code *") },
                        placeholder = { Text("HDFC0000123") },
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = micrCode,
                        onValueChange = { micrCode = it.filter { c -> c.isDigit() }.take(9) },
                        label = { Text("MICR Code") },
                        placeholder = { Text("9 Digits") },
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

                OutlinedTextField(
                    value = cifOrClientCode,
                    onValueChange = { cifOrClientCode = it.trim() },
                    label = { Text("CIF Number / Client Code") },
                    placeholder = { Text("e.g. 123456789 or CIF98765") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Account Holder Name with suggestions
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = accountHolderName,
                        onValueChange = { accountHolderName = it },
                        label = { Text("Account Holder Name *") },
                        placeholder = { Text("As per bank passbook") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (rememberedNames.isNotEmpty() && (accountHolderName.isBlank() || !rememberedNames.contains(accountHolderName))) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(rememberedNames) { name ->
                                AssistChip(
                                    onClick = { accountHolderName = name },
                                    label = { Text(name, fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("Branch Name / City") },
                    placeholder = { Text("e.g. Connaught Place, New Delhi") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Linked Contact (Email & Phone Number for Bank Alerts/OTP)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

                // DIGITAL & ONLINE BANKING CREDENTIALS (SECURE VAULT)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "DIGITAL & ONLINE BANKING (PROTECTED)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "Internet Banking Credentials",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = netBankingUserId,
                                onValueChange = { netBankingUserId = it },
                                label = { Text("Net Banking User ID") },
                                placeholder = { Text("User ID / Cust ID") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = netBankingPassword,
                                onValueChange = { netBankingPassword = it },
                                label = { Text("Net Banking Password") },
                                placeholder = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        Text(
                            text = "Mobile Banking App Credentials",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = mobileBankingUserId,
                                onValueChange = { mobileBankingUserId = it },
                                label = { Text("Mobile Banking ID") },
                                placeholder = { Text("Mobile / User ID") },
                                leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = mobileBankingPassword,
                                onValueChange = { mobileBankingPassword = it },
                                label = { Text("MPIN / Password") },
                                placeholder = { Text("MPIN / PIN") },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ATTACHMENTS SECTION: Passbook, Cheque Book, Account Statements
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "BANK ATTACHMENTS & PASSBOOK SCANS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    chequePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = if (chequeBookImagePath != null) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (chequeBookImagePath != null) "Cheque ✓" else "Cheque", fontSize = 10.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    passbookPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = if (passbookImagePath != null) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (passbookImagePath != null) "Passbook ✓" else "Passbook", fontSize = 10.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    extraAttachmentLauncher.launch(arrayOf("image/*", "application/pdf"))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("+ Files", fontSize = 10.sp, maxLines = 1)
                            }
                        }

                        val allAttached = listOfNotNull(
                            chequeBookImagePath?.let { Pair("Cheque", it) },
                            passbookImagePath?.let { Pair("Passbook", it) }
                        ) + attachmentPaths.map { Pair("Doc", it) }

                        if (allAttached.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(allAttached) { (tag, path) ->
                                    val file = AttachmentFileManager.getFile(context, path)
                                    val isPdf = AttachmentFileManager.isPdf(file)

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { activePreviewPath = path }
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            if (isPdf) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(20.dp))
                                                    Text("PDF", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                AsyncImage(
                                                    model = file,
                                                    contentDescription = tag,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Surface(
                                                color = Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(bottomEnd = 4.dp),
                                                modifier = Modifier.align(Alignment.TopStart)
                                            ) {
                                                Text(tag, color = Color.White, fontSize = 7.sp, modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp), fontWeight = FontWeight.Bold)
                                            }

                                            IconButton(
                                                onClick = {
                                                    if (path == chequeBookImagePath) chequeBookImagePath = null
                                                    else if (path == passbookImagePath) passbookImagePath = null
                                                    else attachmentPaths = attachmentPaths.filter { it != path }
                                                },
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .align(Alignment.TopEnd)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Color Picker / Accent Selection Specifically for Bank Accounts (Google Wallet style)
                Text(
                    text = "Select Bank Color Accent",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isCustomSelected = !BankColorOptions.contains(selectedColor)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isCustomSelected) Color(selectedColor) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isCustomSelected) 3.dp else 1.dp,
                                    color = if (isCustomSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .clickable { showCustomColorPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = "Custom Accent Color",
                                tint = if (isCustomSelected) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    items(BankColorOptions) { colorVal ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (selectedColor == colorVal) 3.dp else 1.dp,
                                    color = if (selectedColor == colorVal) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorVal },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorVal) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Assigned Family Member Picker
                Text(
                    text = "ASSIGN TO FAMILY MEMBER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
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
                            label = { Text("Unassigned") }
                        )
                    }
                    items(members) { member ->
                        FilterChip(
                            selected = selectedMemberId == member.id,
                            onClick = { selectedMemberId = member.id },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (!member.profilePictureUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = com.example.util.ImageModelResolver.resolve(member.profilePictureUri),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp).clip(CircleShape)
                                        )
                                    } else {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Text(member.name)
                                }
                            }
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bankName.isBlank()) {
                        errorMessage = "Please enter Bank Name"
                        return@Button
                    }
                    if (accountNumber.length < 4) {
                        errorMessage = "Please enter valid Account Number"
                        return@Button
                    }
                    if (ifscCode.isBlank()) {
                        errorMessage = "Please enter IFSC Code"
                        return@Button
                    }
                    if (accountHolderName.isBlank()) {
                        errorMessage = "Please enter Account Holder Name"
                        return@Button
                    }

                    val account = BankAccount(
                        id = accountToEdit?.id ?: "bank_${UUID.randomUUID().toString().take(8)}",
                        bankName = bankName.trim(),
                        accountType = accountType,
                        accountNumber = accountNumber.trim(),
                        ifscCode = ifscCode.trim(),
                        micrCode = micrCode.trim(),
                        cifOrClientCode = cifOrClientCode.trim().ifBlank { null },
                        accountHolderName = accountHolderName.trim(),
                        branchName = branchName.trim(),
                        memberId = selectedMemberId,
                        colorHex = selectedColor,
                        linkedEmail = linkedEmail.trim(),
                        linkedPhone = linkedPhone.trim(),
                        customerCareNumber = customerCareNumber.trim(),
                        supportEmail = supportEmail.trim(),
                        netBankingUserId = netBankingUserId.trim(),
                        netBankingPassword = netBankingPassword.trim(),
                        mobileBankingUserId = mobileBankingUserId.trim(),
                        mobileBankingPassword = mobileBankingPassword.trim(),
                        chequeBookImagePath = chequeBookImagePath,
                        passbookImagePath = passbookImagePath,
                        attachmentPaths = attachmentPaths
                    )
                    onSaveAccount(account)
                    onDismiss()
                }
            ) {
                Text(if (isEditing) "Save Changes" else "Add Bank Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (activePreviewPath != null) {
        AttachmentViewerSheet(
            attachmentPath = activePreviewPath,
            documentTitle = "Bank Document Viewer",
            onDismiss = { activePreviewPath = null }
        )
    }

    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColorHex = String.format("#%06X", 0xFFFFFF and selectedColor.toInt()),
            title = "Custom Bank Color Accent",
            onColorSelected = { colorLong, _ ->
                selectedColor = colorLong
            },
            onDismiss = { showCustomColorPicker = false }
        )
    }
}
