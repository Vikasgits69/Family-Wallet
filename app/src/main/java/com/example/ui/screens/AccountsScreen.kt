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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.fragment.app.FragmentActivity
import com.example.security.BiometricAuthManager
import com.example.util.SafeVaultShareManager
import com.example.util.VaultPreferencesManager
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BankAccount
import com.example.data.DisplayMode
import com.example.data.NavigationTab
import com.example.ui.components.AttachmentViewerSheet
import com.example.ui.viewmodel.FamilyWalletUiState
import com.example.util.AttachmentFileManager
import kotlinx.coroutines.delay

@Composable
fun AccountsScreen(
    uiState: FamilyWalletUiState,
    onOpenAddAccount: () -> Unit,
    onOpenEditAccount: (BankAccount) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onToggleMask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var accountToDelete by remember { mutableStateOf<BankAccount?>(null) }
    var previewAttachmentPath by remember { mutableStateOf<String?>(null) }

    // Immediate zero-latency rendering
    val isLoaded = true

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.filteredBankAccounts.isEmpty()) {
            EmptyAccountsView(onAddAccount = onOpenAddAccount)
        } else {
            when (uiState.getDisplayModeForTab(NavigationTab.ACCOUNTS)) {
                DisplayMode.CAROUSEL -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("accounts_screen"),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "LINKED BANK ACCOUNTS (${uiState.filteredBankAccounts.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(uiState.filteredBankAccounts, key = { it.id }) { account ->
                                    val member = uiState.members.find { it.id == account.memberId }
                                    Box(modifier = Modifier.width(320.dp)) {
                                        BankAccountCardItem(
                                            account = account,
                                            memberName = member?.name,
                                            isUnmasked = uiState.isItemUnmasked(account.id),
                                            onToggleMask = { onToggleMask(account.id) },
                                            onEdit = { onOpenEditAccount(account) },
                                            onDelete = { accountToDelete = account },
                                            onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                            onViewAttachment = { previewAttachmentPath = it }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                DisplayMode.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("accounts_screen"),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.filteredBankAccounts, key = { it.id }) { account ->
                            val member = uiState.members.find { it.id == account.memberId }
                            BankAccountCardItem(
                                account = account,
                                memberName = member?.name,
                                isUnmasked = uiState.isItemUnmasked(account.id),
                                onToggleMask = { onToggleMask(account.id) },
                                onEdit = { onOpenEditAccount(account) },
                                onDelete = { accountToDelete = account },
                                onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                onViewAttachment = { previewAttachmentPath = it }
                            )
                        }
                    }
                }

                DisplayMode.LIST -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("accounts_screen"),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(uiState.filteredBankAccounts, key = { it.id }) { account ->
                            AnimatedVisibility(visible = isLoaded, enter = slideInVertically(initialOffsetY = { 30 }) + fadeIn()) {
                                val member = uiState.members.find { it.id == account.memberId }
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    BankAccountCardItem(
                                        account = account,
                                        memberName = member?.name,
                                        isUnmasked = uiState.isItemUnmasked(account.id),
                                        onToggleMask = { onToggleMask(account.id) },
                                        onEdit = { onOpenEditAccount(account) },
                                        onDelete = { accountToDelete = account },
                                        onCopyText = { label, value -> copyToClipboard(context, label, value) },
                                        onViewAttachment = { previewAttachmentPath = it },
                                        modifier = Modifier.widthIn(max = 380.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to Link Bank Account
        FloatingActionButton(
            onClick = onOpenAddAccount,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_account_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Link Bank Account")
                Text("Link Bank", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Delete Confirmation Dialog
    accountToDelete?.let { acc ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text("Delete ${acc.bankName} Account?") },
            text = { Text("Are you sure you want to remove account ending in •••• ${acc.accountNumber.takeLast(4)} from the vault?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAccount(acc.id)
                        accountToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (previewAttachmentPath != null) {
        AttachmentViewerSheet(
            attachmentPath = previewAttachmentPath,
            documentTitle = "Bank Document Viewer",
            onDismiss = { previewAttachmentPath = null }
        )
    }
}

/**
 * Bank Account Card in Google Wallet Vibrant Style with Custom Accent Color
 */
@Composable
private fun BankAccountCardItem(
    account: BankAccount,
    memberName: String?,
    isUnmasked: Boolean,
    onToggleMask: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyText: (String, String) -> Unit,
    onViewAttachment: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = Color(account.colorHex)
    var isSecretRevealed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bank_account_${account.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.15f),
                            accentColor.copy(alpha = 0.04f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Row: Bank Icon, Bank Name, Account Type & Edit/Delete
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalance,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = account.bankName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = accentColor.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = account.accountType,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = accentColor
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                SafeVaultShareManager.shareBankAccountDetails(
                                    context = context,
                                    account = account,
                                    includeBeneficiaryName = !memberName.isNullOrBlank(),
                                    beneficiaryName = memberName ?: ""
                                )
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Account Details",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Account", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Account", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Account Number Row
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
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
                            text = "ACCOUNT NUMBER",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isUnmasked) account.accountNumber else "•••• •••• ${account.accountNumber.takeLast(4)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onToggleMask, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (isUnmasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Mask",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { onCopyText("Account Number", account.accountNumber) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy Account Number",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // IFSC & MICR Codes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { onCopyText("IFSC Code", account.ifscCode) },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("IFSC CODE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy IFSC", modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = account.ifscCode,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (account.micrCode.isNotBlank()) {
                    Surface(
                        onClick = { onCopyText("MICR Code", account.micrCode) },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("MICR CODE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy MICR", modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = account.micrCode,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // CIF Number / Client Code
            if (!account.cifOrClientCode.isNullOrBlank()) {
                Surface(
                    onClick = { onCopyText("CIF / Client Code", account.cifOrClientCode) },
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
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
                                text = "CIF NUMBER / CLIENT CODE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = account.cifOrClientCode,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy CIF Code",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Linked Email & Phone Number
            if (account.linkedEmail.isNotBlank() || account.linkedPhone.isNotBlank()) {
                Surface(
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
                        if (account.linkedEmail.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.clickable { onCopyText("Linked Email", account.linkedEmail) }
                            ) {
                                Icon(Icons.Outlined.Email, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                Text(
                                    text = account.linkedEmail,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        if (account.linkedPhone.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.clickable { onCopyText("Linked Phone", account.linkedPhone) }
                            ) {
                                Icon(Icons.Outlined.Phone, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                                Text(
                                    text = account.linkedPhone,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Digital & Mobile Banking Credentials Vault Section
            if (account.netBankingUserId.isNotBlank() || account.netBankingPassword.isNotBlank() ||
                account.mobileBankingUserId.isNotBlank() || account.mobileBankingPassword.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = accentColor.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header with Biometric Reveal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                                Text(
                                    text = "ONLINE & MOBILE BANKING VAULT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                                    color = accentColor
                                )
                            }

                            Surface(
                                onClick = {
                                    if (!isSecretRevealed) {
                                        (context as? FragmentActivity)?.let { activity ->
                                            BiometricAuthManager.authenticateForSecret(activity, "Banking Password") {
                                                isSecretRevealed = true
                                            }
                                        } ?: run { isSecretRevealed = true }
                                    } else {
                                        isSecretRevealed = false
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                color = accentColor.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSecretRevealed) Icons.Default.VisibilityOff else Icons.Default.Fingerprint,
                                        contentDescription = if (isSecretRevealed) "Hide Passwords" else "Fingerprint to view passwords",
                                        tint = accentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isSecretRevealed) "Hide" else "Tap Fingerprint",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = accentColor
                                    )
                                }
                            }
                        }

                        // Internet Banking Credentials
                        if (account.netBankingUserId.isNotBlank() || account.netBankingPassword.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "🌐 Internet Banking",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = accentColor
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (account.netBankingUserId.isNotBlank()) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { onCopyText("Net Banking User ID", account.netBankingUserId) },
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("USER ID", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(account.netBankingUserId, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                                                }
                                                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy User ID", modifier = Modifier.size(12.dp), tint = accentColor)
                                            }
                                        }

                                        if (account.netBankingPassword.isNotBlank()) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        if (isSecretRevealed) onCopyText("Net Banking Password", account.netBankingPassword)
                                                    },
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("PASSWORD", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(
                                                        text = if (isSecretRevealed) account.netBankingPassword else "••••••••",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                                if (isSecretRevealed) {
                                                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Password", modifier = Modifier.size(12.dp), tint = accentColor)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Mobile Banking Credentials
                        if (account.mobileBankingUserId.isNotBlank() || account.mobileBankingPassword.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "📱 Mobile Banking App",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = accentColor
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (account.mobileBankingUserId.isNotBlank()) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { onCopyText("Mobile Banking ID", account.mobileBankingUserId) },
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("USER ID / MOBILE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(account.mobileBankingUserId, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                                                }
                                                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Mobile ID", modifier = Modifier.size(12.dp), tint = accentColor)
                                            }
                                        }

                                        if (account.mobileBankingPassword.isNotBlank()) {
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        if (isSecretRevealed) onCopyText("Mobile Banking MPIN / Password", account.mobileBankingPassword)
                                                    },
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("MPIN / PASS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(
                                                        text = if (isSecretRevealed) account.mobileBankingPassword else "••••••",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                                if (isSecretRevealed) {
                                                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy MPIN", modifier = Modifier.size(12.dp), tint = accentColor)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Attached Scans & Documents (Cheque, Passbook, PDFs)
            val allAttached = listOfNotNull(
                account.chequeBookImagePath?.let { Pair("Cheque Book", it) },
                account.passbookImagePath?.let { Pair("Passbook", it) }
            ) + account.attachmentPaths.map { Pair("Document", it) }

            if (allAttached.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allAttached) { (tag, path) ->
                        Surface(
                            onClick = { onViewAttachment(path) },
                            shape = RoundedCornerShape(8.dp),
                            color = accentColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = accentColor, modifier = Modifier.size(12.dp))
                                Text(tag, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), color = accentColor)
                            }
                        }
                    }
                }
            }

            // Account Holder & Assigned Member Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ACCOUNT HOLDER", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(account.accountHolderName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                }

                if (!memberName.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                        Text(memberName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = accentColor)
                    }
                }
            }
        }
    }
}
}

@Composable
private fun EmptyAccountsView(onAddAccount: () -> Unit) {
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
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Bank Accounts Linked",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Securely vault your savings, current, overdraft, or loan account details along with IFSC, MICR, and branch info.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(onClick = onAddAccount, shape = RoundedCornerShape(12.dp)) {
                Text("+ Link Bank Account")
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
