package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.example.data.AppThemeMode
import com.example.data.ThemeCategory
import com.example.ui.components.CustomColorPickerDialog
import com.example.ui.components.GoogleDriveSyncCard
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.parseHexColor
import com.example.ui.viewmodel.FamilyWalletUiState

@Composable
fun SettingsScreen(
    uiState: FamilyWalletUiState,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onSetCustomAccent: (String?) -> Unit = {},
    onToggleBiometric: () -> Unit,
    onLockApp: () -> Unit,
    onSyncGoogleDrive: () -> Unit,
    onRestoreGoogleDrive: () -> Unit,
    onUpdateAutoBackupSettings: (String, Boolean, Boolean) -> Unit = { _, _, _ -> },
    onToggleIncludePhotosInBackup: (Boolean) -> Unit = {},
    onToggleClipboardAutoClear: () -> Unit = {},
    onSetClipboardTimeout: (Int) -> Unit = {},
    onOpenSecurityCheckup: () -> Unit = {},
    onOpenEmergencyIce: () -> Unit = {},
    onOpenHelplines: () -> Unit = {},
    onExportJson: () -> Unit = {},
    onImportJson: (String) -> Unit = {},
    onLoadBackups: () -> Unit = {},
    onRestoreSpecificBackup: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ThemeCategory.ALL) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showCustomAccentDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    val filteredThemes = remember(selectedCategory) {
        if (selectedCategory == ThemeCategory.ALL) {
            AppThemeMode.values().toList()
        } else {
            AppThemeMode.values().filter { it.category == selectedCategory }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Appearance & Theme Selection: Clean Organized Filter + Carousel
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with active theme pill
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Themes & Aesthetics",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${AppThemeMode.values().size} crafted palettes • Tap to apply instantly",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${uiState.themeMode.emoji} ${uiState.themeMode.title}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Theme Category Filter Chips (Prevents clutter by organizing into curated vibes)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ThemeCategory.values().toList(), key = { it.name }) { cat ->
                            val isCatSelected = selectedCategory == cat
                            FilterChip(
                                selected = isCatSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text("${cat.emoji} ${cat.label}", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // Side-Animated Scrollable Theme Card Row (Filtered & Uncluttered)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        if (selectedCategory == ThemeCategory.ALL) {
                            item(key = "custom_accent_theme_card") {
                                val hasCustomAccent = uiState.customAccentColorHex != null
                                Surface(
                                    modifier = Modifier
                                        .width(112.dp)
                                        .clickable { showCustomAccentDialog = true }
                                        .testTag("theme_card_custom_accent"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (hasCustomAccent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (hasCustomAccent) 2.dp else 1.dp,
                                        color = if (hasCustomAccent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        (parseHexColor(uiState.customAccentColorHex) ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.25f),
                                                        MaterialTheme.colorScheme.surface
                                                    )
                                                )
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(9.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "🎨", fontSize = 18.sp)
                                                if (hasCustomAccent) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.primary),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Active Custom Accent",
                                                            tint = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = "Custom Accent",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )

                                            Text(
                                                text = uiState.customAccentColorHex ?: "Pick Color...",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                                listOf(Color(0xFF4F46E5), Color(0xFF06B6D4), Color(0xFF10B981)).forEach { swatchColor ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(7.dp)
                                                            .clip(CircleShape)
                                                            .background(swatchColor)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        items(filteredThemes, key = { it.name }) { mode ->
                            val isSelected = uiState.themeMode == mode
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.02f else 1.0f,
                                animationSpec = tween(200),
                                label = "scale_${mode.name}"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                animationSpec = tween(200),
                                label = "border_${mode.name}"
                            )

                            // Realistic preview gradient for theme card
                            val previewBackgroundBrush = when (mode) {
                                AppThemeMode.DOODLE -> Brush.verticalGradient(listOf(Color(0xFFFFFDF8), Color(0xFFF6F0DF)))
                                AppThemeMode.DOODLE_DARK -> Brush.verticalGradient(listOf(Color(0xFF1E212B), Color(0xFF13151A)))
                                AppThemeMode.LIGHT -> Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9)))
                                AppThemeMode.DARK -> Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                AppThemeMode.PITCH_BLACK -> Brush.verticalGradient(listOf(Color(0xFF111111), Color(0xFF000000)))
                                AppThemeMode.HIGH_CONTRAST -> Brush.verticalGradient(listOf(Color(0xFF050505), Color(0xFF000000)))
                                AppThemeMode.PAPERLIKE -> Brush.verticalGradient(listOf(Color(0xFFFEF9EE), Color(0xFFF6E7C8)))
                                AppThemeMode.EMERALD_VAULT -> Brush.verticalGradient(listOf(Color(0xFF0B382D), Color(0xFF041712)))
                                AppThemeMode.MIDNIGHT_ROSE -> Brush.verticalGradient(listOf(Color(0xFF2B1437), Color(0xFF0F0713)))
                                AppThemeMode.PLATINUM_LUXURY -> Brush.verticalGradient(listOf(Color(0xFF20252D), Color(0xFF0B0D10)))
                                AppThemeMode.CYBER_NEON -> Brush.verticalGradient(listOf(Color(0xFF131A3E), Color(0xFF060814)))
                                AppThemeMode.NORDIC_FROST -> Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE0F2FE)))
                                AppThemeMode.SUNSET_AMBER -> Brush.verticalGradient(listOf(Color(0xFFFFF7ED), Color(0xFFFAF5EE)))
                                AppThemeMode.MATCHA_SAGE -> Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF7F8F2)))
                                AppThemeMode.SYSTEM -> Brush.verticalGradient(listOf(Color(0xFF2563EB).copy(alpha = 0.2f), Color(0xFF7C3AED).copy(alpha = 0.2f)))
                            }

                            val previewTextColor = when (mode) {
                                AppThemeMode.DOODLE, AppThemeMode.LIGHT, AppThemeMode.PAPERLIKE,
                                AppThemeMode.NORDIC_FROST, AppThemeMode.SUNSET_AMBER, AppThemeMode.MATCHA_SAGE -> Color(0xFF1E293B)
                                else -> Color(0xFFF8FAFC)
                            }

                            Surface(
                                modifier = Modifier
                                    .width(112.dp)
                                    .scale(scale)
                                    .clickable { onSetThemeMode(mode) }
                                    .testTag("theme_card_${mode.name.lowercase()}"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = borderColor
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(previewBackgroundBrush)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(9.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = mode.emoji, fontSize = 18.sp)
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Active Theme",
                                                        tint = MaterialTheme.colorScheme.onPrimary,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = mode.title,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                            color = previewTextColor,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = mode.subtitle,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = previewTextColor.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Color swatch preview dots
                                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                            val swatches = when (mode) {
                                                AppThemeMode.DOODLE -> listOf(Color(0xFFFF6B6B), Color(0xFF4D96FF), Color(0xFFFFD93D))
                                                AppThemeMode.DOODLE_DARK -> listOf(Color(0xFF13151A), Color(0xFFFF758F), Color(0xFF38BDF8))
                                                AppThemeMode.LIGHT -> listOf(Color(0xFF1A73E8), Color(0xFF059669), Color(0xFFD97706))
                                                AppThemeMode.DARK -> listOf(Color(0xFF6366F1), Color(0xFF06B6D4), Color(0xFF10B981))
                                                AppThemeMode.PITCH_BLACK -> listOf(Color(0xFF000000), Color(0xFF60A5FA), Color(0xFF38BDF8))
                                                AppThemeMode.HIGH_CONTRAST -> listOf(Color(0xFF000000), Color(0xFFFFEA00), Color(0xFF00FFFF))
                                                AppThemeMode.PAPERLIKE -> listOf(Color(0xFF92400E), Color(0xFF2D6A4F), Color(0xFFFDE68A))
                                                AppThemeMode.EMERALD_VAULT -> listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF34D399))
                                                AppThemeMode.MIDNIGHT_ROSE -> listOf(Color(0xFFF43F5E), Color(0xFFA855F7), Color(0xFFFB7185))
                                                AppThemeMode.PLATINUM_LUXURY -> listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8), Color(0xFFE2E8F0))
                                                AppThemeMode.CYBER_NEON -> listOf(Color(0xFF00F5FF), Color(0xFFD946EF), Color(0xFF38BDF8))
                                                AppThemeMode.NORDIC_FROST -> listOf(Color(0xFF0284C7), Color(0xFF0EA5E9), Color(0xFF64748B))
                                                AppThemeMode.SUNSET_AMBER -> listOf(Color(0xFFEA580C), Color(0xFFD97706), Color(0xFFB45309))
                                                AppThemeMode.MATCHA_SAGE -> listOf(Color(0xFF4D7C0F), Color(0xFF65A30D), Color(0xFF3F6212))
                                                AppThemeMode.SYSTEM -> listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF10B981))
                                            }
                                            swatches.forEach { swatchColor ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(swatchColor)
                                                        .border(0.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
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
        }

        // 1.5. Custom Accent Color Picker & Hex Code Input
        item {
            CustomAccentColorCard(
                currentAccentHex = uiState.customAccentColorHex,
                onSetCustomAccent = onSetCustomAccent
            )
        }

        // 2. Google Drive Cloud Sync & Encrypted Vault Restore
        item {
            GoogleDriveSyncCard(
                syncState = uiState.driveSync,
                autoBackupFrequency = uiState.autoBackupFrequency,
                autoBackupOnOpen = uiState.autoBackupOnOpen,
                autoBackupOnClose = uiState.autoBackupOnClose,
                includePhotosInBackup = uiState.includePhotosInBackup,
                onToggleIncludePhotos = onToggleIncludePhotosInBackup,
                onUpdateAutoBackupSettings = onUpdateAutoBackupSettings,
                onSyncNow = onSyncGoogleDrive,
                onRestoreNow = onRestoreGoogleDrive,
                availableBackups = uiState.availableDriveBackups,
                onLoadBackups = onLoadBackups,
                onRestoreSpecificBackup = onRestoreSpecificBackup
            )
        }

        // 3. Biometric Security Card (Clean without redundant titles/subtitles)
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldMint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = EmeraldMint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Biometric Authentication",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Locks automatically when app is closed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = uiState.biometricEnabled,
                            onCheckedChange = { onToggleBiometric() },
                            modifier = Modifier.testTag("biometric_switch")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Self-Destructing Clipboard Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldMint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = EmeraldMint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Auto-Clear Clipboard",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Wipes copied card numbers, CVVs & banking passwords automatically",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = uiState.clipboardAutoClearEnabled,
                            onCheckedChange = { onToggleClipboardAutoClear() },
                            modifier = Modifier.testTag("clipboard_auto_clear_switch")
                        )
                    }

                    if (uiState.clipboardAutoClearEnabled) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Wipe Timeout Duration",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "${uiState.clipboardClearTimeoutSeconds} seconds",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(15, 30, 60).forEach { sec ->
                                        val isSelected = uiState.clipboardClearTimeoutSeconds == sec
                                        Surface(
                                            onClick = { onSetClipboardTimeout(sec) },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${sec}s",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Button(
                        onClick = onLockApp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lock_vault_now_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lock Vault Immediately")
                    }
                }
            }
        }

        // 3. Vault Audit & Security Checkup Card
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().testTag("vault_security_checkup_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(IndigoAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(22.dp))
                            }
                            Column {
                                Text("Vault Security & Audit", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("PIN protection, duplicate check & scans audit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenSecurityCheckup,
                        modifier = Modifier.fillMaxWidth().testTag("run_security_checkup_button")
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Run Security Checkup & Audit")
                    }
                }
            }
        }

        // 4. Emergency ICE & Bank Helplines
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().testTag("emergency_helpline_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE11D48).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(22.dp))
                            }
                            Column {
                                Text("Emergency Access & Helplines", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Nominees, ICE contacts & bank quick-dialers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onOpenEmergencyIce,
                            modifier = Modifier.weight(1f).testTag("emergency_ice_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ICE Vault", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenHelplines,
                            modifier = Modifier.weight(1f).testTag("bank_helplines_button")
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bank Helplines", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. Offline Encrypted Local Backup (Export / Import JSON)
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().testTag("local_backup_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(EmeraldMint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, tint = EmeraldMint, modifier = Modifier.size(22.dp))
                            }
                            Column {
                                Text("Offline Encrypted Backup", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Export/Import encrypted JSON to device or SD card", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = onExportJson,
                            modifier = Modifier.weight(1f).testTag("export_backup_button")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export JSON", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.weight(1f).testTag("import_backup_button")
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import JSON", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 6. App Credits & Creator Badge
        item {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_credits_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "VG",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Text(
                        text = "App designed and Developed by\nCA Vikas S Gupta",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Family Financial Vault • v2.4.0",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Encrypted Vault Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Paste the exported encrypted JSON payload below to restore cards, accounts, wallets, documents and subscriptions:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        placeholder = { Text("{\"version\":3,\"data\":\"...\"}") },
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            onImportJson(importJsonText.trim())
                            showImportDialog = false
                            importJsonText = ""
                        }
                    }
                ) {
                    Text("Restore Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCustomAccentDialog) {
        CustomColorPickerDialog(
            initialColorHex = uiState.customAccentColorHex ?: "#4F46E5",
            title = "Custom Theme Accent Color",
            onColorSelected = { _, hex ->
                onSetCustomAccent(hex)
            },
            onDismiss = { showCustomAccentDialog = false }
        )
    }
}

@Composable
fun CustomAccentColorCard(
    currentAccentHex: String?,
    onSetCustomAccent: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var hexInputText by remember(currentAccentHex) {
        mutableStateOf(currentAccentHex ?: "#4F46E5")
    }

    // Initialize HSV from current accent or default
    val initialHsv = remember(currentAccentHex) {
        val hsv = FloatArray(3)
        val color = parseHexColor(currentAccentHex ?: "#4F46E5") ?: Color(0xFF4F46E5)
        android.graphics.Color.colorToHSV(
            android.graphics.Color.argb(
                (color.alpha * 255).toInt(),
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            ),
            hsv
        )
        hsv
    }

    var hue by remember(currentAccentHex) { mutableStateOf(initialHsv[0]) }
    var saturation by remember(currentAccentHex) { mutableStateOf(initialHsv[1]) }
    var value by remember(currentAccentHex) { mutableStateOf(initialHsv[2]) }
    var isSlidersExpanded by remember { mutableStateOf(false) }

    val currentColorFromSliders = remember(hue, saturation, value) {
        val colorInt = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
        Color(colorInt)
    }

    val computedSliderHex = remember(hue, saturation, value) {
        val colorInt = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
        String.format("#%06X", 0xFFFFFF and colorInt)
    }

    // Parsed color from text input
    val parsedInputColor = remember(hexInputText) {
        parseHexColor(hexInputText)
    }
    val isHexValid = parsedInputColor != null

    val presetAccents = listOf(
        Pair("Indigo", "#4F46E5"),
        Pair("Cyan", "#0284C7"),
        Pair("Teal", "#0D9488"),
        Pair("Emerald", "#10B981"),
        Pair("Lime", "#84CC16"),
        Pair("Amber", "#F59E0B"),
        Pair("Orange", "#F97316"),
        Pair("Rose", "#E11D48"),
        Pair("Pink", "#EC4899"),
        Pair("Purple", "#8B5CF6")
    )

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                (parseHexColor(currentAccentHex) ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = parseHexColor(currentAccentHex) ?: MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Custom Accent Color",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Choose or type any custom HEX color",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Active badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (currentAccentHex != null) (parseHexColor(currentAccentHex) ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentAccentHex != null) (parseHexColor(currentAccentHex) ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (currentAccentHex != null) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(currentAccentHex) ?: MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = currentAccentHex.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = "Theme Default",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick Preset Swatches
            Text(
                text = "POPULAR ACCENT PALETTES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = MaterialTheme.colorScheme.primary
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presetAccents, key = { it.second }) { (name, hex) ->
                    val color = parseHexColor(hex) ?: Color.Gray
                    val isSelected = currentAccentHex?.equals(hex, ignoreCase = true) == true

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                hexInputText = hex
                                val hsv = FloatArray(3)
                                android.graphics.Color.colorToHSV(
                                    android.graphics.Color.argb((color.alpha * 255).toInt(), (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt()),
                                    hsv
                                )
                                hue = hsv[0]
                                saturation = hsv[1]
                                value = hsv[2]
                                onSetCustomAccent(hex)
                            }
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Direct Hex Input + Action Row
            Text(
                text = "CUSTOM HEX CODE INPUT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = hexInputText,
                    onValueChange = { input ->
                        val cleaned = if (input.startsWith("#")) input else "#$input"
                        hexInputText = cleaned
                        val color = parseHexColor(cleaned)
                        if (color != null) {
                            val hsv = FloatArray(3)
                            android.graphics.Color.colorToHSV(
                                android.graphics.Color.argb((color.alpha * 255).toInt(), (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt()),
                                hsv
                            )
                            hue = hsv[0]
                            saturation = hsv[1]
                            value = hsv[2]
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("HEX Code", fontSize = 11.sp) },
                    placeholder = { Text("#4F46E5", fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(parsedInputColor ?: Color.Gray)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (hexInputText.isNotEmpty()) {
                                IconButton(
                                    onClick = { hexInputText = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                                }
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(hexInputText))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy HEX", modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (isHexValid) {
                            val cleanHex = if (hexInputText.startsWith("#")) hexInputText else "#$hexInputText"
                            onSetCustomAccent(cleanHex)
                        }
                    },
                    enabled = isHexValid,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Apply", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            if (!isHexValid && hexInputText.isNotEmpty()) {
                Text(
                    text = "Enter a valid 6-character hex code (e.g., #4F46E5)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Interactive Color Picker Slider Expansion Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isSlidersExpanded = !isSlidersExpanded },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(currentColorFromSliders)
                                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                        )
                        Text(
                            text = if (isSlidersExpanded) "Hide Color Sliders" else "Interactive Color Picker (Hue / Saturation)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = computedSliderHex,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Expanded Sliders
            AnimatedVisibility(visible = isSlidersExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hue Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hue (${hue.toInt()}°)", style = MaterialTheme.typography.labelSmall)
                        }
                        Slider(
                            value = hue,
                            onValueChange = { newHue ->
                                hue = newHue
                                val hex = hsvToHex(hue, saturation, value)
                                hexInputText = hex
                                onSetCustomAccent(hex)
                            },
                            valueRange = 0f..360f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Saturation Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saturation (${(saturation * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall)
                        }
                        Slider(
                            value = saturation,
                            onValueChange = { newSat ->
                                saturation = newSat
                                val hex = hsvToHex(hue, saturation, value)
                                hexInputText = hex
                                onSetCustomAccent(hex)
                            },
                            valueRange = 0.1f..1f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Brightness / Value Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Brightness (${(value * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall)
                        }
                        Slider(
                            value = value,
                            onValueChange = { newVal ->
                                value = newVal
                                val hex = hsvToHex(hue, saturation, value)
                                hexInputText = hex
                                onSetCustomAccent(hex)
                            },
                            valueRange = 0.2f..1f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Reset to default button if custom accent is set
            if (currentAccentHex != null) {
                OutlinedButton(
                    onClick = {
                        onSetCustomAccent(null)
                        hexInputText = "#4F46E5"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset to Theme Default Accent", fontSize = 11.sp)
                }
            }
        }
    }
}

private fun hsvToHex(h: Float, s: Float, v: Float): String {
    val colorInt = android.graphics.Color.HSVToColor(floatArrayOf(h, s, v))
    return String.format("#%06X", 0xFFFFFF and colorInt)
}
