package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.AppThemeMode
import com.example.data.CardNetwork
import com.example.data.DisplayMode
import com.example.data.FamilyMember
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
    onSetNetworkFilter: (CardNetwork?) -> Unit,
    modifier: Modifier = Modifier
) {
    var memberDropdownExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top Main Row: App Title/Branding & Primary Action Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Family Wallet Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Family Wallet",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Informational Vault • ₹ RuPay",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Quick Action Icons (Search, Global Mask Eye, Lock)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Search Button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isSearchExpanded = !isSearchExpanded
                            if (!isSearchExpanded) {
                                onSearchQueryChange("")
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("search_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search Vault",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Global Mask Toggle
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleGlobalMask()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("global_mask_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isMaskedGlobally) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Data Masking",
                            tint = if (uiState.isMaskedGlobally) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Theme Switcher Button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleTheme()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Switch Theme",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Lock Vault
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLockApp()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("lock_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Expandable Search Bar
            AnimatedVisibility(
                visible = isSearchExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search cards, accounts, IFSC, UPI ID...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Responsive Controls Bar: Uses FlowRow to adapt effortlessly to small and large screens
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                // 1. Member Filter Dropdown Selector
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .clickable { memberDropdownExpanded = true }
                            .testTag("member_filter_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val selectedMember = uiState.selectedMember
                            if (selectedMember != null) {
                                Text(
                                    text = selectedMember.avatarEmoji,
                                    fontSize = 14.sp
                                )
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

                    // Dropdown Menu Items
                    DropdownMenu(
                        expanded = memberDropdownExpanded,
                        onDismissRequest = { memberDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("👥  All Family Members (${uiState.members.size})", fontWeight = FontWeight.Bold) },
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
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(member.avatarEmoji)
                                        Column {
                                            Text(member.name, fontWeight = FontWeight.SemiBold)
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

                // 2. View Mode Toggles (Carousel, Grid, List)
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

            // Quick Network Filter Row (Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = uiState.networkFilter == null,
                    onClick = { onSetNetworkFilter(null) },
                    label = { Text("All Networks", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                FilterChip(
                    selected = uiState.networkFilter == CardNetwork.RUPAY,
                    onClick = { onSetNetworkFilter(if (uiState.networkFilter == CardNetwork.RUPAY) null else CardNetwork.RUPAY) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFF27922)))
                            Text("RuPay Vault", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0C2340),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = uiState.networkFilter == CardNetwork.VISA,
                    onClick = { onSetNetworkFilter(if (uiState.networkFilter == CardNetwork.VISA) null else CardNetwork.VISA) },
                    label = { Text("VISA", style = MaterialTheme.typography.labelSmall) }
                )

                FilterChip(
                    selected = uiState.networkFilter == CardNetwork.MASTERCARD,
                    onClick = { onSetNetworkFilter(if (uiState.networkFilter == CardNetwork.MASTERCARD) null else CardNetwork.MASTERCARD) },
                    label = { Text("Mastercard", style = MaterialTheme.typography.labelSmall) }
                )

                FilterChip(
                    selected = uiState.networkFilter == CardNetwork.AMEX,
                    onClick = { onSetNetworkFilter(if (uiState.networkFilter == CardNetwork.AMEX) null else CardNetwork.AMEX) },
                    label = { Text("Amex", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
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
            .size(32.dp)
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
