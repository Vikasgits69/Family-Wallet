package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.viewmodel.FamilyWalletUiState

data class SecurityCheckItem(
    val title: String,
    val description: String,
    val isPassed: Boolean,
    val isWarning: Boolean = false
)

@Composable
fun SecurityCheckupDialog(
    uiState: FamilyWalletUiState,
    onDismiss: () -> Unit
) {
    val checkItems = remember(uiState) {
        val list = mutableListOf<SecurityCheckItem>()

        // 1. Biometric Protection
        list.add(
            SecurityCheckItem(
                title = "App Biometric Lock",
                description = if (uiState.biometricEnabled) "Biometric fingerprint & device lock are active" else "Biometric lock is disabled",
                isPassed = uiState.biometricEnabled
            )
        )

        // 2. Cloud Backup Sync
        val hasDriveBackup = uiState.driveSync.lastSyncTimestamp != null
        list.add(
            SecurityCheckItem(
                title = "Encrypted Cloud Backup",
                description = if (hasDriveBackup) "Google Drive backup is synchronized" else "No recent cloud backup found. Back up in Settings",
                isPassed = hasDriveBackup,
                isWarning = !hasDriveBackup
            )
        )

        // 3. Card PIN Coverage
        val totalCards = uiState.creditCards.size + uiState.debitCards.size
        val cardsWithPin = uiState.creditCards.count { it.atmPin.isNotBlank() || it.cardPin.isNotBlank() } +
                uiState.debitCards.count { it.atmPin.isNotBlank() || it.cardPin.isNotBlank() }
        val cardPinPassed = totalCards == 0 || (cardsWithPin == totalCards)
        list.add(
            SecurityCheckItem(
                title = "Card PINs Stored Securely",
                description = if (cardPinPassed) "All cards have protected ATM/POS PINs" else "$cardsWithPin of $totalCards cards have PINs stored",
                isPassed = cardPinPassed,
                isWarning = !cardPinPassed
            )
        )

        // 4. NetBanking Credential Backups
        val totalBanks = uiState.bankAccounts.size
        val banksWithCreds = uiState.bankAccounts.count { it.netBankingUserId.isNotBlank() || it.mobileBankingUserId.isNotBlank() }
        val bankCredsPassed = totalBanks == 0 || (banksWithCreds > 0)
        list.add(
            SecurityCheckItem(
                title = "NetBanking & MPIN Access",
                description = if (bankCredsPassed) "Banking credentials backed up for quick access" else "No digital banking credentials stored for accounts",
                isPassed = bankCredsPassed,
                isWarning = !bankCredsPassed
            )
        )

        // 5. Expiring Documents
        val now = System.currentTimeMillis()
        val in30Days = now + (30L * 24 * 60 * 60 * 1000)
        val expiringDocs = uiState.documents.count { it.expiryDate != null && it.expiryDate in now..in30Days }
        val expiredDocs = uiState.documents.count { it.expiryDate != null && it.expiryDate < now }
        val docsPassed = expiringDocs == 0 && expiredDocs == 0
        list.add(
            SecurityCheckItem(
                title = "Document Validity & Health",
                description = when {
                    expiredDocs > 0 -> "$expiredDocs document(s) have expired! Renew them promptly"
                    expiringDocs > 0 -> "$expiringDocs document(s) expiring within 30 days"
                    else -> "All personal and vehicle documents are active and valid"
                },
                isPassed = docsPassed,
                isWarning = expiringDocs > 0
            )
        )

        // 6. Emergency Contacts Setup
        val emergencyMembers = uiState.members.count { it.isEmergencyContact }
        val hasIce = emergencyMembers > 0
        list.add(
            SecurityCheckItem(
                title = "Emergency Family Contact (ICE)",
                description = if (hasIce) "$emergencyMembers family member(s) tagged as Emergency Nominee" else "No family member nominated for emergency access",
                isPassed = hasIce,
                isWarning = !hasIce
            )
        )

        // 7. Member Asset Assignment
        val unassignedAssets = uiState.creditCards.count { it.memberId.isBlank() } +
                uiState.debitCards.count { it.memberId.isBlank() } +
                uiState.bankAccounts.count { it.memberId.isBlank() } +
                uiState.documents.count { it.memberId.isBlank() }
        val assignmentPassed = unassignedAssets == 0
        list.add(
            SecurityCheckItem(
                title = "Family Asset Ownership",
                description = if (assignmentPassed) "All assets are mapped to specific family members" else "$unassignedAssets item(s) unassigned to family members",
                isPassed = assignmentPassed,
                isWarning = !assignmentPassed
            )
        )

        list
    }

    val passedCount = checkItems.count { it.isPassed }
    val securityScore = (passedCount.toFloat() / checkItems.size.coerceAtLeast(1) * 100).toInt()

    val scoreColor = when {
        securityScore >= 80 -> Color(0xFF10B981)
        securityScore >= 50 -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.error
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .widthIn(max = 640.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = scoreColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = scoreColor,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Vault Security Checkup",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Comprehensive family privacy & health audit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.10f)),
                    border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "VAULT HEALTH SCORE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = scoreColor
                            )
                            Text(
                                text = "$securityScore%",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = scoreColor
                            )
                            Text(
                                text = if (securityScore >= 80) "Excellent! Your financial vault is well-protected." else "Attention needed: Address flagged items below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                            CircularProgressIndicator(
                                progress = { securityScore / 100f },
                                modifier = Modifier.size(64.dp),
                                color = scoreColor,
                                strokeWidth = 6.dp,
                                trackColor = scoreColor.copy(alpha = 0.2f)
                            )
                            Text(
                                text = "$passedCount/${checkItems.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = scoreColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SECURITY CHECKLIST ITEMS",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(checkItems) { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        item.isPassed -> Icons.Default.CheckCircle
                                        item.isWarning -> Icons.Default.Warning
                                        else -> Icons.Default.Error
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        item.isPassed -> Color(0xFF10B981)
                                        item.isWarning -> Color(0xFFF59E0B)
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    modifier = Modifier.size(20.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
