package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.Face4
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DisplayMode
import com.example.data.FamilyMember
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.IndigoAccent
import com.example.ui.viewmodel.FamilyWalletUiState

fun getRelationshipCategoryVector(categoryName: String): ImageVector {
    return when (categoryName.uppercase()) {
        "SELF" -> Icons.Default.Person
        "SPOUSE" -> Icons.Default.Favorite
        "MOTHER" -> Icons.Default.Face3
        "FATHER" -> Icons.Default.SupervisorAccount
        "BROTHER" -> Icons.Default.Person
        "SISTER" -> Icons.Default.Face4
        "OTHERS", "OTHER" -> Icons.Outlined.AccountCircle
        "MEN" -> Icons.Default.Person
        "WOMEN" -> Icons.Default.Face3
        "CHILD" -> Icons.Default.ChildCare
        "PARENTS" -> Icons.Default.SupervisorAccount
        "SIBLINGS" -> Icons.Default.Diversity3
        else -> Icons.Default.Person
    }
}

@Composable
fun FamilyMembersScreen(
    uiState: FamilyWalletUiState,
    onSelectMember: (String?) -> Unit,
    onOpenAddMember: () -> Unit,
    onOpenEditMember: (FamilyMember) -> Unit,
    onDeleteMember: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var memberToDelete by remember { mutableStateOf<FamilyMember?>(null) }
    var expandedMemberId by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("family_members_screen")
        ) {
            // Header with Add Member Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FAMILY PROFILES (${uiState.members.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Manage and filter vault assets by family member",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = onOpenAddMember,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_member_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Member", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            if (uiState.members.isEmpty()) {
                EmptyFamilyView(onAddMember = onOpenAddMember)
            } else {
                when (uiState.displayMode) {
                    // 1. CAROUSEL VIEW (Triggered when Carousel mode is active)
                    DisplayMode.CAROUSEL -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Swipe to select & view profile breakdown",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    // "All Family" Carousel Card
                                    item {
                                        val isAllSelected = uiState.selectedMemberId == null
                                        val scale by animateFloatAsState(
                                            targetValue = if (isAllSelected) 1.02f else 1.0f,
                                            animationSpec = tween(200),
                                            label = "scale_all"
                                        )

                                        OutlinedCard(
                                            modifier = Modifier
                                                .width(220.dp)
                                                .scale(scale)
                                                .clickable { onSelectMember(null) }
                                                .testTag("member_carousel_all"),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isAllSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isAllSelected) 2.dp else 1.dp,
                                                color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(46.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.primary),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.People,
                                                            contentDescription = "All Members",
                                                            tint = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }

                                                    if (isAllSelected) {
                                                        Surface(
                                                            shape = RoundedCornerShape(6.dp),
                                                            color = MaterialTheme.colorScheme.primary
                                                        ) {
                                                            Text(
                                                                text = "Active",
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                                color = MaterialTheme.colorScheme.onPrimary
                                                            )
                                                        }
                                                    }
                                                }

                                                Column {
                                                    Text(
                                                        text = "All Family Vault",
                                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = "${uiState.members.size} Members Total",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    MiniStatBadge(label = "Cards", count = uiState.creditCards.size + uiState.debitCards.size, color = IndigoAccent)
                                                    MiniStatBadge(label = "Banks", count = uiState.bankAccounts.size, color = CyanAccent)
                                                    MiniStatBadge(label = "Wallets", count = uiState.walletsAndGiftCards.size, color = AmberGold)
                                                }
                                            }
                                        }
                                    }

                                    // Individual Member Carousel Cards
                                    items(uiState.members, key = { "carousel_${it.id}" }) { member ->
                                        val isSelected = uiState.selectedMemberId == member.id
                                        val memberCredits = uiState.creditCards.filter { it.memberId == member.id }
                                        val memberDebits = uiState.debitCards.filter { it.memberId == member.id }
                                        val memberBanks = uiState.bankAccounts.filter { it.memberId == member.id }
                                        val memberWalletsAndGifts = uiState.walletsAndGiftCards.filter { it.memberId == member.id }
                                        val totalCards = memberCredits.size + memberDebits.size

                                        val scale by animateFloatAsState(
                                            targetValue = if (isSelected) 1.02f else 1.0f,
                                            animationSpec = tween(200),
                                            label = "scale_${member.id}"
                                        )

                                        OutlinedCard(
                                            modifier = Modifier
                                                .width(220.dp)
                                                .scale(scale)
                                                .clickable {
                                                    onSelectMember(if (isSelected) null else member.id)
                                                }
                                                .testTag("member_carousel_${member.id}"),
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(member.colorHex).copy(alpha = 0.5f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(46.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(member.colorHex).copy(alpha = 0.15f))
                                                            .border(2.dp, Color(member.colorHex), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (!member.profilePictureUri.isNullOrBlank()) {
                                                            AsyncImage(
                                                                model = com.example.util.ImageModelResolver.resolve(member.profilePictureUri),
                                                                contentDescription = member.name,
                                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                        } else {
                                                            Icon(
                                                                imageVector = getRelationshipCategoryVector(member.relationshipCategory),
                                                                contentDescription = member.relationship,
                                                                tint = Color(member.colorHex),
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                        }
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        if (isSelected) {
                                                            Surface(
                                                                shape = RoundedCornerShape(6.dp),
                                                                color = MaterialTheme.colorScheme.primary
                                                            ) {
                                                                Text(
                                                                    text = "Active",
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                                    color = MaterialTheme.colorScheme.onPrimary
                                                                )
                                                            }
                                                        }
                                                        IconButton(
                                                            onClick = { onOpenEditMember(member) },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                                        }
                                                    }
                                                }

                                                Column {
                                                    Text(
                                                        text = member.name,
                                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1
                                                    )
                                                    Text(
                                                        text = member.relationship,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                        color = Color(member.colorHex)
                                                    )
                                                }

                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    MiniStatBadge(label = "Cards", count = totalCards, color = IndigoAccent)
                                                    MiniStatBadge(label = "Banks", count = memberBanks.size, color = CyanAccent)
                                                    MiniStatBadge(label = "Wallets", count = memberWalletsAndGifts.size, color = AmberGold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Selected Member or All Members Breakdown Card
                            item {
                                val activeMember = uiState.selectedMember
                                if (activeMember != null) {
                                    val memberCredits = uiState.creditCards.filter { it.memberId == activeMember.id }
                                    val memberDebits = uiState.debitCards.filter { it.memberId == activeMember.id }
                                    val memberBanks = uiState.bankAccounts.filter { it.memberId == activeMember.id }
                                    val memberWalletsAndGifts = uiState.walletsAndGiftCards.filter { it.memberId == activeMember.id }
                                    val totalAssigned = memberCredits.size + memberDebits.size + memberBanks.size + memberWalletsAndGifts.size

                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            Text(
                                                text = "${activeMember.name}'s Vault Asset Breakdown",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            if (totalAssigned > 0) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier.size(90.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        MemberAssetDonutChart(
                                                            creditCount = memberCredits.size,
                                                            debitCount = memberDebits.size,
                                                            bankCount = memberBanks.size,
                                                            walletCount = memberWalletsAndGifts.size,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                        Text(
                                                            text = "$totalAssigned",
                                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }

                                                    Column(
                                                        modifier = Modifier.weight(1f),
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        DrillDownRow(label = "Credit Cards", count = memberCredits.size, color = IndigoAccent)
                                                        DrillDownRow(label = "Debit Cards", count = memberDebits.size, color = EmeraldMint)
                                                        DrillDownRow(label = "Bank Accounts", count = memberBanks.size, color = CyanAccent)
                                                        DrillDownRow(label = "Wallets & Gifts", count = memberWalletsAndGifts.size, color = AmberGold)
                                                    }
                                                }
                                            } else {
                                                Text(
                                                    text = "No assets linked to ${activeMember.name} yet.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Button(
                                                onClick = { onSelectMember(null) },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Show All Family Assets")
                                            }
                                        }
                                    }
                                } else {
                                    OutlinedCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = "All Family Members Summary",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Select any member above to filter the entire financial vault or view individual asset distribution.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. GRID VIEW (Triggered when Grid mode is active)
                    DisplayMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 88.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.filteredMembers, key = { "grid_${it.id}" }) { member ->
                                val isSelected = uiState.selectedMemberId == member.id
                                val memberCredits = uiState.creditCards.filter { it.memberId == member.id }
                                val memberDebits = uiState.debitCards.filter { it.memberId == member.id }
                                val memberBanks = uiState.bankAccounts.filter { it.memberId == member.id }
                                val memberWalletsAndGifts = uiState.walletsAndGiftCards.filter { it.memberId == member.id }
                                val totalCards = memberCredits.size + memberDebits.size

                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectMember(if (isSelected) null else member.id) }
                                        .testTag("member_grid_${member.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(member.colorHex).copy(alpha = 0.15f))
                                                    .border(1.5.dp, Color(member.colorHex), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (!member.profilePictureUri.isNullOrBlank()) {
                                                    AsyncImage(
                                                        model = com.example.util.ImageModelResolver.resolve(member.profilePictureUri),
                                                        contentDescription = member.name,
                                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = getRelationshipCategoryVector(member.relationshipCategory),
                                                        contentDescription = member.relationship,
                                                        tint = Color(member.colorHex),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { onOpenEditMember(member) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = member.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = member.relationship,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(member.colorHex)
                                            )
                                        }

                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "💳 $totalCards",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = IndigoAccent
                                            )
                                            Text(
                                                text = "🏦 ${memberBanks.size}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = CyanAccent
                                            )
                                            Text(
                                                text = "🎁 ${memberWalletsAndGifts.size}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = AmberGold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. LIST VIEW (Triggered when List mode is active)
                    DisplayMode.LIST -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(uiState.filteredMembers, key = { "list_${it.id}" }) { member ->
                                val isSelected = uiState.selectedMemberId == member.id
                                val isExpanded = expandedMemberId == member.id

                                val memberCredits = uiState.creditCards.filter { it.memberId == member.id }
                                val memberDebits = uiState.debitCards.filter { it.memberId == member.id }
                                val memberBanks = uiState.bankAccounts.filter { it.memberId == member.id }
                                val memberWalletsAndGifts = uiState.walletsAndGiftCards.filter { it.memberId == member.id }
                                val totalAssigned = memberCredits.size + memberDebits.size + memberBanks.size + memberWalletsAndGifts.size

                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedMemberId = if (isExpanded) null else member.id }
                                        .testTag("member_card_${member.id}"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
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
                                                        .size(48.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(member.colorHex).copy(alpha = 0.15f))
                                                        .border(1.5.dp, Color(member.colorHex), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (!member.profilePictureUri.isNullOrBlank()) {
                                                        AsyncImage(
                                                            model = com.example.util.ImageModelResolver.resolve(member.profilePictureUri),
                                                            contentDescription = member.name,
                                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    } else {
                                                        Icon(
                                                            imageVector = getRelationshipCategoryVector(member.relationshipCategory),
                                                            contentDescription = member.relationship,
                                                            tint = Color(member.colorHex),
                                                            modifier = Modifier.size(26.dp)
                                                        )
                                                    }
                                                }

                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = member.name,
                                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        if (isSelected) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Surface(
                                                                shape = RoundedCornerShape(6.dp),
                                                                color = MaterialTheme.colorScheme.primary
                                                            ) {
                                                                Text(
                                                                    text = "Active Filter",
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                                    color = MaterialTheme.colorScheme.onPrimary
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Text(
                                                        text = member.relationship,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { onOpenEditMember(member) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                                }

                                                IconButton(
                                                    onClick = { memberToDelete = member },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Profile", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                }

                                                Icon(
                                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                    contentDescription = "Expand",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AssetCountBadge(label = "Cards", count = memberCredits.size + memberDebits.size, color = IndigoAccent)
                                            AssetCountBadge(label = "Banks", count = memberBanks.size, color = CyanAccent)
                                            AssetCountBadge(label = "Wallets/Gifts", count = memberWalletsAndGifts.size, color = AmberGold)
                                        }

                                        AnimatedVisibility(
                                            visible = isExpanded,
                                            enter = expandVertically() + fadeIn(),
                                            exit = shrinkVertically() + fadeOut()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp),
                                                verticalArrangement = Arrangement.spacedBy(14.dp)
                                            ) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                                Text(
                                                    text = "${member.name}'s Vault Distribution",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                if (totalAssigned > 0) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier.size(90.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            MemberAssetDonutChart(
                                                                creditCount = memberCredits.size,
                                                                debitCount = memberDebits.size,
                                                                bankCount = memberBanks.size,
                                                                walletCount = memberWalletsAndGifts.size,
                                                                modifier = Modifier.fillMaxSize()
                                                            )
                                                            Text(
                                                                text = "$totalAssigned",
                                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                                color = MaterialTheme.colorScheme.onSurface
                                                            )
                                                        }

                                                        Column(
                                                            modifier = Modifier.weight(1f),
                                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            DrillDownRow(label = "Credit Cards", count = memberCredits.size, color = IndigoAccent)
                                                            DrillDownRow(label = "Debit Cards", count = memberDebits.size, color = EmeraldMint)
                                                            DrillDownRow(label = "Bank Accounts", count = memberBanks.size, color = CyanAccent)
                                                            DrillDownRow(label = "Wallets & Gifts", count = memberWalletsAndGifts.size, color = AmberGold)
                                                        }
                                                    }
                                                }

                                                Button(
                                                    onClick = { onSelectMember(if (isSelected) null else member.id) },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(if (isSelected) "Clear Member Filter" else "Filter Entire Vault by ${member.name}")
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
        }
    }

    // Delete Member Confirmation Dialog
    memberToDelete?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            title = { Text("Delete ${member.name}'s Profile?") },
            text = {
                Text("This will remove this family profile. Any linked cards or bank accounts will safely remain in the vault as unassigned.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMember(member.id)
                        memberToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MiniStatBadge(label: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(color))
            Text(
                text = "$count $label",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = color
            )
        }
    }
}

@Composable
private fun AssetCountBadge(label: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Text(
                text = "$count $label",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun DrillDownRow(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(text = "$count", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
    }
}

@Composable
private fun MemberAssetDonutChart(
    creditCount: Int,
    debitCount: Int,
    bankCount: Int,
    walletCount: Int,
    modifier: Modifier = Modifier
) {
    val total = creditCount + debitCount + bankCount + walletCount

    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
        val arcSize = Size(diameter, diameter)

        if (total == 0) return@Canvas

        var startAngle = -90f
        val slices = listOf(
            Pair(creditCount, IndigoAccent),
            Pair(debitCount, EmeraldMint),
            Pair(bankCount, CyanAccent),
            Pair(walletCount, AmberGold)
        )

        for ((count, color) in slices) {
            if (count > 0) {
                val sweep = (count.toFloat() / total) * 360f
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep - 2f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweep
            }
        }
    }
}

@Composable
private fun EmptyFamilyView(onAddMember: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Family Profiles Added",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Add profiles for your parents, spouse, siblings, or children to categorize cards, banks, and wallets with drill-down charts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(onClick = onAddMember, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add First Family Member")
            }
        }
    }
}
