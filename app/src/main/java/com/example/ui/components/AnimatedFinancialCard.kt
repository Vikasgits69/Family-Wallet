package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Refresh
import androidx.fragment.app.FragmentActivity
import com.example.security.BiometricAuthManager
import com.example.util.SafeVaultShareManager
import com.example.util.VaultPreferencesManager
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import com.example.data.CardNetwork
import com.example.data.CreditCard
import com.example.data.DebitCard

/**
 * Format Expiry to always show as MM/YY instead of MMYY
 */
fun formatExpiryDisplay(raw: String): String {
    val clean = raw.trim()
    if (clean.contains("/")) return clean
    val digits = clean.filter { it.isDigit() }
    return when (digits.length) {
        4 -> "${digits.take(2)}/${digits.drop(2)}"
        3 -> "0${digits.take(1)}/${digits.drop(1)}"
        else -> if (clean.isBlank()) "MM/YY" else clean
    }
}

/**
 * Solid Block Colors with Google Wallet Style Depth & Vibrancy
 */
fun getCardBackgroundBrush(colorHex: Long): Brush {
    val base = Color(colorHex)
    return Brush.linearGradient(
        colors = listOf(
            base,
            base.copy(alpha = 0.88f),
            base
        )
    )
}

/**
 * 3D Flippable Credit Card Component with Google Wallet ExtraLarge Corners (28.dp)
 * Supports dragging horizontally (left-to-right or right-to-left) to reveal expanded details!
 */
@Composable
fun InteractiveCreditCardItem(
    card: CreditCard,
    isFlipped: Boolean,
    isUnmasked: Boolean,
    onFlip: () -> Unit,
    onToggleMask: () -> Unit,
    modifier: Modifier = Modifier,
    memberName: String? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onCopyNumber: (() -> Unit)? = null,
    onToggleBillPaid: (() -> Unit)? = null,
    onOpenHelpline: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "credit_card_flip"
    )

    val isFrontVisible = rotation <= 90f
    val brush = getCardBackgroundBrush(card.colorHex)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFlip()
                }
                .testTag("credit_card_${card.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(card.colorHex)),
            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            ) {
                if (isFrontVisible) {
                    CreditCardFrontView(
                        card = card,
                        isUnmasked = isUnmasked,
                        onToggleMask = onToggleMask,
                        memberName = memberName,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        onCopy = {
                            copyToClipboard(context, "Card Number", card.cardNumber)
                            onCopyNumber?.invoke()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                    ) {
                        CreditCardBackView(
                            card = card,
                            isUnmasked = isUnmasked,
                            onCopyCvv = {
                                copyToClipboard(context, "CVV", card.cvv)
                            }
                        )
                    }
                }
            }
        }

        // Expanded View revealing extra details
        CardExpandedDetailsView(
            isExpanded = isExpanded,
            onToggleExpand = { isExpanded = !isExpanded },
            linkedEmail = card.linkedEmail,
            linkedPhone = card.linkedPhone,
            issuanceDate = card.issuanceDate,
            rewardPoints = if (card.ccRewardPoints > 0) "${card.ccRewardPoints} pts" else null,
            statementDate = card.statementDate,
            dueDate = card.dueDate,
            remindExpiry = card.remindExpiry,
            remindBillDate = card.remindBillDate,
            remindDueDate = card.remindDueDate,
            memberName = memberName,
            colorHex = card.colorHex,
            isCredit = true,
            cardNumber = card.cardNumber,
            cvv = card.cvv,
            atmPin = card.atmPin,
            cardPin = card.cardPin,
            isBillPaid = card.isBillPaid,
            lastPaidDate = card.lastPaidDate,
            domesticPosLimit = card.domesticPosLimit,
            dailyAtmLimit = card.dailyAtmLimit,
            internationalUsage = card.internationalUsage,
            frontCardImagePath = card.frontCardImagePath,
            backCardImagePath = card.backCardImagePath,
            attachmentPaths = card.attachmentPaths,
            isUnmasked = isUnmasked,
            onCopy = { label, text -> copyToClipboard(context, label, text) },
            onToggleBillPaid = onToggleBillPaid,
            onOpenHelpline = onOpenHelpline
        )
    }
}

/**
 * 3D Flippable Debit Card Component with Google Wallet ExtraLarge Corners (28.dp)
 * Supports dragging horizontally (left-to-right or right-to-left) to reveal expanded details!
 */
