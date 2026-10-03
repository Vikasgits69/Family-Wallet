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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccountType
import com.example.data.FamilyMember

data class BankPreset(
    val bankName: String,
    val ifscPrefix: String,
    val branch: String,
    val minBalance: Double
)

val defaultBankPresets = listOf(
    BankPreset("HDFC Bank", "HDFC0000240", "Connaught Place, New Delhi", 10000.0),
    BankPreset("State Bank of India", "SBIN0001234", "Sector 18, Noida", 3000.0),
    BankPreset("ICICI Bank", "ICIC0000011", "Cyber City, Gurugram", 10000.0),
    BankPreset("Kotak Mahindra Bank", "KKBK0000182", "Indirapuram, Ghaziabad", 0.0),
    BankPreset("Axis Bank", "UTIB0000054", "Kasturba Gandhi Marg, Delhi", 10000.0),
    BankPreset("Punjab National Bank", "PUNB0024000", "Lajpat Nagar, New Delhi", 1000.0)
)

@Composable
fun AddAccountDialog(
    members: List<FamilyMember>,
    onDismiss: () -> Unit,
    onAddAccount: (
        bankName: String,
        accountType: AccountType,
        accountNumber: String,
        ifscCode: String,
        branchName: String,
        accountHolderName: String,
        customerId: String,
        linkedMobile: String,
        linkedUpi: String,
        minBalance: Double,
        memberId: String
    ) -> Unit
) {
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }

    var bankName by remember { mutableStateOf("HDFC Bank") }
    var accountType by remember { mutableStateOf(AccountType.SALARY) }
    var accountNumber by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("HDFC0000240") }
    var branchName by remember { mutableStateOf("Connaught Place, New Delhi") }
    var accountHolderName by remember { mutableStateOf("VIKAS GUPTA") }
    var customerId by remember { mutableStateOf("84729103") }
    var linkedMobile by remember { mutableStateOf("+91 98765 43210") }
    var linkedUpi by remember { mutableStateOf("vikasgupta@hdfcbank") }
    var minBalanceText by remember { mutableStateOf("0") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Link Bank Account to Vault", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text("Store IFSC, branch, customer ID & linked UPI details", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Presets
                Text(
                    text = "POPULAR INDIAN BANKS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(defaultBankPresets) { preset ->
                        FilterChip(
                            selected = (bankName == preset.bankName),
                            onClick = {
                                bankName = preset.bankName
                                ifscCode = preset.ifscPrefix
                                branchName = preset.branch
                                minBalanceText = preset.minBalance.toLong().toString()
                            },
                            label = { Text(preset.bankName, style = MaterialTheme.typography.labelSmall) }
                        )
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

                // Account Type Selector
                Text(
                    text = "ACCOUNT TYPE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AccountType.values()) { type ->
                        FilterChip(
                            selected = accountType == type,
                            onClick = { accountType = type },
                            label = { Text(type.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Input Fields
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number") },
                    placeholder = { Text("501002345678912") },
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
                        onValueChange = { ifscCode = it.uppercase() },
                        label = { Text("IFSC Code") },
                        placeholder = { Text("HDFC0000240") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = customerId,
                        onValueChange = { customerId = it },
                        label = { Text("Customer ID / CIF") },
                        placeholder = { Text("84729103") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("Branch Location / City") },
                    placeholder = { Text("Connaught Place, New Delhi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = accountHolderName,
                    onValueChange = { accountHolderName = it },
                    label = { Text("Account Holder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = linkedMobile,
                        onValueChange = { linkedMobile = it },
                        label = { Text("Registered Mobile") },
                        placeholder = { Text("+91 98765 43210") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minBalanceText,
                        onValueChange = { minBalanceText = it },
                        label = { Text("Min Balance (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = linkedUpi,
                    onValueChange = { linkedUpi = it },
                    label = { Text("Linked Bank UPI ID") },
                    placeholder = { Text("vikasgupta@hdfcbank") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

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
                    if (bankName.isBlank()) {
                        errorMessage = "Please enter bank name"
                        return@Button
                    }
                    if (accountNumber.isBlank() || accountNumber.length < 6) {
                        errorMessage = "Please enter valid account number"
                        return@Button
                    }

                    val minBal = minBalanceText.toDoubleOrNull() ?: 0.0
                    onAddAccount(
                        bankName,
                        accountType,
                        accountNumber,
                        ifscCode,
                        branchName,
                        accountHolderName,
                        customerId,
                        linkedMobile,
                        linkedUpi,
                        minBal,
                        selectedMemberId
                    )
                }
            ) {
                Text("Link Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
