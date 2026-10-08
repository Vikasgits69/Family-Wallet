package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.abs
import kotlinx.coroutines.launch
import com.example.data.CardNetwork
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.data.DisplayMode
import com.example.data.NavigationTab
import com.example.ui.components.InteractiveCreditCardItem
import com.example.ui.components.InteractiveDebitCardItem
import com.example.ui.viewmodel.FamilyWalletUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    uiState: FamilyWalletUiState,
    onOpenAddCreditCard: () -> Unit,
    onOpenAddDebitCard: () -> Unit,
    onOpenEditCreditCard: (CreditCard) -> Unit,
    onOpenEditDebitCard: (DebitCard) -> Unit,
    onToggleCardFlip: (String) -> Unit,
    onToggleItemMask: (String) -> Unit,
    onDeleteCreditCard: (String) -> Unit,
    onDeleteDebitCard: (String) -> Unit,
    onToggleBillPaid: (String) -> Unit = {},
    onOpenHelpline: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("All Cards", "Credit", "Debit", "RuPay")

    var cardToDelete by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // (id, isCredit)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Immediate zero-latency rendering
    val isLoaded = true

    // Cards Content
    val creditList by remember(uiState.filteredCreditCards, selectedTabIndex) {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> uiState.filteredCreditCards
                1 -> uiState.filteredCreditCards
                2 -> emptyList()
                3 -> uiState.filteredCreditCards.filter { it.network == CardNetwork.RUPAY }
                else -> uiState.filteredCreditCards
            }
        }
    }

    val debitList by remember(uiState.filteredDebitCards, selectedTabIndex) {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> uiState.filteredDebitCards
                1 -> emptyList()
                2 -> uiState.filteredDebitCards
                3 -> uiState.filteredDebitCards.filter { it.network == CardNetwork.RUPAY }
                else -> uiState.filteredDebitCards
            }
        }
    }

    val isVaultEmpty by remember(creditList, debitList) {
        derivedStateOf { creditList.isEmpty() && debitList.isEmpty() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Selector
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            if (isVaultEmpty) {
                EmptyCardsView(
                    onAddCredit = onOpenAddCreditCard,
                    onAddDebit = onOpenAddDebitCard
                )
            } else {
                when (uiState.getDisplayModeForTab(NavigationTab.CARDS)) {
                    DisplayMode.CAROUSEL -> {
                        // Material 3 HorizontalPager Carousel View with Smooth 3D Flip, Page Interpolation, & Navigation Controls
                        val density = androidx.compose.ui.platform.LocalDensity.current.density

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            if (creditList.isNotEmpty()) {
                                item {
                                    val creditPagerState = rememberPagerState(pageCount = { creditList.size })

                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        // Header Row with Title and Smooth Prev/Next Controls
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "CREDIT CARDS (${creditList.size})",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 1.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "Swipe to switch • Tap card to Flip 3D",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Smooth Page Switcher Buttons
                                            if (creditList.size > 1) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            if (creditPagerState.currentPage > 0) {
                                                                coroutineScope.launch {
                                                                    creditPagerState.animateScrollToPage(creditPagerState.currentPage - 1)
                                                                }
                                                            }
                                                        },
                                                        enabled = creditPagerState.currentPage > 0,
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ChevronLeft,
                                                            contentDescription = "Previous Credit Card",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = if (creditPagerState.currentPage > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                                    ) {
                                                        Text(
                                                            text = "${creditPagerState.currentPage + 1} / ${creditList.size}",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            if (creditPagerState.currentPage < creditList.size - 1) {
                                                                coroutineScope.launch {
                                                                    creditPagerState.animateScrollToPage(creditPagerState.currentPage + 1)
                                                                }
                                                            }
                                                        },
                                                        enabled = creditPagerState.currentPage < creditList.size - 1,
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ChevronRight,
                                                            contentDescription = "Next Credit Card",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = if (creditPagerState.currentPage < creditList.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Horizontal Pager with Smooth Page Transformation
                                        HorizontalPager(
                                            state = creditPagerState,
                                            contentPadding = PaddingValues(horizontal = 40.dp),
                                            pageSpacing = 16.dp,
                                            beyondViewportPageCount = 1,
                                            modifier = Modifier.fillMaxWidth()
                                        ) { page ->
                                            val card = creditList[page]
                                            val member = uiState.members.find { it.id == card.memberId }

                                            // Smooth carousel animation offset
                                            val pageOffset = ((creditPagerState.currentPage - page) + creditPagerState.currentPageOffsetFraction)
                                            val absOffset = kotlin.math.abs(pageOffset).coerceIn(0f, 1f)
                                            val scale = lerp(0.92f, 1.0f, 1f - absOffset)
                                            val alpha = lerp(0.65f, 1.0f, 1f - absOffset)
                                            val rotationY = (pageOffset * -6f).coerceIn(-12f, 12f)

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .graphicsLayer {
                                                        scaleX = scale
                                                        scaleY = scale
                                                        this.alpha = alpha
                                                        this.rotationY = rotationY
                                                        cameraDistance = 14f * density
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                InteractiveCreditCardItem(
                                                    card = card,
                                                    isFlipped = uiState.isCardFlipped(card.id),
                                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                                    onFlip = { onToggleCardFlip(card.id) },
                                                    onToggleMask = { onToggleItemMask(card.id) },
                                                    memberName = member?.name,
                                                    onEdit = { onOpenEditCreditCard(card) },
                                                    onDelete = { cardToDelete = Pair(card.id, true) },
                                                    onToggleBillPaid = { onToggleBillPaid(card.id) },
                                                    onOpenHelpline = { onOpenHelpline(card.bankName) },
                                                    modifier = Modifier.widthIn(max = 340.dp)
                                                )
                                            }
                                        }

                                        // Interactive Page Indicator Dots/Pills
                                        if (creditList.size > 1) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                repeat(creditList.size) { index ->
                                                    val isSelected = creditPagerState.currentPage == index
                                                    val dotWidth by animateDpAsState(
                                                        targetValue = if (isSelected) 20.dp else 6.dp,
                                                        label = "credit_dot_anim_$index"
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(horizontal = 3.dp)
                                                            .height(6.dp)
                                                            .width(dotWidth)
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                                                            )
                                                            .clickable {
                                                                coroutineScope.launch {
                                                                    creditPagerState.animateScrollToPage(index)
                                                                }
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (debitList.isNotEmpty()) {
                                item {
                                    val debitPagerState = rememberPagerState(pageCount = { debitList.size })

                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        // Header Row with Title and Smooth Prev/Next Controls
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "DEBIT CARDS (${debitList.size})",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 1.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "Swipe to switch • Tap card to Flip 3D",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Smooth Page Switcher Buttons
                                            if (debitList.size > 1) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            if (debitPagerState.currentPage > 0) {
                                                                coroutineScope.launch {
                                                                    debitPagerState.animateScrollToPage(debitPagerState.currentPage - 1)
                                                                }
                                                            }
                                                        },
                                                        enabled = debitPagerState.currentPage > 0,
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ChevronLeft,
                                                            contentDescription = "Previous Debit Card",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = if (debitPagerState.currentPage > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                                    ) {
                                                        Text(
                                                            text = "${debitPagerState.currentPage + 1} / ${debitList.size}",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                        )
                                                    }

                                                    IconButton(
                                                        onClick = {
                                                            if (debitPagerState.currentPage < debitList.size - 1) {
                                                                coroutineScope.launch {
                                                                    debitPagerState.animateScrollToPage(debitPagerState.currentPage + 1)
                                                                }
                                                            }
                                                        },
                                                        enabled = debitPagerState.currentPage < debitList.size - 1,
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ChevronRight,
                                                            contentDescription = "Next Debit Card",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = if (debitPagerState.currentPage < debitList.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Horizontal Pager with Smooth Page Transformation
                                        HorizontalPager(
                                            state = debitPagerState,
                                            contentPadding = PaddingValues(horizontal = 40.dp),
                                            pageSpacing = 16.dp,
                                            beyondViewportPageCount = 1,
                                            modifier = Modifier.fillMaxWidth()
                                        ) { page ->
                                            val card = debitList[page]
                                            val member = uiState.members.find { it.id == card.memberId }

                                            // Smooth carousel animation offset
                                            val pageOffset = ((debitPagerState.currentPage - page) + debitPagerState.currentPageOffsetFraction)
                                            val absOffset = kotlin.math.abs(pageOffset).coerceIn(0f, 1f)
                                            val scale = lerp(0.92f, 1.0f, 1f - absOffset)
                                            val alpha = lerp(0.65f, 1.0f, 1f - absOffset)
                                            val rotationY = (pageOffset * -6f).coerceIn(-12f, 12f)

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .graphicsLayer {
                                                        scaleX = scale
                                                        scaleY = scale
                                                        this.alpha = alpha
                                                        this.rotationY = rotationY
                                                        cameraDistance = 14f * density
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                InteractiveDebitCardItem(
                                                    card = card,
                                                    isFlipped = uiState.isCardFlipped(card.id),
                                                    isUnmasked = uiState.isItemUnmasked(card.id),
                                                    onFlip = { onToggleCardFlip(card.id) },
                                                    onToggleMask = { onToggleItemMask(card.id) },
                                                    memberName = member?.name,
                                                    onEdit = { onOpenEditDebitCard(card) },
                                                    onDelete = { cardToDelete = Pair(card.id, false) },
                                                    onOpenHelpline = { onOpenHelpline(card.bankName) },
                                                    modifier = Modifier.widthIn(max = 340.dp)
                                                )
                                            }
                                        }

                                        // Interactive Page Indicator Dots/Pills
                                        if (debitList.size > 1) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                repeat(debitList.size) { index ->
                                                    val isSelected = debitPagerState.currentPage == index
                                                    val dotWidth by animateDpAsState(
                                                        targetValue = if (isSelected) 20.dp else 6.dp,
                                                        label = "debit_dot_anim_$index"
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(horizontal = 3.dp)
                                                            .height(6.dp)
                                                            .width(dotWidth)
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                                                            )
                                                            .clickable {
                                                                coroutineScope.launch {
                                                                    debitPagerState.animateScrollToPage(index)
                                                                }
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    DisplayMode.GRID -> {
                        // Grid View
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 280.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(creditList, key = { it.id }) { card ->
                                val member = uiState.members.find { it.id == card.memberId }
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    InteractiveCreditCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name,
                                        onEdit = { onOpenEditCreditCard(card) },
                                        onDelete = { cardToDelete = Pair(card.id, true) },
                                        onToggleBillPaid = { onToggleBillPaid(card.id) },
                                        onOpenHelpline = { onOpenHelpline(card.bankName) }
                                    )
                                }
                            }
                            items(debitList, key = { it.id }) { card ->
                                val member = uiState.members.find { it.id == card.memberId }
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    InteractiveDebitCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name,
                                        onEdit = { onOpenEditDebitCard(card) },
                                        onDelete = { cardToDelete = Pair(card.id, false) },
                                        onOpenHelpline = { onOpenHelpline(card.bankName) }
                                    )
                                }
                            }
                        }
                    }

                    DisplayMode.LIST -> {
                        // List View: Full vertical layout with centered max-width cards
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (creditList.isNotEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().widthIn(max = 380.dp)) {
                                        Text(
                                            text = "CREDIT CARDS (${creditList.size})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                items(creditList, key = { it.id }) { card ->
                                    val member = uiState.members.find { it.id == card.memberId }
                                    InteractiveCreditCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name,
                                        onEdit = { onOpenEditCreditCard(card) },
                                        onDelete = { cardToDelete = Pair(card.id, true) },
                                        onToggleBillPaid = { onToggleBillPaid(card.id) },
                                        onOpenHelpline = { onOpenHelpline(card.bankName) },
                                        modifier = Modifier.widthIn(max = 360.dp)
                                    )
                                }
                            }

                            if (debitList.isNotEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().widthIn(max = 380.dp)) {
                                        Column {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "DEBIT CARDS (${debitList.size})",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                items(debitList, key = { it.id }) { card ->
                                    val member = uiState.members.find { it.id == card.memberId }
                                    InteractiveDebitCardItem(
                                        card = card,
                                        isFlipped = uiState.isCardFlipped(card.id),
                                        isUnmasked = uiState.isItemUnmasked(card.id),
                                        onFlip = { onToggleCardFlip(card.id) },
                                        onToggleMask = { onToggleItemMask(card.id) },
                                        memberName = member?.name,
                                        onEdit = { onOpenEditDebitCard(card) },
                                        onDelete = { cardToDelete = Pair(card.id, false) },
                                        onOpenHelpline = { onOpenHelpline(card.bankName) },
                                        modifier = Modifier.widthIn(max = 360.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Add Card Floating Action Button
        if (!isVaultEmpty) {
            var showAddOptions by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showAddOptions) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            showAddOptions = false
                            onOpenAddCreditCard()
                        },
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                        text = { Text("+ Credit Card", fontWeight = FontWeight.Bold) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    ExtendedFloatingActionButton(
                        onClick = {
                            showAddOptions = false
                            onOpenAddDebitCard()
                        },
                        icon = { Icon(Icons.Outlined.Payments, contentDescription = null) },
                        text = { Text("+ Debit Card", fontWeight = FontWeight.Bold) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                FloatingActionButton(
                    onClick = {
                        when (selectedTabIndex) {
                            1 -> onOpenAddCreditCard()
                            2 -> onOpenAddDebitCard()
                            else -> showAddOptions = !showAddOptions
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_card_fab")
                ) {
                    Icon(
                        imageVector = if (showAddOptions) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Card"
                    )
                }
            }
        }
    }

    // Delete Card Confirmation Dialog
    cardToDelete?.let { (id, isCredit) ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text(if (isCredit) "Delete Credit Card?" else "Delete Debit Card?") },
            text = { Text("Are you sure you want to delete this card from the vault? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (isCredit) onDeleteCreditCard(id) else onDeleteDebitCard(id)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyCardsView(
    onAddCredit: () -> Unit,
    onAddDebit: () -> Unit
) {
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
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "No Cards in Vault",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Securely vault RuPay, Visa, Mastercard, or Amex credit and debit cards with 3D flip interactive previews and reminder alerts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAddCredit,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("add_credit_card_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Credit Card",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                FilledTonalButton(
                    onClick = onAddDebit,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("add_debit_card_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Payments,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Debit Card",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
