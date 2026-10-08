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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FamilyMember
import com.example.data.WalletOrGiftCard
import com.example.ui.components.AttachmentViewerSheet
import com.example.ui.components.CustomColorPickerDialog
import com.example.ui.theme.CardColorBlockOptions
import com.example.util.AttachmentFileManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

val giftCardPresets = listOf("Amazon", "Flipkart", "Apple Store", "Zara", "Starbucks", "Google Play", "MakeMyTrip")
val walletPresets = listOf("Paytm", "PhonePe", "Google Pay (GPay)", "Amazon Pay", "CRED", "MobiKwik")
val redemptionModes = listOf("Online / App", "In-Store / POS", "Voucher Code", "Scan QR")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWalletDialog(
    members: List<FamilyMember>,
    itemToEdit: WalletOrGiftCard? = null,
    onDismiss: () -> Unit,
    onSaveItem: (WalletOrGiftCard) -> Unit
) {
    val context = LocalContext.current
    val isEditing = itemToEdit != null
    var isGiftCard by remember { mutableStateOf(itemToEdit?.isGiftCard ?: false) }

    var providerOrName by remember { mutableStateOf(itemToEdit?.providerOrName ?: "") }
    var cardNumberOrUpi by remember { mutableStateOf(itemToEdit?.cardNumberOrUpi ?: "") }
    var giftCardPin by remember { mutableStateOf(itemToEdit?.giftCardPin ?: "") }
    var vendorName by remember { mutableStateOf(itemToEdit?.vendorName ?: "") }
    var remindExpiry by remember { mutableStateOf(itemToEdit?.remindExpiry ?: true) }
    var showExpiryDatePicker by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf(itemToEdit?.let { if (it.amount > 0) it.amount.toInt().toString() else "" } ?: "") }
    var expiryDate by remember { mutableStateOf(itemToEdit?.expiryDate ?: "") }
    var modeOfRedemption by remember { mutableStateOf(itemToEdit?.modeOfRedemption ?: "Online / App") }
    var remarks by remember { mutableStateOf(itemToEdit?.remarks ?: "") }
    var registeredMobile by remember { mutableStateOf(itemToEdit?.registeredMobile ?: "") }
    var kycStatus by remember { mutableStateOf(itemToEdit?.kycStatus ?: "Full KYC Verified") }
    var selectedMemberId by remember { mutableStateOf(itemToEdit?.memberId ?: "") }
    var selectedColor by remember { mutableLongStateOf(itemToEdit?.colorHex ?: CardColorBlockOptions[1]) }
    var showCustomColorPicker by remember { mutableStateOf(false) }
    
    var barcodeOrReceiptImagePath by remember { mutableStateOf(itemToEdit?.barcodeOrReceiptImagePath) }
    var attachmentPaths by remember { mutableStateOf(itemToEdit?.attachmentPaths ?: emptyList()) }
    var activePreviewPath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val barcodePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = AttachmentFileManager.copyUriToInternalStorage(context, uri)
            if (path != null) {
                barcodeOrReceiptImagePath = path
                Toast.makeText(context, "Barcode/Receipt attached", Toast.LENGTH_SHORT).show()
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
                        value = vendorName,
                        onValueChange = { vendorName = it },
                        label = { Text("Purchased From / Vendor") },
                        placeholder = { Text("e.g. Amazon, Gyft, Woohoo, Retail Store") },
                        leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cardNumberOrUpi,
                            onValueChange = { cardNumberOrUpi = it },
                            label = { Text("Voucher Code *") },
                            placeholder = { Text("e.g. AMZN-XXXX-YYYY") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = giftCardPin,
                            onValueChange = { giftCardPin = it },
                            label = { Text("Redeem PIN") },
                            placeholder = { Text("PIN / Code") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            singleLine = true,
                            modifier = Modifier.weight(0.9f)
                        )
                    }

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
                            placeholder = { Text("DD MMM YYYY") },
                            trailingIcon = {
                                IconButton(onClick = { showExpiryDatePicker = true }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date")
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Remind on Expiry Day Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { remindExpiry = !remindExpiry }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Remind on Expiry Day", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text("Receive vault notification on the day of expiry", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = remindExpiry,
                            onCheckedChange = { remindExpiry = it }
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

                // ATTACHMENTS SECTION: Barcode, QR Receipt, Voucher Scans
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
                            text = "VOUCHER & BARCODE ATTACHMENTS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    barcodePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = if (barcodeOrReceiptImagePath != null) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (barcodeOrReceiptImagePath != null) "Barcode ✓" else "Barcode / QR", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    extraAttachmentLauncher.launch(arrayOf("image/*", "application/pdf"))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Voucher PDF", fontSize = 11.sp)
                            }
                        }

                        val allAttached = listOfNotNull(
                            barcodeOrReceiptImagePath?.let { Pair("QR/Code", it) }
                        ) + attachmentPaths.map { Pair("Voucher", it) }

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
                                                    if (path == barcodeOrReceiptImagePath) barcodeOrReceiptImagePath = null
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

                // Solid Color Accent Block
                Text(
                    text = "Select Color Accent",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
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
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCustomSelected) Color(selectedColor) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isCustomSelected) 3.dp else 1.dp,
                                    color = if (isCustomSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp)
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
                        giftCardPin = giftCardPin.trim(),
                        vendorName = vendorName.trim(),
                        remindExpiry = remindExpiry,
                        amount = amt,
                        expiryDate = expiryDate.trim(),
                        modeOfRedemption = modeOfRedemption.trim(),
                        remarks = remarks.trim(),
                        kycStatus = kycStatus.trim(),
                        registeredMobile = registeredMobile.trim(),
                        memberId = selectedMemberId,
                        colorHex = selectedColor,
                        barcodeOrReceiptImagePath = barcodeOrReceiptImagePath,
                        attachmentPaths = attachmentPaths
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

    if (showExpiryDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showExpiryDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        expiryDate = sdf.format(Date(millis))
                    }
                    showExpiryDatePicker = false
                }) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    expiryDate = ""
                    showExpiryDatePicker = false
                }) {
                    Text("Clear")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (activePreviewPath != null) {
        AttachmentViewerSheet(
            attachmentPath = activePreviewPath,
            documentTitle = "Wallet Attachment Viewer",
            onDismiss = { activePreviewPath = null }
        )
    }

    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColorHex = String.format("#%06X", 0xFFFFFF and selectedColor.toInt()),
            title = "Custom Wallet Color Accent",
            onColorSelected = { colorLong, _ ->
                selectedColor = colorLong
            },
            onDismiss = { showCustomColorPicker = false }
        )
    }
}
