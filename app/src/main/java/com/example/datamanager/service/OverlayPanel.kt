package com.example.datamanager.service

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Launch
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datamanager.R
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.ui.component.CopyButton
import com.example.datamanager.ui.theme.CategoryCardsTint
import com.example.datamanager.ui.theme.CategoryIdentityTint
import com.example.datamanager.ui.theme.CategoryLoginsTint
import com.example.datamanager.ui.theme.CategoryNotesTint
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.util.ClipboardHelper
import com.example.datamanager.util.FieldFormatter.formatFieldLabel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FlatField(
    val entryId: Long,
    val entryTitle: String,
    val category: String,
    val label: String,
    val value: String,
    val isSensitive: Boolean,
    val isFavorite: Boolean
)

@Composable
fun OverlayPanel(
    entries: List<DataEntry>,
    onMinimize: () -> Unit,
    onOpenMainApp: () -> Unit,
    onCloseOverlay: () -> Unit,
    onCopiedAndMinimize: () -> Unit = onMinimize,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gson = remember { Gson() }
    val fieldListType = remember { object : TypeToken<List<FieldItem>>() {}.type }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Flatten all entries into copyable fields
    val allFields = remember(entries) {
        val list = mutableListOf<FlatField>()
        for (entry in entries) {
            try {
                val fields: List<FieldItem> = gson.fromJson(entry.fieldsJson, fieldListType)
                for (field in fields) {
                    if (field.key.isNotBlank() && field.value.isNotBlank()) {
                        list.add(
                            FlatField(
                                entryId = entry.id,
                                entryTitle = entry.title,
                                category = entry.category,
                                label = field.key,
                                value = field.value,
                                isSensitive = field.isSensitive,
                                isFavorite = entry.isFavorite
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
        list
    }

    // Filter fields based on category and search query
    val filteredFields = remember(allFields, searchQuery, selectedCategory) {
        allFields.filter { item ->
            val matchesCategory = selectedCategory == null || item.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    item.entryTitle.contains(searchQuery, ignoreCase = true) ||
                    item.label.contains(searchQuery, ignoreCase = true) ||
                    item.value.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, ShapeTokens.OverlayRadius)
            .clip(ShapeTokens.OverlayRadius),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.m)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "MyVault",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenMainApp,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Launch,
                            contentDescription = stringResource(R.string.open_app),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.xxs))

                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = stringResource(R.string.minimize),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.xxs))

                    IconButton(
                        onClick = onCloseOverlay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Yüzen Erişimi Kapat",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.s))

            // Search input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ShapeTokens.InputRadius)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = ShapeTokens.InputRadius
                    )
                    .padding(horizontal = Spacing.s, vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    CompactChip(
                        label = stringResource(R.string.all),
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                item {
                    CompactChip(
                        label = stringResource(R.string.category_accounts),
                        isSelected = selectedCategory == Category.ACCOUNT.id,
                        onClick = { selectedCategory = Category.ACCOUNT.id }
                    )
                }
                item {
                    CompactChip(
                        label = stringResource(R.string.category_financial),
                        isSelected = selectedCategory == Category.FINANCIAL.id,
                        onClick = { selectedCategory = Category.FINANCIAL.id }
                    )
                }
                item {
                    CompactChip(
                        label = stringResource(R.string.category_personal),
                        isSelected = selectedCategory == Category.PERSONAL.id,
                        onClick = { selectedCategory = Category.PERSONAL.id }
                    )
                }
                item {
                    CompactChip(
                        label = stringResource(R.string.category_custom),
                        isSelected = selectedCategory == Category.CUSTOM.id,
                        onClick = { selectedCategory = Category.CUSTOM.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Field List
            if (filteredFields.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_entries),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    items(filteredFields, key = { "${it.entryId}_${it.label}" }) { item ->
                        OverlayFieldCard(
                            item = item,
                            onCopy = {
                                val readableLabel = formatFieldLabel(item.label)
                                ClipboardHelper.copyToClipboard(
                                    context = context,
                                    label = readableLabel,
                                    text = item.value,
                                    isSensitive = item.isSensitive
                                )

                                Toast.makeText(
                                    context,
                                    context.getString(R.string.copied_item, readableLabel),
                                    Toast.LENGTH_SHORT
                                ).show()

                                scope.launch {
                                    delay(150)
                                    onCopiedAndMinimize()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        label = "chip_bg"
    )
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(ShapeTokens.ChipRadius)
            .background(bgColor)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.ChipRadius
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.s, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun OverlayFieldCard(
    item: FlatField,
    onCopy: () -> Unit
) {
    var isRevealed by remember { mutableStateOf(!item.isSensitive) }

    val category = Category.fromId(item.category)
    val (categoryIcon, categoryTint) = when (category) {
        Category.ACCOUNT -> Icons.Rounded.Lock to CategoryLoginsTint
        Category.FINANCIAL -> Icons.Rounded.CreditCard to CategoryCardsTint
        Category.PERSONAL -> Icons.Rounded.AccountCircle to CategoryIdentityTint
        Category.CUSTOM -> Icons.Rounded.Description to CategoryNotesTint
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .padding(horizontal = Spacing.s, vertical = Spacing.xs)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(ShapeTokens.BadgeRadius)
                    .background(categoryTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.xs))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.entryTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatFieldLabel(item.label)}: " + if (item.isSensitive && !isRevealed) "••••••••••••" else item.value,
                    style = if (item.isSensitive && !isRevealed) MonospaceSecretStyle.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.isSensitive) {
                IconButton(
                    onClick = { isRevealed = !isRevealed },
                    modifier = Modifier.size(Spacing.touchTargetMin)
                ) {
                    Icon(
                        imageVector = if (isRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            CopyButton(
                onClick = onCopy,
                contentDescription = "${item.entryTitle} ${item.label} kopyala"
            )
        }
    }
}
