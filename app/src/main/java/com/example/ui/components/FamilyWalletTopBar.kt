package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AppThemeMode
import com.example.data.DisplayMode
import com.example.data.FamilyMember
import com.example.data.NavigationTab
import com.example.ui.viewmodel.FamilyWalletUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FamilyWalletTopBar(
    uiState: FamilyWalletUiState,
    onSelectMember: (String?) -> Unit,
    onToggleGlobalMask: () -> Unit,
    onToggleTheme: () -> Unit,
    onLockApp: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSetDisplayMode: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
    onSetThemeMode: ((AppThemeMode) -> Unit)? = null,
    onSetCustomAccent: ((String?) -> Unit)? = null,
    onNavigateTab: (NavigationTab) -> Unit = {}
) {
    var memberDropdownExpanded by remember { mutableStateOf(false) }
    var themeDropdownExpanded by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("family_wallet_top_bar"),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Main Top Bar Row: App Brand & Quick Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Branding (Google Wallet Styled)
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Family Wallet Logo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Family Wallet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "Financial Information Vault",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Quick Action Icons: Search, Global Mask Eye, Theme Dropdown, Settings, Lock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Global Search Toggle
                    IconButton(
                        onClick = { isSearchExpanded = !isSearchExpanded },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("search_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Vault",
                            tint = if (isSearchExpanded || uiState.searchQuery.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Global Mask / Unmask Toggle Eye
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleGlobalMask()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("global_mask_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isMaskedGlobally) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Data Masking",
                            tint = if (uiState.isMaskedGlobally) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Compact Theme Switcher Button with Dropdown
                    Box {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (onSetThemeMode != null) {
                                    themeDropdownExpanded = true
                                } else {
                                    onToggleTheme()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Switch Theme",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = themeDropdownExpanded,
                            onDismissRequest = { themeDropdownExpanded = false }
                        ) {
                            AppThemeMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(mode.emoji)
                                            Text(mode.title, fontWeight = FontWeight.SemiBold)
                                        }
                                    },
                                    onClick = {
                                        onSetThemeMode?.invoke(mode) ?: onToggleTheme()
                                        themeDropdownExpanded = false
                                    },
                                    trailingIcon = {
                                        if (uiState.themeMode == mode && uiState.customAccentColorHex == null) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ColorLens,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Custom Accent Color...",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    themeDropdownExpanded = false
                                    showColorPickerDialog = true
                                },
                                trailingIcon = {
                                    if (uiState.customAccentColorHex != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // Lock Vault (placed before Settings)
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLockApp()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("lock_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings Button (at the end)
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateTab(NavigationTab.SETTINGS)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("settings_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Global Search Bar across ALL tables (Sleek & Compact)
            AnimatedVisibility(
                visible = isSearchExpanded || uiState.searchQuery.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search cards, accounts, wallets, members...",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                )
            }

            // Responsive Controls Bar: Member Filter & Functional View Toggles (Hidden on Settings)
            if (uiState.currentTab != com.example.data.NavigationTab.SETTINGS) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Member Filter Dropdown Selector
                    Box {
                        Surface(
                            onClick = { memberDropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.testTag("member_filter_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val selectedMember = uiState.selectedMember
                                if (selectedMember != null) {
                                    if (!selectedMember.profilePictureUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = com.example.util.ImageModelResolver.resolve(selectedMember.profilePictureUri),
                                            contentDescription = selectedMember.name,
                                            modifier = Modifier.size(20.dp).clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedMember.name,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(text = "👥", fontSize = 14.sp)
                                    Text(
                                        text = "All Vault Items (${uiState.members.size} Members)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = memberDropdownExpanded,
                            onDismissRequest = { memberDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("All Members (${uiState.members.size})", fontWeight = FontWeight.Bold)
                                },
                                onClick = {
                                    onSelectMember(null)
                                    memberDropdownExpanded = false
                                },
                                trailingIcon = {
                                    if (uiState.selectedMemberId == null) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )

                            uiState.members.forEach { member ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (!member.profilePictureUri.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = com.example.util.ImageModelResolver.resolve(member.profilePictureUri),
                                                    contentDescription = member.name,
                                                    modifier = Modifier.size(22.dp).clip(CircleShape)
                                                )
                                            } else {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            }
                                            Column {
                                                Text(member.name, fontWeight = FontWeight.Bold)
                                                Text(member.relationship, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    },
                                    onClick = {
                                        onSelectMember(member.id)
                                        memberDropdownExpanded = false
                                    },
                                    trailingIcon = {
                                        if (uiState.selectedMemberId == member.id) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // 2. Functional View Mode Toggles (Carousel, Grid, List) - Shown on supported sections
                    if (uiState.currentTab in setOf(
                            com.example.data.NavigationTab.CARDS,
                            com.example.data.NavigationTab.ACCOUNTS,
                            com.example.data.NavigationTab.WALLETS,
                            com.example.data.NavigationTab.DOCUMENTS,
                            com.example.data.NavigationTab.MEMBERS
                        )
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                ViewModeIconButton(
                                    icon = Icons.Outlined.ViewCarousel,
                                    isSelected = uiState.displayMode == DisplayMode.CAROUSEL,
                                    onClick = { onSetDisplayMode(DisplayMode.CAROUSEL) },
                                    contentDescription = "Carousel View"
                                )
                                ViewModeIconButton(
                                    icon = Icons.Outlined.GridView,
                                    isSelected = uiState.displayMode == DisplayMode.GRID,
                                    onClick = { onSetDisplayMode(DisplayMode.GRID) },
                                    contentDescription = "Grid View"
                                )
                                ViewModeIconButton(
                                    icon = Icons.Outlined.ViewList,
                                    isSelected = uiState.displayMode == DisplayMode.LIST,
                                    onClick = { onSetDisplayMode(DisplayMode.LIST) },
                                    contentDescription = "List View"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showColorPickerDialog) {
        CustomColorPickerDialog(
            initialColorHex = uiState.customAccentColorHex ?: "#4F46E5",
            title = "Choose Custom Accent Color",
            onColorSelected = { _, hex ->
                onSetCustomAccent?.invoke(hex)
            },
            onDismiss = { showColorPickerDialog = false }
        )
    }
}

@Composable
private fun ViewModeIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}
