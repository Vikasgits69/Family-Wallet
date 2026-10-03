package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FamilyMember
import com.example.data.KycStatus

data class WalletPreset(
    val name: String,
    val upiSuffix: String,
    val defaultLimit: Double = 100000.0
)

val defaultWalletPresets = listOf(
    WalletPreset("Paytm Wallet", "@paytm", 100000.0),
    WalletPreset("PhonePe", "@ybl", 100000.0),
    WalletPreset("Google Pay (Tez)", "@okhdfcbank", 100000.0),
    WalletPreset("Amazon Pay", "@apl", 10000.0),
    WalletPreset("CRED Pay", "@axisbank", 200000.0),
    WalletPreset("MobiKwik", "@ikwik", 50000.0)
)

@Composable
fun AddWalletDialog(
    members: List<FamilyMember>,
    onDismiss: () -> Unit,
    onAddWallet: (
        providerName: String,
        registeredMobile: String,
        registeredEmail: String,
        upiId: String,
        kycStatus: KycStatus,
        walletLimit: Double,
        memberId: String
    ) -> Unit
) {
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }

    var providerName by remember { mutableStateOf("Paytm Wallet") }
    var registeredMobile by remember { mutableStateOf("+91 98765 43210") }
    var registeredEmail by remember { mutableStateOf("vikas.gupta@example.com") }
    var upiId by remember { mutableStateOf("vikasgupta@paytm") }
    var kycStatus by remember { mutableStateOf(KycStatus.FULL_KYC) }
    var walletLimitText by remember { mutableStateOf("100000") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Link Online Wallet to Vault", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text("Store UPI handle, registered contact & KYC status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
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
                    text = "POPULAR WALLETS & UPI PROVIDERS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(defaultWalletPresets) { preset ->
                        FilterChip(
                            selected = (providerName == preset.name),
                            onClick = {
                                providerName = preset.name
                                upiId = "vikasgupta" + preset.upiSuffix
                                walletLimitText = preset.defaultLimit.toLong().toString()
                            },
                            label = { Text(preset.name, style = MaterialTheme.typography.labelSmall) }
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

                // KYC Status Picker
                Text(
                    text = "KYC VERIFICATION STATUS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    KycStatus.values().forEach { status ->
                        FilterChip(
                            selected = kycStatus == status,
                            onClick = { kycStatus = status },
                            label = { Text(status.label.take(12), style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = providerName,
                    onValueChange = { providerName = it },
                    label = { Text("Wallet / Provider Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Primary UPI ID / VPA") },
                    placeholder = { Text("username@okhdfcbank") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = registeredMobile,
                    onValueChange = { registeredMobile = it },
                    label = { Text("Registered Mobile Number") },
                    placeholder = { Text("+91 98765 43210") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = registeredEmail,
                    onValueChange = { registeredEmail = it },
                    label = { Text("Registered Email") },
                    placeholder = { Text("user@example.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = walletLimitText,
                    onValueChange = { walletLimitText = it },
                    label = { Text("Monthly / Balance Limit (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    if (providerName.isBlank()) {
                        errorMessage = "Please enter wallet provider name"
                        return@Button
                    }
                    if (upiId.isBlank()) {
                        errorMessage = "Please enter UPI ID"
                        return@Button
                    }

                    val limit = walletLimitText.toDoubleOrNull() ?: 100000.0
                    onAddWallet(
                        providerName,
                        registeredMobile,
                        registeredEmail,
                        upiId,
                        kycStatus,
                        limit,
                        selectedMemberId
                    )
                }
            ) {
                Text("Link Wallet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
