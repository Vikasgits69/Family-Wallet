package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.parseHexColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomColorPickerDialog(
    initialColorHex: String = "#4F46E5",
    title: String = "Custom Accent Color",
    onColorSelected: (colorLong: Long, hexString: String) -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val parsedInitial = remember(initialColorHex) {
        parseHexColor(initialColorHex) ?: Color(0xFF4F46E5)
    }

    val initialHsv = remember(parsedInitial) {
        val hsv = FloatArray(3)
        val colorInt = android.graphics.Color.argb(
            255,
            (parsedInitial.red * 255).toInt(),
            (parsedInitial.green * 255).toInt(),
            (parsedInitial.blue * 255).toInt()
        )
        android.graphics.Color.colorToHSV(colorInt, hsv)
        hsv
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1].coerceIn(0.05f, 1f)) }
    var value by remember { mutableFloatStateOf(initialHsv[2].coerceIn(0.15f, 1f)) }

    val currentColorInt = remember(hue, saturation, value) {
        android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
    }
    val currentColor = remember(currentColorInt) {
        Color(currentColorInt)
    }
    val currentHex = remember(currentColorInt) {
        String.format("#%06X", 0xFFFFFF and currentColorInt)
    }

    var hexInputText by remember { mutableStateOf(currentHex) }

    val presetPalettes = listOf(
        Pair("Indigo", "#4F46E5"),
        Pair("Blue", "#2563EB"),
        Pair("Sky", "#0284C7"),
        Pair("Cyan", "#06B6D4"),
        Pair("Teal", "#0D9488"),
        Pair("Emerald", "#10B981"),
        Pair("Lime", "#84CC16"),
        Pair("Amber", "#F59E0B"),
        Pair("Orange", "#F97316"),
        Pair("Coral", "#EF4444"),
        Pair("Rose", "#E11D48"),
        Pair("Pink", "#EC4899"),
        Pair("Purple", "#8B5CF6"),
        Pair("Violet", "#7C3AED"),
        Pair("Slate", "#64748B"),
        Pair("Dark", "#1E293B")
    )

    fun applyHsvFromHex(hex: String) {
        val c = parseHexColor(hex)
        if (c != null) {
            val hsv = FloatArray(3)
            val cInt = android.graphics.Color.argb(255, (c.red * 255).toInt(), (c.green * 255).toInt(), (c.blue * 255).toInt())
            android.graphics.Color.colorToHSV(cInt, hsv)
            hue = hsv[0]
            saturation = hsv[1]
            value = hsv[2]
            hexInputText = hex.uppercase()
        }
    }

    val hueGradient = remember {
        Brush.horizontalGradient(
            listOf(
                Color.Red, Color.Yellow, Color.Green,
                Color.Cyan, Color.Blue, Color.Magenta, Color.Red
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("custom_color_picker_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(currentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Live Color Preview Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = currentColor,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val lum = currentColor.red * 0.299f + currentColor.green * 0.587f + currentColor.blue * 0.114f
                            val textTint = if (lum > 0.6f) Color.Black else Color.White
                            Text(
                                text = "Selected Accent",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = textTint.copy(alpha = 0.8f)
                            )
                            Text(
                                text = currentHex,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = textTint
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(currentHex))
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White, modifier = Modifier.size(12.dp))
                                Text("Copy", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Preset Swatches Flow Grid
                Text(
                    text = "QUICK PALETTE SWATCHES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetPalettes.forEach { (name, hex) ->
                        val pColor = parseHexColor(hex) ?: Color.Gray
                        val isSelected = currentHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(pColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable { applyHsvFromHex(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = name, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Interactive Sliders: Hue, Saturation, Brightness
                Text(
                    text = "FINE-TUNE ADJUSTMENT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Hue Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Hue", style = MaterialTheme.typography.bodySmall)
                        Text("${hue.toInt()}°", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(hueGradient)
                    )
                    Slider(
                        value = hue,
                        onValueChange = {
                            hue = it
                            hexInputText = String.format("#%06X", 0xFFFFFF and android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value)))
                        },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 2. Saturation Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saturation", style = MaterialTheme.typography.bodySmall)
                        Text("${(saturation * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = saturation,
                        onValueChange = {
                            saturation = it
                            hexInputText = String.format("#%06X", 0xFFFFFF and android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value)))
                        },
                        valueRange = 0.05f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 3. Brightness Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Brightness", style = MaterialTheme.typography.bodySmall)
                        Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = value,
                        onValueChange = {
                            value = it
                            hexInputText = String.format("#%06X", 0xFFFFFF and android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value)))
                        },
                        valueRange = 0.15f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Direct HEX Input
                Text(
                    text = "OR ENTER HEX CODE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = hexInputText,
                    onValueChange = { input ->
                        val cleaned = if (input.startsWith("#")) input else "#$input"
                        hexInputText = cleaned
                        applyHsvFromHex(cleaned)
                    },
                    label = { Text("HEX Color (e.g. #4F46E5)", fontSize = 11.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(currentColor)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                    },
                    trailingIcon = {
                        if (hexInputText.isNotEmpty()) {
                            IconButton(
                                onClick = { hexInputText = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val colorLong = 0xFF000000L or (0xFFFFFFL and currentColorInt.toLong())
                    onColorSelected(colorLong, currentHex)
                    onDismiss()
                },
                modifier = Modifier.testTag("apply_color_picker_button")
            ) {
                Text("Apply Color", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
