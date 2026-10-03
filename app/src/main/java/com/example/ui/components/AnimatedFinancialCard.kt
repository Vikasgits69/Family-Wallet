package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Badge
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CardNetwork
import com.example.data.CardStatus
import com.example.data.CardThemeColor
import com.example.data.CreditCard
import com.example.data.DebitCard
import com.example.util.CurrencyFormatter

/**
 * Solid Block Colors with Subtle Depth for Google Wallet Aesthetic
 */
fun getCardBackgroundBrush(themeColor: CardThemeColor): Brush {
    return when (themeColor) {
        CardThemeColor.CHARCOAL -> Brush.linearGradient(
            colors = listOf(Color(0xFF232738), Color(0xFF131622), Color(0xFF1E2235))
        )
        CardThemeColor.EMERALD -> Brush.linearGradient(
            colors = listOf(Color(0xFF0F5A47), Color(0xFF07382B), Color(0xFF032219))
        )
        CardThemeColor.SAPPHIRE -> Brush.linearGradient(
            colors = listOf(Color(0xFF1E3A8A), Color(0xFF172554), Color(0xFF0F172A))
        )
        CardThemeColor.INDIGO -> Brush.linearGradient(
            colors = listOf(Color(0xFF4338CA), Color(0xFF312E81), Color(0xFF1E1B4B))
        )
        CardThemeColor.RUBY -> Brush.linearGradient(
            colors = listOf(Color(0xFF881337), Color(0xFF4C0519), Color(0xFF28020D))
        )
        CardThemeColor.AMBER -> Brush.linearGradient(
            colors = listOf(Color(0xFF92400E), Color(0xFF78350F), Color(0xFF451A03))
        )
        CardThemeColor.TITANIUM -> Brush.linearGradient(
            colors = listOf(Color(0xFF854D0E), Color(0xFFA16207), Color(0xFF713F12))
        )
    }
}

