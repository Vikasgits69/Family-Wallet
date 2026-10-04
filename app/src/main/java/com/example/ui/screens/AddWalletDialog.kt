package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.example.data.FamilyMember
import com.example.data.WalletOrGiftCard
import com.example.ui.theme.CardColorBlockOptions
import java.util.UUID

val giftCardPresets = listOf("Amazon", "Flipkart", "Apple Store", "Zara", "Starbucks", "Google Play", "MakeMyTrip")
val walletPresets = listOf("Paytm", "PhonePe", "Google Pay (GPay)", "Amazon Pay", "CRED", "MobiKwik")
val redemptionModes = listOf("Online / App", "In-Store / POS", "Voucher Code", "Scan QR")

@Composable
fun AddWalletDialog(
    members: List<FamilyMember>,
    itemToEdit: WalletOrGiftCard? = null,
    onDismiss: () -> Unit,
    onSaveItem: (WalletOrGiftCard) -> Unit
) {
    val isEditing = itemToEdit != null
    var isGiftCard by remember { mutableStateOf(itemToEdit?.isGiftCard ?: false) }

    var providerOrName by remember { mutableStateOf(itemToEdit?.providerOrName ?: "") }
    var cardNumberOrUpi by remember { mutableStateOf(itemToEdit?.cardNumberOrUpi ?: "") }
    var amountText by remember { mutableStateOf(itemToEdit?.let { if (it.amount > 0) it.amount.toInt().toString() else "" } ?: "") }
    var expiryDate by remember { mutableStateOf(itemToEdit?.expiryDate ?: "") }
    var modeOfRedemption by remember { mutableStateOf(itemToEdit?.modeOfRedemption ?: "Online / App") }
    var remarks by remember { mutableStateOf(itemToEdit?.remarks ?: "") }
    var registeredMobile by remember { mutableStateOf(itemToEdit?.registeredMobile ?: "") }
    var kycStatus by remember { mutableStateOf(itemToEdit?.kycStatus ?: "Full KYC Verified") }
    var selectedMemberId by remember { mutableStateOf(itemToEdit?.memberId ?: "") }
    var selectedColor by remember { mutableLongStateOf(itemToEdit?.colorHex ?: CardColorBlockOptions[1]) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = when {
                        isEditing && isGiftCard -> "Edit Gift Card"
                        isEditing && !isGiftCard -> "Edit Online Wallet"
                        isGiftCard -> "Add Gift Card"
                        else -> "Add Online Wallet"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Vault vouchers, wallets, and redemption details",
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
                // Switch between Online Wallet and Gift Card
                if (!isEditing) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = !isGiftCard,
                            onClick = { isGiftCard = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Online Wallet", fontWeight = FontWeight.Bold)
                        }
                        SegmentedButton(
                            selected = isGiftCard,
                            onClick = { isGiftCard = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("Gift Card", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (isGiftCard) {
                    // Quick Gift Card Brand Chips
                    Text(
                        text = "POPULAR GIFT CARD BRANDS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(giftCardPresets) { brand ->
                            FilterChip(
                                selected = providerOrName == brand,
                                onClick = { providerOrName = brand },
                                label = { Text(brand, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = providerOrName,
                        onValueChange = { providerOrName = it },
                        label = { Text("Brand / Store Name *") },
                        placeholder = { Text("e.g. Amazon, Apple, Starbucks") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cardNumberOrUpi,
                        onValueChange = { cardNumberOrUpi = it },
                        label = { Text("Card Number / Voucher Code / PIN *") },
                        placeholder = { Text("e.g. AMZN-XXXX-YYYY or 16-digit code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                            label = { Text("Amount (₹) *") },
                            placeholder = { Text("5000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            label = { Text("Expiry Date") },
                            placeholder = { Text("12/28 or DD/MM/YY") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Mode of Redemption
                    Text(
                        text = "MODE OF REDEMPTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(redemptionModes) { mode ->
                            FilterChip(
                                selected = modeOfRedemption == mode,
                                onClick = { modeOfRedemption = mode },
                                label = { Text(mode, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks / Conditions") },
                        placeholder = { Text("e.g. Apply at checkout; Gifted by sibling") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Online Wallet section
                    Text(
                        text = "POPULAR WALLET PROVIDERS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(walletPresets) { prov ->
                            FilterChip(
                                selected = providerOrName == prov,
                                onClick = { providerOrName = prov },
                                label = { Text(prov, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = providerOrName,
                        onValueChange = { providerOrName = it },
                        label = { Text("Wallet Provider *") },
                        placeholder = { Text("e.g. Paytm, PhonePe, Google Pay") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cardNumberOrUpi,
                        onValueChange = { cardNumberOrUpi = it },
                        label = { Text("UPI ID / VPA *") },
                        placeholder = { Text("e.g. username@okhdfcbank or 9876543210@paytm") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = registeredMobile,
                        onValueChange = { registeredMobile = it.filter { c -> c.isDigit() } },
                        label = { Text("Linked Mobile Number") },
                        placeholder = { Text("10 digits") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("KYC / Notes") },
                        placeholder = { Text("Full KYC Verified") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Solid Color Accent Block
                Text(
                    text = "SOLID COLOR BLOCK",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CardColorBlockOptions) { colorValue ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(colorValue))
                                .border(
                                    width = if (selectedColor == colorValue) 3.dp else 1.dp,
                                    color = if (selectedColor == colorValue) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedColor = colorValue },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorValue) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Assigned Family Member
                Text(
                    text = "ASSIGN TO FAMILY MEMBER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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
                    if (providerOrName.isBlank()) {
                        errorMessage = if (isGiftCard) "Please enter Brand Name" else "Please enter Wallet Provider"
                        return@Button
                    }
                    if (cardNumberOrUpi.isBlank()) {
                        errorMessage = if (isGiftCard) "Please enter Card Number / Code" else "Please enter UPI ID / Phone"
                        return@Button
                    }

                    val amt = amountText.toDoubleOrNull() ?: 0.0

                    val item = WalletOrGiftCard(
                        id = itemToEdit?.id ?: "wgc_${UUID.randomUUID().toString().take(8)}",
                        isGiftCard = isGiftCard,
                        providerOrName = providerOrName.trim(),
                        cardNumberOrUpi = cardNumberOrUpi.trim(),
                        amount = amt,
                        expiryDate = expiryDate.trim(),
                        modeOfRedemption = modeOfRedemption.trim(),
                        remarks = remarks.trim(),
                        kycStatus = kycStatus.trim(),
                        registeredMobile = registeredMobile.trim(),
                        memberId = selectedMemberId,
                        colorHex = selectedColor
                    )
                    onSaveItem(item)
                }
            ) {
                Text(if (isEditing) "Save Changes" else "Add to Vault")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