@Composable
fun InteractiveDebitCardItem(
    card: DebitCard,
    isFlipped: Boolean,
    isUnmasked: Boolean,
    onFlip: () -> Unit,
    onToggleMask: () -> Unit,
    modifier: Modifier = Modifier,
    memberName: String? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onCopyNumber: (() -> Unit)? = null,
    onOpenHelpline: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "debit_card_flip"
    )

    val isFrontVisible = rotation <= 90f
    val brush = getCardBackgroundBrush(card.colorHex)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 14f * density
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFlip()
                }
                .testTag("debit_card_${card.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(card.colorHex)),
            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush)
            ) {
                if (isFrontVisible) {
                    DebitCardFrontView(
                        card = card,
                        isUnmasked = isUnmasked,
                        onToggleMask = onToggleMask,
                        memberName = memberName,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        onCopy = {
                            copyToClipboard(context, "Debit Card Number", card.cardNumber)
                            onCopyNumber?.invoke()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                    ) {
                        DebitCardBackView(
                            card = card,
                            isUnmasked = isUnmasked,
                            onCopyCvv = {
                                copyToClipboard(context, "CVV", card.cvv)
                            }
                        )
                    }
                }
            }
        }

        // Expanded View revealing extra details
        CardExpandedDetailsView(
            isExpanded = isExpanded,
            onToggleExpand = { isExpanded = !isExpanded },
            linkedEmail = card.linkedEmail,
            linkedPhone = card.linkedPhone,
            issuanceDate = card.issuanceDate,
            rewardPoints = if (card.rewardPoints > 0) "${card.rewardPoints} pts" else null,
            statementDate = "",
            dueDate = "",
            remindExpiry = card.remindExpiry,
            remindBillDate = false,
            remindDueDate = false,
            memberName = memberName,
            colorHex = card.colorHex,
            isCredit = false,
            cardNumber = card.cardNumber,
            cvv = card.cvv,
            atmPin = card.atmPin,
            cardPin = card.cardPin,
            domesticPosLimit = card.domesticPosLimit,
            dailyAtmLimit = card.dailyAtmLimit,
            internationalUsage = card.internationalUsage,
            frontCardImagePath = card.frontCardImagePath,
            backCardImagePath = card.backCardImagePath,
            attachmentPaths = card.attachmentPaths,
            isUnmasked = isUnmasked,
            onCopy = { label, text -> copyToClipboard(context, label, text) },
            onOpenHelpline = onOpenHelpline
        )
    }
}

