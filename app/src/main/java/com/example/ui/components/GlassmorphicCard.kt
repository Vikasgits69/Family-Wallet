package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Cohesive Doodle & Material 3 Card Shape Tokens
val DoodleCardShape = RoundedCornerShape(20.dp)
val DoodleSubCardShape = RoundedCornerShape(14.dp)
val DoodleChipShape = RoundedCornerShape(10.dp)
val DoodleDialogShape = RoundedCornerShape(24.dp)

/**
 * Pure Material 3 & Doodle Aesthetic Card Container.
 * Standardizes consistent 20.dp corner radius, 1.5.dp outline stroke, and 2.dp tactile elevation.
 */
@Composable
fun MaterialCardContainer(
    modifier: Modifier = Modifier,
    shape: Shape = DoodleCardShape,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tonalElevation: Dp = 2.dp,
    shadowElevation: Dp = 2.dp,
    border: BorderStroke? = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

// Alias for backwards compatibility
@Composable
fun GlassmorphicContainer(
    modifier: Modifier = Modifier,
    shape: Shape = DoodleCardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    borderWidth: Dp = 1.5.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    MaterialCardContainer(
        modifier = modifier,
        shape = shape,
        containerColor = backgroundColor,
        border = BorderStroke(borderWidth, borderColor),
        onClick = onClick,
        content = content
    )
}
