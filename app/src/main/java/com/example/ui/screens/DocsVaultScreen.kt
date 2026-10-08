package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DisplayMode
import com.example.data.DocType
import com.example.data.Document
import com.example.data.FamilyMember
import com.example.data.NavigationTab
import com.example.ui.components.AttachmentViewerSheet
import com.example.ui.viewmodel.FamilyWalletUiState
import com.example.util.AttachmentFileManager
import com.example.util.SafeVaultShareManager
import com.example.util.VaultPreferencesManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsVaultScreen(
    uiState: FamilyWalletUiState,
    onOpenAddDocument: () -> Unit,
    onOpenEditDocument: (Document) -> Unit,
    onDeleteDocument: (String) -> Unit,
    onToggleMask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTypeFilter by remember { mutableStateOf<DocType?>(null) }
    var docToDelete by remember { mutableStateOf<Document?>(null) }
    var previewAttachmentPath by remember { mutableStateOf<String?>(null) }
    var previewDocTitle by remember { mutableStateOf("Attachment") }
    val context = LocalContext.current

    val filteredList by remember(uiState.filteredDocuments, selectedTypeFilter) {
        derivedStateOf {
            if (selectedTypeFilter == null) {
                uiState.filteredDocuments
            } else {
                uiState.filteredDocuments.filter { it.docType == selectedTypeFilter }
            }
        }
    }

    val isVaultEmpty by remember(uiState.filteredDocuments) {
        derivedStateOf { uiState.filteredDocuments.isEmpty() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Category Filter Chips Carousel
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedTypeFilter == null,
                            onClick = { selectedTypeFilter = null },
                            label = { Text("All Vault Docs (${uiState.filteredDocuments.size})") }
                        )
                    }
                    items(DocType.values()) { dtype ->
                        val count = uiState.filteredDocuments.count { it.docType == dtype }
                        FilterChip(
                            selected = selectedTypeFilter == dtype,
                            onClick = { selectedTypeFilter = if (selectedTypeFilter == dtype) null else dtype },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(dtype.iconEmoji)
                                    Text("${dtype.title} ($count)")
                                }
                            }
                        )
                    }
                }
            }

            if (isVaultEmpty) {
                EmptyDocsView(onAddDocument = onOpenAddDocument)
            } else {
                when (uiState.getDisplayModeForTab(NavigationTab.DOCUMENTS)) {
                    DisplayMode.CAROUSEL -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("docs_vault_carousel"),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "SECURE DOCUMENTS VAULT (${filteredList.size})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    items(filteredList, key = { it.id }) { doc ->
                                        val member = uiState.members.find { it.id == doc.memberId }
                                        Box(modifier = Modifier.width(300.dp)) {
                                            DocumentCardItem(
                                                doc = doc,
                                                member = member,
                                                isUnmasked = uiState.isItemUnmasked(doc.id),
                                                onToggleMask = { onToggleMask(doc.id) },
                                                onEdit = { onOpenEditDocument(doc) },
                                                onDelete = { docToDelete = doc },
                                                onPreviewAttachment = { path ->
                                                    previewAttachmentPath = path
                                                    previewDocTitle = doc.displayTitle
                                                },
                                                onCopy = { label, value -> copyToClipboard(context, label, value) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    DisplayMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("docs_vault_grid"),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList, key = { it.id }) { doc ->
                                val member = uiState.members.find { it.id == doc.memberId }
                                DocumentCardItem(
                                    doc = doc,
                                    member = member,
                                    isUnmasked = uiState.isItemUnmasked(doc.id),
                                    onToggleMask = { onToggleMask(doc.id) },
                                    onEdit = { onOpenEditDocument(doc) },
                                    onDelete = { docToDelete = doc },
                                    onPreviewAttachment = { path ->
                                        previewAttachmentPath = path
                                        previewDocTitle = doc.displayTitle
                                    },
                                    onCopy = { label, value -> copyToClipboard(context, label, value) }
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("docs_vault_list"),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredList, key = { it.id }) { doc ->
                                AnimatedVisibility(visible = true, enter = slideInVertically(initialOffsetY = { 30 }) + fadeIn()) {
                                    val member = uiState.members.find { it.id == doc.memberId }
                                    DocumentCardItem(
                                        doc = doc,
                                        member = member,
                                        isUnmasked = uiState.isItemUnmasked(doc.id),
                                        onToggleMask = { onToggleMask(doc.id) },
                                        onEdit = { onOpenEditDocument(doc) },
                                        onDelete = { docToDelete = doc },
                                        onPreviewAttachment = { path ->
                                            previewAttachmentPath = path
                                            previewDocTitle = doc.displayTitle
                                        },
                                        onCopy = { label, value -> copyToClipboard(context, label, value) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to Add Document
        FloatingActionButton(
            onClick = onOpenAddDocument,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_doc_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
                Text("Add Doc", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Delete Confirmation Dialog
    docToDelete?.let { doc ->
        AlertDialog(
            onDismissRequest = { docToDelete = null },
            title = { Text("Delete ${doc.displayTitle}?") },
            text = { Text("Are you sure you want to permanently remove ${doc.displayTitle} (${doc.docNumber}) from your encrypted vault?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDocument(doc.id)
                        docToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { docToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // In-App Attachment Viewer Modal Sheet
    if (previewAttachmentPath != null) {
        AttachmentViewerSheet(
            attachmentPath = previewAttachmentPath,
            documentTitle = previewDocTitle,
            onDismiss = { previewAttachmentPath = null }
        )
    }
}

@Composable
fun DocumentCardItem(
    doc: Document,
    member: FamilyMember?,
    isUnmasked: Boolean,
    onToggleMask: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPreviewAttachment: (String) -> Unit,
    onCopy: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(doc.colorHex)
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("doc_card_${doc.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Emoji icon + Title + Member chip + Edit/Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = doc.docType.iconEmoji, fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = doc.displayTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        if (member != null) {
                            Text(
                                text = "Assigned to: ${member.name} (${member.relationship})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "Shared Vault Document",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expiry & Validity Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val daysLeft = doc.daysUntilExpiry
                when {
                    doc.isExpired -> {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Expired on ${doc.expiryDate?.let { dateFormat.format(Date(it)) } ?: "Unknown"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    daysLeft != null && daysLeft <= 60 -> {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7) // Amber light container
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Expires in $daysLeft days (${doc.expiryDate?.let { dateFormat.format(Date(it)) }})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                    doc.expiryDate != null -> {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "Valid until ${dateFormat.format(Date(doc.expiryDate))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ) {
                            Text(
                                text = "Lifetime / No Expiry",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                if (doc.attachmentPaths.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${doc.attachmentPaths.size} file(s)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Document Identification Number Pill with Mask & Copy
            val displayedNumber = if (isUnmasked) doc.docNumber else {
                if (doc.docNumber.length > 4) {
                    "•••• •••• " + doc.docNumber.takeLast(4)
                } else {
                    "••••"
                }
            }

            Surface(
                onClick = { onCopy(doc.displayTitle, doc.docNumber) },
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DOCUMENT NUMBER (TAP TO COPY)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                        Text(
                            text = displayedNumber,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row {
                        IconButton(onClick = onToggleMask, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (isUnmasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle mask",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(onClick = { onCopy(doc.displayTitle, doc.docNumber) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy number",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // In-Card Attachment Thumbnails Grid
            if (doc.attachmentPaths.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(doc.attachmentPaths) { path ->
                        val file = AttachmentFileManager.getFile(context, path)
                        val isPdf = AttachmentFileManager.isPdf(file)

                        Surface(
                            onClick = { onPreviewAttachment(path) },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.size(56.dp)
                        ) {
                            if (isPdf) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF Scan",
                                        tint = Color(0xFFE11D48),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text("PDF", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                AsyncImage(
                                    model = file,
                                    contentDescription = "Document Scan Thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Notes / Safe Location footer if present
            if (!doc.notes.isNullOrBlank()) {
                Text(
                    text = "📍 ${doc.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun EmptyDocsView(onAddDocument: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Personal Documents in Vault",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Securely store Aadhaar cards, PAN cards, Passports, Voter IDs, Driving Licences, Insurance Policies, Vehicle RCs, and Property deeds with multi-page PDF & photo scans.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(onClick = onAddDocument, shape = RoundedCornerShape(12.dp)) {
                Text("+ Add Personal Document")
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val prefs = VaultPreferencesManager(context)
    val autoClear = prefs.isClipboardAutoClearEnabled()
    val timeout = prefs.getClipboardClearTimeout()
    SafeVaultShareManager.copyWithSecurity(
        context = context,
        label = label,
        text = text,
        timeoutSeconds = timeout,
        enableAutoClear = autoClear
    )
}