@Composable
private fun CreditCardFrontView(
    card: CreditCard,
    isUnmasked: Boolean,
    onToggleMask: () -> Unit,
    memberName: String?,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onCopy: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row 1: Bank Name, Card Name, Member Badge & Card Network Logo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = card.bankName.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        fontSize = 15.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = card.cardName.ifBlank { "Credit Card" },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!memberName.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memberName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
                CardNetworkBadge(network = card.network)
            }
        }

        // Row 2: Chip and CC Reward Points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmvChipGraphic()

            if (card.ccRewardPoints > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                    Text(
                        text = "${card.ccRewardPoints} pts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = Color.White
                    )
                }
            }
        }

        // Row 3: Card Number with Masking Toggle and Copy Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayNumber = if (isUnmasked) {
                formatFullCardNumber(card.cardNumber)
            } else {
                formatMaskedCardNumber(card.cardNumber)
            }

            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 4.dp)
            ) {
                AnimatedContent(targetState = displayNumber, label = "card_num_anim") { number ->
                    Text(
                        text = number,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 16.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleMask, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isUnmasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Mask toggle",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy number",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Row 4: Cardholder Name, Expiry, Bill/Due Date, and Edit/Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "CARDHOLDER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = card.cardholderName.ifBlank { "CARDHOLDER" }.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "EXPIRES",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = formatExpiryDisplay(card.expiry),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val last4 = if (card.cardNumber.length >= 4) card.cardNumber.takeLast(4) else card.cardNumber
                        SafeVaultShareManager.shareCardDetails(
                            context = context,
                            bankName = card.bankName,
                            cardType = "Credit Card",
                            last4 = last4,
                            holderName = card.cardholderName,
                            expiry = formatExpiryDisplay(card.expiry),
                            masked = true
                        )
                    },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Card Info",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(15.dp)
                    )
                }
                if (onEdit != null) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Card", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(15.dp))
                    }
                }
                if (onDelete != null) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DebitCardFrontView(
    card: DebitCard,
    isUnmasked: Boolean,
    onToggleMask: () -> Unit,
    memberName: String?,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onCopy: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = card.bankName.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        fontSize = 15.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = "DEBIT • ${card.cardName.ifBlank { "Card" }}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!memberName.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memberName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
                CardNetworkBadge(network = card.network)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmvChipGraphic()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayNumber = if (isUnmasked) {
                formatFullCardNumber(card.cardNumber)
            } else {
                formatMaskedCardNumber(card.cardNumber)
            }

            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 4.dp)
            ) {
                AnimatedContent(targetState = displayNumber, label = "debit_num_anim") { number ->
                    Text(
                        text = number,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 16.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleMask, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isUnmasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Mask toggle",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy number",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "CARDHOLDER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = card.cardholderName.ifBlank { "CARDHOLDER" }.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "EXPIRES",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = formatExpiryDisplay(card.expiry),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val last4 = if (card.cardNumber.length >= 4) card.cardNumber.takeLast(4) else card.cardNumber
                        SafeVaultShareManager.shareCardDetails(
                            context = context,
                            bankName = card.bankName,
                            cardType = "Debit Card",
                            last4 = last4,
                            holderName = card.cardholderName,
                            expiry = formatExpiryDisplay(card.expiry),
                            masked = true
                        )
                    },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Card Info",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(15.dp)
                    )
                }
                if (onEdit != null) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Card", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(15.dp))
                    }
                }
                if (onDelete != null) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreditCardBackView(
    card: CreditCard,
    isUnmasked: Boolean,
    onCopyCvv: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Magnetic Stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(42.dp)
                .background(Color.Black)
        )

        // Signature Strip & CVV Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(Color.White)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = card.cardholderName.ifBlank { "Authorized Signature" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A), // Royal blue fountain ink signature
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "AUTH SIGN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.LightGray,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE2E8F0))
                    .clickable { onCopyCvv() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnmasked) card.cvv else "•••",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
        }

        // Reminders & Dates row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                if (card.statementDate.isNotBlank()) {
                    Text(
                        text = "Bill Date: ${card.statementDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                if (card.dueDate.isNotBlank()) {
                    Text(
                        text = "Due Date: ${card.dueDate}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Flip back", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                Text(text = "Tap to flip", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun DebitCardBackView(
    card: DebitCard,
    isUnmasked: Boolean,
    onCopyCvv: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(42.dp)
                .background(Color.Black)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(Color.White)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = card.cardholderName.ifBlank { "Authorized Signature" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A), // Royal blue fountain ink signature
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "AUTH SIGN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.LightGray,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE2E8F0))
                    .clickable { onCopyCvv() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnmasked) card.cvv else "•••",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Issuance: ${card.issuanceDate.ifBlank { "N/A" }}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Flip back", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                Text(text = "Tap to flip", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

/**
 * Authentic Network Logos for RuPay, Visa, Mastercard, Amex
 */
@Composable
fun CardNetworkBadge(network: CardNetwork) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White.copy(alpha = 0.18f),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)) {
            when (network) {
                CardNetwork.RUPAY -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFF27922)))
                        Text(
                            text = "RuPay",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.5.sp),
                            color = Color.White
                        )
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0F9D58)))
                    }
                }
                CardNetwork.VISA -> {
                    Text(
                        text = "VISA",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = Color.White
                    )
                }
                CardNetwork.MASTERCARD -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy((-4).dp)
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFEB001B)))
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFF79E1B).copy(alpha = 0.9f)))
                    }
                }
                CardNetwork.AMEX -> {
                    Text(
                        text = "AMEX",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = Color(0xFF60A5FA)
                    )
                }
            }
        }
    }
}

@Composable
fun EmvChipGraphic() {
    Surface(
        modifier = Modifier.size(width = 38.dp, height = 28.dp),
        shape = RoundedCornerShape(5.dp),
        color = Color(0xFFD4AF37),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8A6827))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineColor = Color(0xFF7A5C20)
            drawLine(color = lineColor, start = Offset(0f, size.height / 2), end = Offset(size.width, size.height / 2), strokeWidth = 1.5f)
            drawLine(color = lineColor, start = Offset(size.width * 0.35f, 0f), end = Offset(size.width * 0.35f, size.height), strokeWidth = 1.5f)
            drawLine(color = lineColor, start = Offset(size.width * 0.65f, 0f), end = Offset(size.width * 0.65f, size.height), strokeWidth = 1.5f)
        }
    }
}