/**
 * 3D Flippable Credit Card Component
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
    onCopyNumber: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val haptic = LocalHapticFeedback.current

    // Smooth 3D Flip animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_3d_flip"
    )

    val isFrontVisible = rotation <= 90f
    val brush = getCardBackgroundBrush(card.themeColor)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // Standard ID-1 ISO card aspect ratio
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
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        ) {
            // Subtle geometric mesh lines for authentic Google Wallet card finish
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeColor = Color.White.copy(alpha = 0.04f)
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, size.height * 0.3f),
                    end = Offset(size.width, size.height * 0.8f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, size.height * 0.7f),
                    end = Offset(size.width * 0.8f, 0f),
                    strokeWidth = 2f
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = size.width * 0.4f,
                    center = Offset(size.width * 0.9f, size.height * 0.1f)
                )
            }

            if (isFrontVisible) {
                // FRONT OF CREDIT CARD
                CreditCardFrontView(
                    card = card,
                    isUnmasked = isUnmasked,
                    onToggleMask = onToggleMask,
                    memberName = memberName,
                    onCopy = {
                        copyToClipboard(context, "Card Number", card.cardNumber)
                        onCopyNumber?.invoke()
                    }
                )
            } else {
                // BACK OF CREDIT CARD (Rotated back to readable orientation)
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
}

/**
 * 3D Flippable Debit Card Component
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
    onCopyNumber: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val haptic = LocalHapticFeedback.current

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "debit_card_3d_flip"
    )

    val isFrontVisible = rotation <= 90f
    val brush = getCardBackgroundBrush(card.themeColor)

    Card(
        modifier = modifier
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
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeColor = Color.White.copy(alpha = 0.04f)
                drawLine(
                    color = strokeColor,
                    start = Offset(size.width * 0.1f, 0f),
                    end = Offset(size.width, size.height * 0.9f),
                    strokeWidth = 2f
                )
            }

            if (isFrontVisible) {
                DebitCardFrontView(
                    card = card,
                    isUnmasked = isUnmasked,
                    onToggleMask = onToggleMask,
                    memberName = memberName,
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
}

// ==========================================
// FRONT VIEWS
// ==========================================

@Composable
private fun CreditCardFrontView(
    card: CreditCard,
    isUnmasked: Boolean,
    onToggleMask: () -> Unit,
    memberName: String?,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row 1: Bank Name, Member Badge & Card Network Logo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = card.bankName.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = card.cardName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (memberName != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memberName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                CardNetworkBadge(network = card.network)
            }
        }

        // Row 2: EMV Chip & Contactless Wave
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmvChipGraphic()
            ContactlessWaveGraphic()
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

            AnimatedContent(
                targetState = displayNumber,
                label = "card_number_anim"
            ) { number ->
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleMask,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isUnmasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isUnmasked) "Mask card" else "Unmask card",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy number",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Row 4: Cardholder Name, Expiry Date & Flip Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "CARDHOLDER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = card.cardholderName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VALID THRU",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = card.expiry,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Flip Card",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "3D Flip",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.White
                    )
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
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = card.bankName.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "DEBIT CARD • ${card.linkedAccount}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (memberName != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memberName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                CardNetworkBadge(network = card.network)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmvChipGraphic()
            ContactlessWaveGraphic()
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

            AnimatedContent(
                targetState = displayNumber,
                label = "debit_number_anim"
            ) { number ->
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleMask,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isUnmasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Mask toggle",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy number",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "CARDHOLDER",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = card.cardholderName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EXPIRES",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = card.expiry,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Flip",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "3D Flip",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==========================================
// BACK OF CARDS
// ==========================================

@Composable
private fun CreditCardBackView(
    card: CreditCard,
    isUnmasked: Boolean,
    onCopyCvv: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Magnetic Stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(Color(0xFF0F0F12))
        )

        // 2. White Signature / CVV Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Signature band
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Authorized Signature • Not Valid Unless Signed",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // CVV Box
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.clickable { onCopyCvv() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CVV:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = if (isUnmasked) card.cvv else "•••",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }

        // 3. Vault & Informational Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "STATEMENT DATE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = card.statementDate,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PAYMENT DUE DATE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = card.dueDate,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Limit: ${CurrencyFormatter.formatRupees(card.creditLimit, false)} • Fee: ${CurrencyFormatter.formatRupees(card.annualFee, false)}/yr",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color.White.copy(alpha = 0.85f)
            )

            Text(
                text = "24x7 Customer Helpline: 1800 202 6161",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
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
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(Color(0xFF0F0F12))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Authorized Signature • Bank Property",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.clickable { onCopyCvv() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CVV:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = if (isUnmasked) card.cvv else "•••",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ATM DAILY LIMIT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = CurrencyFormatter.formatRupees(card.atmLimit, false),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "POS/ONLINE LIMIT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = CurrencyFormatter.formatRupees(card.posLimit, false),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Linked: ${card.linkedAccount} • 24x7 Helpline: 1800 11 2211",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

// ==========================================
// BADGES & GRAPHICS
// ==========================================

@Composable
fun CardNetworkBadge(network: CardNetwork) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                when (network) {
                    CardNetwork.RUPAY -> Color(0xFF0C2340) // RuPay Navy Deep Blue
                    CardNetwork.VISA -> Color(0xFF1434CB)
                    CardNetwork.MASTERCARD -> Color(0xFFEB001B)
                    CardNetwork.AMEX -> Color(0xFF006FCF)
                }
            )
            .border(
                1.dp,
                when (network) {
                    CardNetwork.RUPAY -> Color(0xFFF27922) // RuPay Orange Border
                    else -> Color.White.copy(alpha = 0.3f)
                },
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (network) {
            CardNetwork.RUPAY -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // RuPay Orange and Green Flag Accent
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF27922))
                    )
                    Text(
                        text = "RuPay",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F9D58))
                    )
                }
            }
            CardNetwork.VISA -> {
                Text(
                    text = "VISA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
            }
            CardNetwork.MASTERCARD -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEB001B)))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF79E1B)))
                }
            }
            CardNetwork.AMEX -> {
                Text(
                    text = "AMEX",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun EmvChipGraphic() {
    Surface(
        modifier = Modifier
            .size(width = 38.dp, height = 28.dp),
        shape = RoundedCornerShape(5.dp),
        color = Color(0xFFD4AF37), // Metallic Gold
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8A6827))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineColor = Color(0xFF7A5C20)
            // Horizontal circuit line
            drawLine(
                color = lineColor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1.5f
            )
            // Vertical circuit lines
            drawLine(
                color = lineColor,
                start = Offset(size.width * 0.35f, 0f),
                end = Offset(size.width * 0.35f, size.height),
                strokeWidth = 1.5f
            )
            drawLine(
                color = lineColor,
                start = Offset(size.width * 0.65f, 0f),
                end = Offset(size.width * 0.65f, size.height),
                strokeWidth = 1.5f
            )
        }
    }
}

@Composable
fun ContactlessWaveGraphic() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = 2f
        val color = Color.White.copy(alpha = 0.8f)
        drawArc(
            color = color,
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(size.width * 0.2f, size.height * 0.2f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.6f, size.height * 0.6f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        drawArc(
            color = color,
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(size.width * 0.4f, size.height * 0.35f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.4f, size.height * 0.4f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
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
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
}
