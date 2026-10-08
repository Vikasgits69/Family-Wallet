package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.FamilyMember
import com.example.data.Subscription
import com.example.ui.components.CustomColorPickerDialog
import com.example.ui.theme.CardColorBlockOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

val popularSubscriptionPresets = listOf(
    "Netflix", "Spotify", "Amazon Prime", "YouTube Premium", "iCloud+",
    "Disney+ Hotstar", "Google One", "Broadband / Fiber", "Gym / Fitness"
)

val billingCycles = listOf("Monthly", "Quarterly", "Annual")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionDialog(
    members: List<FamilyMember>,
    subscriptionToEdit: Subscription? = null,
    onDismiss: () -> Unit,
    onSaveSubscription: (Subscription) -> Unit
) {
    val isEditing = subscriptionToEdit != null

    var name by remember { mutableStateOf(subscriptionToEdit?.name ?: "") }
    var planName by remember { mutableStateOf(subscriptionToEdit?.planName ?: "") }
    var costText by remember { mutableStateOf(subscriptionToEdit?.let { if (it.cost > 0) it.cost.toInt().toString() else "" } ?: "") }
    var billingCycle by remember { mutableStateOf(subscriptionToEdit?.billingCycle ?: "Monthly") }
    var nextRenewalDate by remember { mutableStateOf(subscriptionToEdit?.nextRenewalDate ?: "") }
    var linkedPaymentMethod by remember { mutableStateOf(subscriptionToEdit?.linkedPaymentMethod ?: "") }
    var selectedMemberId by remember { mutableStateOf(subscriptionToEdit?.memberId ?: "") }
    var category by remember { mutableStateOf(subscriptionToEdit?.category ?: "Entertainment") }
    var notes by remember { mutableStateOf(subscriptionToEdit?.notes ?: "") }
    var selectedColor by remember { mutableLongStateOf(subscriptionToEdit?.colorHex ?: CardColorBlockOptions.first()) }
    var showCustomColorPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .widthIn(max = 640.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(modifier = Modifier.fillMaxHeight()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Subscriptions,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isEditing) "Edit Subscription" else "Add Family Subscription",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Track renewals, cost & shared payment cards",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Presets
                    Text(
                        text = "POPULAR SERVICES",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(popularSubscriptionPresets) { preset ->
                            FilterChip(
                                selected = name == preset,
                                onClick = { name = preset },
                                label = { Text(preset, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Service Name *") },
                        placeholder = { Text("e.g. Netflix, Spotify, Prime") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = planName,
                            onValueChange = { planName = it },
                            label = { Text("Plan / Tier") },
                            placeholder = { Text("e.g. Premium 4K, Family") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = costText,
                            onValueChange = { costText = it.filter { c -> c.isDigit() } },
                            label = { Text("Cost (₹) *") },
                            placeholder = { Text("649") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    // Billing Cycle
                    Text(
                        text = "BILLING FREQUENCY",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(billingCycles) { cycle ->
                            FilterChip(
                                selected = billingCycle == cycle,
                                onClick = { billingCycle = cycle },
                                label = { Text(cycle, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Next Renewal Date with DatePicker
                    OutlinedTextField(
                        value = nextRenewalDate,
                        onValueChange = { nextRenewalDate = it },
                        label = { Text("Next Renewal / Due Date") },
                        placeholder = { Text("DD MMM YYYY") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = linkedPaymentMethod,
                        onValueChange = { linkedPaymentMethod = it },
                        label = { Text("Paid Via / Linked Card") },
                        placeholder = { Text("e.g. HDFC Millennia Card, Paytm UPI") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Assigned Member
                    if (members.isNotEmpty()) {
                        Text(
                            text = "ASSIGNED FAMILY MEMBER",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(members) { m ->
                                FilterChip(
                                    selected = selectedMemberId == m.id,
                                    onClick = {
                                        selectedMemberId = if (selectedMemberId == m.id) "" else m.id
                                    },
                                    label = { Text(m.name, fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                )
                            }
                        }
                    }

                    // Color Accent Block
                    Text(
                        text = "Select Color Accent",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            val isCustomSelected = !CardColorBlockOptions.contains(selectedColor)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCustomSelected) Color(selectedColor) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = if (isCustomSelected) 3.dp else 1.dp,
                                        color = if (isCustomSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = CircleShape
                                    )
                                    .clickable { showCustomColorPicker = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ColorLens,
                                    contentDescription = "Custom Accent Color",
                                    tint = if (isCustomSelected) Color.White else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        items(CardColorBlockOptions) { colorValue ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorValue))
                                    .border(
                                        width = if (selectedColor == colorValue) 3.dp else 1.dp,
                                        color = if (selectedColor == colorValue) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = colorValue },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColor == colorValue) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Login info") },
                        placeholder = { Text("e.g. Shared with 4 family profiles") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Bottom Action Button
                Surface(
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (name.isBlank()) return@Button
                            val sub = Subscription(
                                id = subscriptionToEdit?.id ?: UUID.randomUUID().toString(),
                                name = name.trim(),
                                planName = planName.trim(),
                                cost = costText.toDoubleOrNull() ?: 0.0,
                                billingCycle = billingCycle,
                                nextRenewalDate = nextRenewalDate.trim(),
                                linkedPaymentMethod = linkedPaymentMethod.trim(),
                                memberId = selectedMemberId,
                                category = category,
                                colorHex = selectedColor,
                                notes = notes.trim()
                            )
                            onSaveSubscription(sub)
                            onDismiss()
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEditing) "Save Changes" else "Add Subscription")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        nextRenewalDate = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    nextRenewalDate = ""
                    showDatePicker = false
                }) {
                    Text("Clear")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColorHex = String.format("#%06X", 0xFFFFFF and selectedColor.toInt()),
            title = "Custom Subscription Color Accent",
            onColorSelected = { colorLong, _ ->
                selectedColor = colorLong
            },
            onDismiss = { showCustomColorPicker = false }
        )
    }
}
