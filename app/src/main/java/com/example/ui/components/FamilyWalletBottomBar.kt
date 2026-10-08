package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NavigationTab

@Composable
fun FamilyWalletBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                tab = NavigationTab.DASHBOARD,
                selected = currentTab == NavigationTab.DASHBOARD,
                icon = Icons.Outlined.Dashboard,
                selectedIcon = Icons.Filled.Dashboard,
                label = "Dashboard",
                onClick = { onTabSelected(NavigationTab.DASHBOARD) }
            )

            BottomNavItem(
                tab = NavigationTab.CARDS,
                selected = currentTab == NavigationTab.CARDS,
                icon = Icons.Outlined.CreditCard,
                selectedIcon = Icons.Filled.CreditCard,
                label = "Cards",
                onClick = { onTabSelected(NavigationTab.CARDS) }
            )

            BottomNavItem(
                tab = NavigationTab.ACCOUNTS,
                selected = currentTab == NavigationTab.ACCOUNTS,
                icon = Icons.Outlined.AccountBalance,
                selectedIcon = Icons.Filled.AccountBalance,
                label = "Banks",
                onClick = { onTabSelected(NavigationTab.ACCOUNTS) }
            )

            BottomNavItem(
                tab = NavigationTab.WALLETS,
                selected = currentTab == NavigationTab.WALLETS,
                icon = Icons.Outlined.AccountBalanceWallet,
                selectedIcon = Icons.Outlined.AccountBalanceWallet,
                label = "Wallets",
                onClick = { onTabSelected(NavigationTab.WALLETS) }
            )

            BottomNavItem(
                tab = NavigationTab.DOCUMENTS,
                selected = currentTab == NavigationTab.DOCUMENTS,
                icon = Icons.Outlined.Description,
                selectedIcon = Icons.Filled.Description,
                label = "Docs",
                onClick = { onTabSelected(NavigationTab.DOCUMENTS) }
            )

            BottomNavItem(
                tab = NavigationTab.MEMBERS,
                selected = currentTab == NavigationTab.MEMBERS,
                icon = Icons.Outlined.People,
                selectedIcon = Icons.Filled.People,
                label = "Family",
                onClick = { onTabSelected(NavigationTab.MEMBERS) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: NavigationTab,
    selected: Boolean,
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "nav_container_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "nav_content_color"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("nav_tab_${label.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(containerColor)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = contentColor
        )
    }
}
