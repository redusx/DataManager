package com.example.datamanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.ui.theme.CategoryCardsTint
import com.example.datamanager.ui.theme.CategoryIdentityTint
import com.example.datamanager.ui.theme.CategoryLoginsTint
import com.example.datamanager.ui.theme.CategoryNotesTint
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun EntryCard(
    entry: DataEntry,
    onCardClick: () -> Unit,
    onCopyClick: (secret: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fields = remember(entry.fieldsJson) {
        try {
            val type = object : TypeToken<List<FieldItem>>() {}.type
            Gson().fromJson<List<FieldItem>>(entry.fieldsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Determine subtitle (Username / Card Number Hint / ID Hint)
    val subtitleField = remember(fields) {
        fields.firstOrNull { it.key in listOf("username", "email", "card_holder", "first_name", "bank_name", "website") }
            ?: fields.firstOrNull { !it.isSensitive && it.value.isNotEmpty() }
    }

    // Determine primary secret for 1-tap copy
    val primarySecretField = remember(fields) {
        fields.firstOrNull { it.type == FieldType.PASSWORD }
            ?: fields.firstOrNull { it.type == FieldType.CARD_NUMBER }
            ?: fields.firstOrNull { it.type == FieldType.IBAN }
            ?: fields.firstOrNull { it.isSensitive }
            ?: fields.firstOrNull { it.value.isNotEmpty() }
    }

    val category = Category.fromId(entry.category)
    val (categoryIcon, categoryTint) = getCategoryVisuals(category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .clickable(onClick = onCardClick)
            .padding(horizontal = Spacing.m, vertical = Spacing.s)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge (36x36dp)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(ShapeTokens.BadgeRadius)
                    .background(categoryTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.s))

            // Center Column: Title, Subtitle, and Masked Secret indicator
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (subtitleField != null && subtitleField.value.isNotEmpty()) {
                        Text(
                            text = subtitleField.value,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    if (primarySecretField != null && primarySecretField.isSensitive) {
                        if (subtitleField != null && subtitleField.value.isNotEmpty()) {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "••••••••••••",
                            style = MonospaceSecretStyle.copy(
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            // Right Action: 1-Tap Quick-Copy Button (48dp touch bounds)
            if (primarySecretField != null && primarySecretField.value.isNotEmpty()) {
                Spacer(modifier = Modifier.width(Spacing.xs))
                CopyButton(
                    onClick = { onCopyClick(primarySecretField.value) },
                    contentDescription = "${entry.title} kopyala"
                )
            }
        }
    }
}

fun getCategoryVisuals(category: Category): Pair<ImageVector, Color> {
    return when (category) {
        Category.ACCOUNT -> Icons.Rounded.Lock to CategoryLoginsTint
        Category.FINANCIAL -> Icons.Rounded.CreditCard to CategoryCardsTint
        Category.PERSONAL -> Icons.Rounded.AccountCircle to CategoryIdentityTint
        Category.CUSTOM -> Icons.Rounded.Description to CategoryNotesTint
    }
}
