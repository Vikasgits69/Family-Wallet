package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.Face4
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FamilyMember
import com.example.data.RelationshipCategory
import java.io.File
import java.util.UUID

val defaultMemberColors = listOf(
    0xFF4F46E5, // Indigo
    0xFF059669, // Emerald
    0xFF0284C7, // Cyan
    0xFFD97706, // Amber
    0xFFE11D48, // Rose
    0xFF7C3AED, // Violet
    0xFF1F2937  // Slate
)

data class RelationshipCategoryItem(
    val category: RelationshipCategory,
    val title: String,
    val icon: ImageVector
)

val relationshipCategories = listOf(
    RelationshipCategoryItem(RelationshipCategory.SELF, "Self", Icons.Default.Person),
    RelationshipCategoryItem(RelationshipCategory.SPOUSE, "Spouse", Icons.Default.Favorite),
    RelationshipCategoryItem(RelationshipCategory.MOTHER, "Mother", Icons.Default.Face3),
    RelationshipCategoryItem(RelationshipCategory.FATHER, "Father", Icons.Default.SupervisorAccount),
    RelationshipCategoryItem(RelationshipCategory.BROTHER, "Brother", Icons.Default.Person),
    RelationshipCategoryItem(RelationshipCategory.SISTER, "Sister", Icons.Default.Face4),
    RelationshipCategoryItem(RelationshipCategory.OTHERS, "Others", Icons.Outlined.AccountCircle)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddMemberDialog(
    memberToEdit: FamilyMember? = null,
    onDismiss: () -> Unit,
    onConfirm: (FamilyMember) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(memberToEdit?.name ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            memberToEdit?.let {
                runCatching { RelationshipCategory.valueOf(it.relationshipCategory) }.getOrDefault(RelationshipCategory.OTHERS)
            } ?: RelationshipCategory.SELF
        )
    }
    var customRelationshipText by remember { mutableStateOf(memberToEdit?.customRelationship ?: "") }
    var profilePictureUri by remember { mutableStateOf<String?>(memberToEdit?.profilePictureUri) }
    var selectedColor by remember { mutableLongStateOf(memberToEdit?.colorHex ?: 0xFF4F46E5) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Android Photo Picker - copies to persistent app internal storage so it NEVER goes blank on reload
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = "profile_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val destFile = File(context.filesDir, fileName)
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                profilePictureUri = Uri.fromFile(destFile).toString()
            } catch (e: Exception) {
                profilePictureUri = uri.toString()
            }
        }
    }

    val isEditing = memberToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isEditing) "Edit Family Profile" else "Add Family Member",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Assign vault cards and accounts to this profile",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Picture Picker Section
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color(selectedColor).copy(alpha = 0.15f))
                                .border(2.dp, Color(selectedColor), CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profilePictureUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = com.example.util.ImageModelResolver.resolve(profilePictureUri),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Upload photo",
                                    tint = Color(selectedColor),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (profilePictureUri.isNullOrBlank()) "Tap to upload actual photo" else "Photo attached",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!profilePictureUri.isNullOrBlank()) {
                                IconButton(
                                    onClick = { profilePictureUri = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove photo", modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. Vikas Gupta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Professional Vector Icon Relationship Selector: Men, Women, Child, Parents, Siblings, Other
                Text(
                    text = "RELATIONSHIP CATEGORY (VECTOR ICONS)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    relationshipCategories.forEach { item ->
                        val isSelected = selectedCategory == item.category
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.clickable { selectedCategory = item.category }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // If "Others" is selected: reveal text field for user to specify exact relationship
                AnimatedVisibility(visible = selectedCategory == RelationshipCategory.OTHERS) {
                    OutlinedTextField(
                        value = customRelationshipText,
                        onValueChange = { customRelationshipText = it },
                        label = { Text("Specify Relationship *") },
                        placeholder = { Text("e.g. Mentor, Business Partner, Cousin, Guardian") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Color accent picker
                Text(
                    text = "PROFILE ACCENT COLOR",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(defaultMemberColors) { colorValue ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .border(
                                    width = if (selectedColor == colorValue) 3.dp else 1.dp,
                                    color = if (selectedColor == colorValue) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorValue },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorValue) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter the member's full name"
                        return@Button
                    }
                    if (selectedCategory == RelationshipCategory.OTHERS && customRelationshipText.isBlank()) {
                        errorMessage = "Please specify the custom relationship"
                        return@Button
                    }

                    val finalRelationship = if (selectedCategory == RelationshipCategory.OTHERS) {
                        customRelationshipText.trim()
                    } else {
                        selectedCategory.label
                    }

                    val updatedMember = FamilyMember(
                        id = memberToEdit?.id ?: "mem_${UUID.randomUUID().toString().take(8)}",
                        name = name.trim(),
                        relationship = finalRelationship,
                        relationshipCategory = selectedCategory.name,
                        customRelationship = customRelationshipText.trim(),
                        profilePictureUri = profilePictureUri,
                        colorHex = selectedColor
                    )

                    onConfirm(updatedMember)
                }
            ) {
                Text(if (isEditing) "Save Changes" else "Add Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
