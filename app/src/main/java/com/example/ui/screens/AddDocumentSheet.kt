package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DocType
import com.example.data.Document
import com.example.data.FamilyMember
import com.example.ui.components.AttachmentViewerSheet
import com.example.ui.components.CustomColorPickerDialog
import com.example.ui.theme.BankColorOptions
import com.example.util.AttachmentFileManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentSheet(
    members: List<FamilyMember>,
    initialMemberId: String? = null,
    documentToEdit: Document? = null,
    onDismiss: () -> Unit,
    onSaveDocument: (Document) -> Unit
) {
    val context = LocalContext.current
    val isEditing = documentToEdit != null

    var title by remember { mutableStateOf(documentToEdit?.title ?: "") }
    var selectedDocType by remember { mutableStateOf(documentToEdit?.docType ?: DocType.AADHAAR) }
    var customDocTypeName by remember { mutableStateOf(documentToEdit?.customDocTypeName ?: "") }
    var docNumber by remember { mutableStateOf(documentToEdit?.docNumber ?: "") }
    var selectedMemberId by remember { mutableStateOf(documentToEdit?.memberId ?: initialMemberId ?: "") }
    var issuanceDate by remember { mutableStateOf(documentToEdit?.issuanceDate) }
    var expiryDate by remember { mutableStateOf(documentToEdit?.expiryDate) }
    var notes by remember { mutableStateOf(documentToEdit?.notes ?: "") }
    var selectedColor by remember { mutableLongStateOf(documentToEdit?.colorHex ?: selectedDocType.defaultColorHex) }
    var showCustomColorPicker by remember { mutableStateOf(false) }
    var attachmentPaths by remember { mutableStateOf(documentToEdit?.attachmentPaths ?: emptyList()) }

    var activePreviewPath by remember { mutableStateOf<String?>(null) }
    var showIssueDatePicker by remember { mutableStateOf(false) }
    var showExpiryDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val savedPaths = uris.mapNotNull { uri ->
                AttachmentFileManager.copyUriToInternalStorage(context, uri)
            }
            if (savedPaths.isNotEmpty()) {
                attachmentPaths = (attachmentPaths + savedPaths).distinct()
                Toast.makeText(context, "Added ${savedPaths.size} photo attachment(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Document/PDF Picker Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val savedPaths = uris.mapNotNull { uri ->
                AttachmentFileManager.copyUriToInternalStorage(context, uri)
            }
            if (savedPaths.isNotEmpty()) {
                attachmentPaths = (attachmentPaths + savedPaths).distinct()
                Toast.makeText(context, "Added ${savedPaths.size} document file(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .widthIn(max = 640.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .testTag("add_document_sheet"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp,
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
                            text = if (isEditing) "Edit Vault Document" else "Add New Document",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Identity, Vehicle RC, Insurance & Property Deeds",
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

            // Scrollable Form Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Document Category / Type Chips
                Text(
                    text = "DOCUMENT CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(DocType.values()) { dtype ->
                        FilterChip(
                            selected = selectedDocType == dtype,
                            onClick = {
                                selectedDocType = dtype
                                selectedColor = dtype.defaultColorHex
                                if (title.isBlank() || DocType.values().any { it.title == title }) {
                                    title = dtype.title
                                }
                            },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(dtype.iconEmoji)
                                    Text(dtype.title)
                                }
                            }
                        )
                    }
                }

                // Custom type name input if OTHER
                if (selectedDocType == DocType.OTHER) {
                    OutlinedTextField(
                        value = customDocTypeName,
                        onValueChange = { customDocTypeName = it },
                        label = { Text("Custom Document Type Name *") },
                        placeholder = { Text("e.g. Birth Certificate, Municipal Tax") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 2. Document Title & Number
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Document Title *") },
                    placeholder = { Text(selectedDocType.title) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it.uppercase() },
                    label = { Text("Document / Identification Number *") },
                    placeholder = { Text(selectedDocType.placeholder) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Family Member Linking
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
                            label = { Text("Shared / None") }
                        )
                    }
                    items(members) { member ->
                        FilterChip(
                            selected = selectedMemberId == member.id,
                            onClick = { selectedMemberId = member.id },
                            label = { Text("${member.name} (${member.relationship})") }
                        )
                    }
                }

                // 4. Dates: Issuance & Expiry
                Text(
                    text = "VALIDITY & EXPIRY TIMELINES",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Issue Date Picker Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showIssueDatePicker = true }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Issue Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = issuanceDate?.let { dateFormat.format(Date(it)) } ?: "Not set (Tap)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    // Expiry Date Picker Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showExpiryDatePicker = true }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Expiry Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = expiryDate?.let { dateFormat.format(Date(it)) } ?: "No Expiry (Tap)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }

                // 5. Multi-Attachment Section (Images & PDFs)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ATTACHED SCANS & FILES (${attachmentPaths.size})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Photos, camera scans or multi-page PDFs",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Action Buttons: Add Photo or Add PDF Document
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Photo", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    filePickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ PDF / File", fontSize = 12.sp)
                            }
                        }

                        // Attachments Thumbnail Strip
                        if (attachmentPaths.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(attachmentPaths) { path ->
                                    val file = AttachmentFileManager.getFile(context, path)
                                    val isPdf = AttachmentFileManager.isPdf(file)

                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                            .clickable { activePreviewPath = path },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPdf) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PictureAsPdf,
                                                    contentDescription = "PDF Scan",
                                                    tint = Color(0xFFE11D48),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                Text("PDF", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold))
                                            }
                                        } else {
                                            AsyncImage(
                                                model = file,
                                                contentDescription = "Attachment thumbnail",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }

                                        // Remove Button
                                        IconButton(
                                            onClick = {
                                                attachmentPaths = attachmentPaths.filter { it != path }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(22.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove attachment",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes & Safe Location") },
                    placeholder = { Text("e.g. Original physical copy in safe locker") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // 7. Accent Color Selection
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
                        val isCustomSelected = !BankColorOptions.contains(selectedColor)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
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
                                .size(34.dp)
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
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
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

            // Footer Save Bar
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedDocType == DocType.OTHER && customDocTypeName.isBlank()) {
                                errorMessage = "Please enter custom document type name"
                                return@Button
                            }
                            if (title.isBlank()) {
                                errorMessage = "Please enter document title"
                                return@Button
                            }
                            if (docNumber.isBlank()) {
                                errorMessage = "Please enter document number"
                                return@Button
                            }

                            val doc = Document(
                                id = documentToEdit?.id ?: "doc_${UUID.randomUUID().toString().take(8)}",
                                title = title.trim(),
                                docType = selectedDocType,
                                customDocTypeName = customDocTypeName.trim().ifBlank { null },
                                docNumber = docNumber.trim(),
                                issuanceDate = issuanceDate,
                                expiryDate = expiryDate,
                                memberId = selectedMemberId,
                                notes = notes.trim().ifBlank { null },
                                attachmentPaths = attachmentPaths,
                                colorHex = selectedColor
                            )
                            onSaveDocument(doc)
                            onDismiss()
                        }
                    ) {
                        Text(if (isEditing) "Save Changes" else "Save to Vault")
                    }
                }
            }
        }
    }
}

    // Date Picker Dialog for Issue Date
    if (showIssueDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = issuanceDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showIssueDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    issuanceDate = datePickerState.selectedDateMillis
                    showIssueDatePicker = false
                }) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    issuanceDate = null
                    showIssueDatePicker = false
                }) {
                    Text("Clear")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Date Picker Dialog for Expiry Date
    if (showExpiryDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = expiryDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showExpiryDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    expiryDate = datePickerState.selectedDateMillis
                    showExpiryDatePicker = false
                }) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    expiryDate = null
                    showExpiryDatePicker = false
                }) {
                    Text("No Expiry")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // In-App Fullscreen Attachment Viewer
    if (activePreviewPath != null) {
        AttachmentViewerSheet(
            attachmentPath = activePreviewPath,
            documentTitle = title.ifBlank { selectedDocType.title },
            onDismiss = { activePreviewPath = null }
        )
    }

    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColorHex = String.format("#%06X", 0xFFFFFF and selectedColor.toInt()),
            title = "Custom Document Color Accent",
            onColorSelected = { colorLong, _ ->
                selectedColor = colorLong
            },
            onDismiss = { showCustomColorPicker = false }
        )
    }
}