private fun formatMaskedCardNumber(raw: String): String {
    val clean = raw.replace(" ", "")
    if (clean.length < 4) return "•••• •••• •••• ••••"
    val last4 = clean.takeLast(4)
    return "•••• •••• •••• $last4"
}

private fun formatFullCardNumber(raw: String): String {
    val clean = raw.replace(" ", "")
    return clean.chunked(4).joinToString(" ")
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

@Composable
fun CardExpandedDetailsView(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    linkedEmail: String,
    linkedPhone: String,
    issuanceDate: String,
    rewardPoints: String?,
    statementDate: String,
    dueDate: String,
    remindExpiry: Boolean,
    remindBillDate: Boolean,
    remindDueDate: Boolean,
    memberName: String?,
    colorHex: Long,
    isCredit: Boolean,
    cardNumber: String,
    cvv: String,
    atmPin: String = "",
    cardPin: String = "",
    isBillPaid: Boolean = false,
    lastPaidDate: String = "",
    domesticPosLimit: Long = 0L,
    dailyAtmLimit: Long = 0L,
    internationalUsage: Boolean = false,
    frontCardImagePath: String? = null,
    backCardImagePath: String? = null,
    attachmentPaths: List<String> = emptyList(),
    isUnmasked: Boolean,
    onCopy: (String, String) -> Unit,
    onToggleBillPaid: (() -> Unit)? = null,
    onOpenHelpline: (() -> Unit)? = null
) {
    val accent = Color(colorHex)
    val context = LocalContext.current
    var isPinRevealed by remember { mutableStateOf(false) }

    // Expand / collapse hint pill
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Hide details" else "View details",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isExpanded) "Hide details" else "View details & linked information",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    AnimatedVisibility(
        visible = isExpanded,
        enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.5.dp, accent.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                accent.copy(alpha = 0.10f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCredit) "CREDIT CARD VAULT DOSSIER" else "DEBIT CARD VAULT DOSSIER",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = accent
                        )

                        if (!memberName.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = accent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = memberName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = accent
                                )
                            }
                        }
                    }

                    // Protected PIN Vault Section (ATM PIN & Card PIN with Fingerprint reveal)
                    if (atmPin.isNotBlank() || cardPin.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
                                        Text(
                                            text = "SECURITY PIN VAULT",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                                            color = accent
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = accent.copy(alpha = 0.15f),
                                        modifier = Modifier.clickable {
                                            if (!isPinRevealed) {
                                                (context as? FragmentActivity)?.let { activity ->
                                                    BiometricAuthManager.authenticateForSecret(activity, "Card PINs") {
                                                        isPinRevealed = true
                                                    }
                                                } ?: run { isPinRevealed = true }
                                            } else {
                                                isPinRevealed = false
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isPinRevealed) Icons.Default.VisibilityOff else Icons.Default.Fingerprint,
                                                contentDescription = if (isPinRevealed) "Hide PIN" else "Fingerprint to view PIN",
                                                tint = accent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (isPinRevealed) "Hide PINs" else "Tap Fingerprint",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                                color = accent
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (atmPin.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                            modifier = if (cardPin.isNotBlank()) Modifier.weight(1f) else Modifier.weight(0.5f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("ATM PIN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(
                                                        text = if (isPinRevealed) atmPin else "••••",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                if (isPinRevealed) {
                                                    IconButton(onClick = { onCopy("ATM PIN", atmPin) }, modifier = Modifier.size(24.dp)) {
                                                        Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy ATM PIN", modifier = Modifier.size(13.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (cardPin.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                            modifier = if (atmPin.isNotBlank()) Modifier.weight(1f) else Modifier.weight(0.5f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text("CARD / POS PIN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(
                                                        text = if (isPinRevealed) cardPin else "••••",
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                                if (isPinRevealed) {
                                                    IconButton(onClick = { onCopy("Card PIN", cardPin) }, modifier = Modifier.size(24.dp)) {
                                                        Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Card PIN", modifier = Modifier.size(13.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (atmPin.isNotBlank() && cardPin.isBlank()) {
                                        Spacer(modifier = Modifier.weight(0.5f))
                                    } else if (atmPin.isBlank() && cardPin.isNotBlank()) {
                                        Spacer(modifier = Modifier.weight(0.5f))
                                    }
                                }
                            }
                        }
                    }

                    // Linked Email & Phone section
                    if (linkedEmail.isNotBlank() || linkedPhone.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (linkedEmail.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
                                            Text("Email:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(linkedEmail, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        IconButton(onClick = { onCopy("Linked Email", linkedEmail) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Email", modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }
                                if (linkedPhone.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
                                            Text("Phone:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(linkedPhone, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        IconButton(onClick = { onCopy("Linked Phone", linkedPhone) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Phone", modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Key Schedule & Numbers Grid
                    if (issuanceDate.isNotBlank() || !rewardPoints.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (issuanceDate.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    modifier = if (!rewardPoints.isNullOrBlank()) Modifier.weight(1f) else Modifier.weight(0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("ISSUANCE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(issuanceDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }

                            if (!rewardPoints.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    modifier = if (issuanceDate.isNotBlank()) Modifier.weight(1f) else Modifier.weight(0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("REWARD POINTS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(rewardPoints, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFD97706)))
                                    }
                                }
                            }

                            if (issuanceDate.isNotBlank() && rewardPoints.isNullOrBlank()) {
                                Spacer(modifier = Modifier.weight(0.5f))
                            } else if (issuanceDate.isBlank() && !rewardPoints.isNullOrBlank()) {
                                Spacer(modifier = Modifier.weight(0.5f))
                            }
                        }
                    }

                    // Attached Scans & Documents (Front, Back, Statements)
                    val allAttached = listOfNotNull(
                        frontCardImagePath?.let { Pair("Front Scan", it) },
                        backCardImagePath?.let { Pair("Back Scan", it) }
                    ) + attachmentPaths.map { Pair("Document", it) }

                    if (allAttached.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ATTACHED CARD SCANS & FILES (${allAttached.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                                color = accent
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(allAttached) { (tag, path) ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = accent.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = accent, modifier = Modifier.size(12.dp))
                                            Text(tag, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), color = accent)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Credit Card Billing Cycle
                    if (isCredit && (statementDate.isNotBlank() || dueDate.isNotBlank())) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (statementDate.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("BILL STATEMENT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(statementDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                            if (dueDate.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("PAYMENT DUE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(dueDate, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error))
                                    }
                                }
                            }
                        }
                    }

                    // Bill Tracker & Payment Status
                    if (isCredit && (statementDate.isNotBlank() || dueDate.isNotBlank() || onToggleBillPaid != null)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isBillPaid) Color(0xFF059669).copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, if (isBillPaid) Color(0xFF059669).copy(alpha = 0.4f) else MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        imageVector = if (isBillPaid) Icons.Default.Check else Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = if (isBillPaid) Color(0xFF059669) else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isBillPaid) "Statement Bill Paid ✓" else "Bill Due / Pending",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isBillPaid) Color(0xFF059669) else MaterialTheme.colorScheme.error
                                        )
                                        if (isBillPaid && lastPaidDate.isNotBlank()) {
                                            Text("Paid on $lastPaidDate", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        } else if (dueDate.isNotBlank()) {
                                            Text("Due: $dueDate", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }

                                if (onToggleBillPaid != null) {
                                    Button(
                                        onClick = onToggleBillPaid,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isBillPaid) Color(0xFF059669) else MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isBillPaid) "Paid ✓" else "Mark Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Card Limits & Helpline Action Row
                    if (domesticPosLimit > 0 || dailyAtmLimit > 0 || onOpenHelpline != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (domesticPosLimit > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                        Text("POS LIMIT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("₹${domesticPosLimit / 1000}k", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }

                            if (dailyAtmLimit > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                        Text("ATM LIMIT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("₹${dailyAtmLimit / 1000}k", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }

                            if (onOpenHelpline != null) {
                                Button(
                                    onClick = onOpenHelpline,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Helpline", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Active reminder chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = accent, modifier = Modifier.size(13.dp))
                        Text("Active Alerts:", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (remindExpiry) {
                            Surface(shape = RoundedCornerShape(4.dp), color = accent.copy(alpha = 0.12f)) {
                                Text("Expiry", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = accent)
                            }
                        }
                        if (remindBillDate) {
                            Surface(shape = RoundedCornerShape(4.dp), color = accent.copy(alpha = 0.12f)) {
                                Text("Bill Date", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = accent)
                            }
                        }
                        if (remindDueDate) {
                            Surface(shape = RoundedCornerShape(4.dp), color = accent.copy(alpha = 0.12f)) {
                                Text("Due Date", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = accent)
                            }
                        }
                    }
                }
            }
        }
    }
}
