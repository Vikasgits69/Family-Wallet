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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BankAccount
import com.example.data.FamilyMember
import com.example.ui.theme.BankColorOptions
import java.util.UUID

val bankAccountTypeOptions = listOf("Savings", "Current", "Overdraft", "Loan Account")

@Composable
fun AddAccountDialog(
    members: List<FamilyMember>,
    accountToEdit: BankAccount? = null,
    onDismiss: () -> Unit,
    onSaveAccount: (BankAccount) -> Unit
) {
    val isEditing = accountToEdit != null

    var bankName by remember { mutableStateOf(accountToEdit?.bankName ?: "") }
    var accountType by remember { mutableStateOf(accountToEdit?.accountType ?: "Savings") }
    var accountNumber by remember { mutableStateOf(accountToEdit?.accountNumber ?: "") }
    var ifscCode by remember { mutableStateOf(accountToEdit?.ifscCode ?: "") }
    var micrCode by remember { mutableStateOf(accountToEdit?.micrCode ?: "") }
    var accountHolderName by remember { mutableStateOf(accountToEdit?.accountHolderName ?: "") }
    var branchName by remember { mutableStateOf(accountToEdit?.branchName ?: "") }
    var linkedEmail by remember { mutableStateOf(accountToEdit?.linkedEmail ?: "") }
    var linkedPhone by remember { mutableStateOf(accountToEdit?.linkedPhone ?: "") }
    var selectedMemberId by remember { mutableStateOf(accountToEdit?.memberId ?: "") }
    var selectedColor by remember { mutableLongStateOf(accountToEdit?.colorHex ?: BankColorOptions.first()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isEditing) "Edit Bank Account" else "Link Bank Account",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Vault account details, IFSC, and MICR code securely",
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
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name *") },
                    placeholder = { Text("e.g. HDFC Bank, State Bank of India, ICICI") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

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
                    singleLine = true,
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
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = micrCode,
                        onValueChange = { micrCode = it.filter { c -> c.isDigit() }.take(9) },
                        label = { Text("MICR Code") },
                        placeholder = { Text("9 Digits") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = accountHolderName,
                    onValueChange = { accountHolderName = it },
                    label = { Text("Account Holder Name *") },
                    placeholder = { Text("As per bank passbook") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("Branch Name / City") },
                    placeholder = { Text("e.g. Connaught Place, New Delhi") },
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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = linkedPhone,
                        onValueChange = { linkedPhone = it },
                        label = { Text("Linked Phone") },
                        placeholder = { Text("+91 9876543210") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Color Picker / Accent Selection Specifically for Bank Accounts (Google Wallet style)
                Text(
                    text = "BANK ACCOUNT ACCENT COLOR",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                        accountHolderName = accountHolderName.trim(),
                        branchName = branchName.trim(),
                        memberId = selectedMemberId,
                        colorHex = selectedColor,
                        linkedEmail = linkedEmail.trim(),
                        linkedPhone = linkedPhone.trim()
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
}
