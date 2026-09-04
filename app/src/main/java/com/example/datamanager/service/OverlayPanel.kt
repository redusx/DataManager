package com.example.datamanager.service

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.semantics.Role
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Launch
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ChevronRight
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
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.data.model.TemplateType
import com.example.datamanager.data.model.isEffectivelySensitive
import com.example.datamanager.ui.component.CopyButton
import com.example.datamanager.ui.theme.CategoryCardsTint
import com.example.datamanager.ui.theme.CategoryIdentityTint
import com.example.datamanager.ui.theme.CategoryLoginsTint
import com.example.datamanager.ui.theme.CategoryNotesTint
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.util.ClipboardHelper
import com.example.datamanager.util.FieldFormatter
import com.example.datamanager.util.FieldFormatter.formatFieldLabel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    var selectedEntry by remember { mutableStateOf<DataEntry?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Filter entries based on search and selected category
    val filteredEntries = remember(entries, searchQuery, selectedCategory) {
        entries.filter { entry ->
            val matchesCategory = selectedCategory == null || entry.category == selectedCategory
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                val inTitle = entry.title.contains(searchQuery, ignoreCase = true)
                val inFields = try {
                    val fields: List<FieldItem> = gson.fromJson(entry.fieldsJson, fieldListType)
                    fields.any { it.key.contains(searchQuery, ignoreCase = true) || it.value.contains(searchQuery, ignoreCase = true) }
                } catch (e: Exception) {
                    false
                }
                inTitle || inFields
            }
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
            // Main Top Header bar (App Logo, Name, and Window Controls)
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
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
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
                            contentDescription = stringResource(R.string.overlay_close),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            AnimatedContent(
                targetState = selectedEntry,
                transitionSpec = {
                    if (targetState != null) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "overlay_navigation"
            ) { currentEntry ->
                if (currentEntry == null) {
                    // Level 1: Entry List (Grouped by Category & Filterable)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Search bar
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

                        // Entry Cards List
                        if (filteredEntries.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
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
                                    .height(240.dp),
                                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                items(filteredEntries, key = { it.id }) { entry ->
                                    OverlayEntryCard(
                                        entry = entry,
                                        gson = gson,
                                        fieldListType = fieldListType,
                                        onClick = { selectedEntry = entry }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Level 2: Selected Entry's Detail & Copyable Fields View
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Parse fields of selected entry
                        val fields: List<FieldItem> = remember(currentEntry) {
                            try {
                                gson.fromJson<List<FieldItem>>(currentEntry.fieldsJson, fieldListType) ?: emptyList()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }

                        // Sub-header with Back button (Replaces Category Chips)
                        val category = Category.fromId(currentEntry.category)
                        val (categoryIcon, categoryTint) = com.example.datamanager.ui.component.getEntryVisuals(category, fields)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ShapeTokens.CardRadius)
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(horizontal = Spacing.xs, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { selectedEntry = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.overlay_back),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(ShapeTokens.BadgeRadius)
                                    .background(categoryTint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = categoryTint,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(Spacing.xs))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentEntry.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        if (fields.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_fields_in_entry),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 220.dp, max = 300.dp),
                                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                val hasCardNumber = fields.any { it.key == "card_number" && it.value.isNotBlank() }
                                if (hasCardNumber) {
                                    val cardNumberField = fields.firstOrNull { it.key == "card_number" && it.value.isNotBlank() }
                                    if (cardNumberField != null) {
                                        item {
                                            val isSensitive = cardNumberField.isEffectivelySensitive(currentEntry.category)
                                            val readableLabel = FieldFormatter.formatFieldLabel(context, cardNumberField.key)
                                            OverlayEntryFieldRow(
                                                field = cardNumberField,
                                                category = currentEntry.category,
                                                entryTitle = currentEntry.title,
                                                onCopy = {
                                                    ClipboardHelper.copyToClipboard(context, readableLabel, cardNumberField.value, isSensitive)
                                                    Toast.makeText(context, context.getString(R.string.copied_item, readableLabel), Toast.LENGTH_SHORT).show()
                                                    scope.launch { delay(150); onCopiedAndMinimize() }
                                                }
                                            )
                                        }
                                    }

                                    val expiryField = fields.firstOrNull { it.key == "expiry_date" && it.value.isNotBlank() }
                                    val cvvField = fields.firstOrNull { it.key == "cvv" && it.value.isNotBlank() }
                                    if (expiryField != null && cvvField != null) {
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(IntrinsicSize.Min),
                                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                            ) {
                                                val expSensitive = expiryField.isEffectivelySensitive(currentEntry.category)
                                                val expLabel = stringResource(R.string.field_expiry_compact)
                                                OverlayEntryFieldRow(
                                                    field = expiryField,
                                                    category = currentEntry.category,
                                                    entryTitle = currentEntry.title,
                                                    overrideLabel = expLabel,
                                                    onCopy = {
                                                        ClipboardHelper.copyToClipboard(context, expLabel, expiryField.value, expSensitive)
                                                        Toast.makeText(context, context.getString(R.string.copied_item, expLabel), Toast.LENGTH_SHORT).show()
                                                        scope.launch { delay(150); onCopiedAndMinimize() }
                                                    },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                )

                                                val cvvSensitive = cvvField.isEffectivelySensitive(currentEntry.category)
                                                val cvvLabel = stringResource(R.string.field_cvv_compact)
                                                OverlayEntryFieldRow(
                                                    field = cvvField,
                                                    category = currentEntry.category,
                                                    entryTitle = currentEntry.title,
                                                    overrideLabel = cvvLabel,
                                                    onCopy = {
                                                        ClipboardHelper.copyToClipboard(context, cvvLabel, cvvField.value, cvvSensitive)
                                                        Toast.makeText(context, context.getString(R.string.copied_item, cvvLabel), Toast.LENGTH_SHORT).show()
                                                        scope.launch { delay(150); onCopiedAndMinimize() }
                                                    },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                )
                                            }
                                        }
                                    } else if (expiryField != null) {
                                        item {
                                            val expSensitive = expiryField.isEffectivelySensitive(currentEntry.category)
                                            val expLabel = FieldFormatter.formatFieldLabel(context, expiryField.key)
                                            OverlayEntryFieldRow(
                                                field = expiryField,
                                                category = currentEntry.category,
                                                entryTitle = currentEntry.title,
                                                onCopy = {
                                                    ClipboardHelper.copyToClipboard(context, expLabel, expiryField.value, expSensitive)
                                                    Toast.makeText(context, context.getString(R.string.copied_item, expLabel), Toast.LENGTH_SHORT).show()
                                                    scope.launch { delay(150); onCopiedAndMinimize() }
                                                }
                                            )
                                        }
                                    } else if (cvvField != null) {
                                        item {
                                            val cvvSensitive = cvvField.isEffectivelySensitive(currentEntry.category)
                                            val cvvLabel = FieldFormatter.formatFieldLabel(context, cvvField.key)
                                            OverlayEntryFieldRow(
                                                field = cvvField,
                                                category = currentEntry.category,
                                                entryTitle = currentEntry.title,
                                                onCopy = {
                                                    ClipboardHelper.copyToClipboard(context, cvvLabel, cvvField.value, cvvSensitive)
                                                    Toast.makeText(context, context.getString(R.string.copied_item, cvvLabel), Toast.LENGTH_SHORT).show()
                                                    scope.launch { delay(150); onCopiedAndMinimize() }
                                                }
                                            )
                                        }
                                    }

                                    val otherFields = fields.filter { it.key !in listOf("card_number", "expiry_date", "cvv") && it.key.isNotBlank() && it.value.isNotBlank() }
                                    items(otherFields) { field ->
                                        val isSensitive = field.isEffectivelySensitive(currentEntry.category)
                                        val readableLabel = FieldFormatter.formatFieldLabel(context, field.key)
                                        OverlayEntryFieldRow(
                                            field = field,
                                            category = currentEntry.category,
                                            entryTitle = currentEntry.title,
                                            onCopy = {
                                                ClipboardHelper.copyToClipboard(context, readableLabel, field.value, isSensitive)
                                                Toast.makeText(context, context.getString(R.string.copied_item, readableLabel), Toast.LENGTH_SHORT).show()
                                                scope.launch { delay(150); onCopiedAndMinimize() }
                                            }
                                        )
                                    }
                                } else {
                                    items(fields) { field ->
                                        if (field.key.isNotBlank() && field.value.isNotBlank()) {
                                            val isSensitive = field.isEffectivelySensitive(currentEntry.category)
                                            val readableLabel = FieldFormatter.formatFieldLabel(context, field.key)
                                            OverlayEntryFieldRow(
                                                field = field,
                                                category = currentEntry.category,
                                                entryTitle = currentEntry.title,
                                                onCopy = {
                                                    ClipboardHelper.copyToClipboard(context, readableLabel, field.value, isSensitive)
                                                    Toast.makeText(context, context.getString(R.string.copied_item, readableLabel), Toast.LENGTH_SHORT).show()
                                                    scope.launch { delay(150); onCopiedAndMinimize() }
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
        }
    }
}

@Composable
private fun OverlayEntryCard(
    entry: DataEntry,
    gson: Gson,
    fieldListType: java.lang.reflect.Type,
    onClick: () -> Unit
) {
    val fields = remember(entry.fieldsJson) {
        try {
            gson.fromJson<List<FieldItem>>(entry.fieldsJson, fieldListType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    val category = Category.fromId(entry.category)
    val (categoryIcon, categoryTint) = com.example.datamanager.ui.component.getEntryVisuals(category, fields)

    val summary = remember(entry) {
        getEntrySummary(entry, gson, fieldListType)
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
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.s, vertical = Spacing.s)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(ShapeTokens.BadgeRadius)
                    .background(categoryTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.s))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (summary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun OverlayEntryFieldRow(
    field: FieldItem,
    category: String,
    entryTitle: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
    overrideLabel: String? = null
) {
    val isSensitive = field.isEffectivelySensitive(category)
    var isRevealed by remember { mutableStateOf(!isSensitive) }
    val context = LocalContext.current
    val readableLabel = overrideLabel ?: FieldFormatter.formatFieldLabel(context, field.key)

    val isMultiline = field.type == FieldType.MULTILINE ||
            field.value.contains("\n") ||
            field.key.contains("note", ignoreCase = true) ||
            field.key.contains("address", ignoreCase = true) ||
            field.key.contains("adres", ignoreCase = true) ||
            field.value.length > 35

    val maskText = remember(field.value) {
        if (field.value.contains("\n")) {
            field.value.lines().joinToString("\n") { "••••••••" }
        } else {
            when {
                field.value.length <= 4 -> "•".repeat(field.value.length.coerceAtLeast(3))
                field.value.length in 5..8 -> "••••"
                else -> "••••••••"
            }
        }
    }

    val displayText = if (isSensitive && !isRevealed) maskText else field.value
    val textStyle = if (isSensitive && !isRevealed) {
        MonospaceSecretStyle.copy(fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
    } else if (isSensitive) {
        MonospaceSecretStyle.copy(fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.primary)
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .padding(horizontal = Spacing.s, vertical = 6.dp),
        contentAlignment = if (isMultiline) Alignment.TopStart else Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = if (isMultiline) Alignment.Top else Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = readableLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))

                if (isMultiline) {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 95.dp) // Expands dynamically up to 5 lines, scrollable if more
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = displayText,
                            style = textStyle
                        )
                    }
                } else {
                    Text(
                        text = displayText,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            if (isSensitive) {
                Box(
                    modifier = Modifier
                        .padding(top = if (isMultiline) 2.dp else 0.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable(
                            role = Role.Button,
                            onClick = { isRevealed = !isRevealed }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (isRevealed) stringResource(R.string.hide_value) else stringResource(R.string.reveal_value),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            CopyButton(
                onClick = onCopy,
                contentDescription = "$entryTitle $readableLabel",
                compact = true,
                modifier = if (isMultiline) Modifier.padding(top = 2.dp) else Modifier
            )
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

private fun getEntrySummary(entry: DataEntry, gson: Gson, type: java.lang.reflect.Type): String {
    return try {
        val fields: List<FieldItem> = gson.fromJson(entry.fieldsJson, type) ?: emptyList()
        val template = TemplateType.detect(entry.category, fields)
        val fieldMap = fields.associate { it.key.lowercase() to it.value }
        when (template) {
            TemplateType.LOGIN -> fieldMap["username"] ?: fieldMap["website"] ?: "${fields.size} alan"
            TemplateType.CARD -> {
                val num = fieldMap["card_number"] ?: ""
                val bank = fieldMap["bank_name"] ?: ""
                if (num.length >= 4) "$bank •••• ${num.takeLast(4)}" else bank.ifEmpty { "${fields.size} alan" }
            }
            TemplateType.BANK_ACCOUNT -> {
                val iban = fieldMap["iban"] ?: ""
                val bank = fieldMap["bank_name"] ?: ""
                if (iban.length >= 4) "$bank •••• ${iban.takeLast(4)}" else bank.ifEmpty { "${fields.size} alan" }
            }
            TemplateType.IDENTITY -> {
                val id = fieldMap["id_number"] ?: fieldMap["tc_no"] ?: ""
                val name = fieldMap["full_name"] ?: ""
                if (id.length >= 4) "$name •••${id.takeLast(4)}" else name.ifEmpty { "${fields.size} alan" }
            }
            TemplateType.ADDRESS -> {
                val city = fieldMap["city"] ?: ""
                val dist = fieldMap["district"] ?: ""
                if (dist.isNotEmpty() && city.isNotEmpty()) "$dist, $city" else (city.ifEmpty { dist }).ifEmpty { "${fields.size} alan" }
            }
            TemplateType.SECURE_NOTE -> {
                val note = fieldMap["note_content"] ?: ""
                note.lineSequence().firstOrNull()?.take(30) ?: "${fields.size} alan"
            }
            TemplateType.CUSTOM -> "${fields.size} alan"
        }
    } catch (e: Exception) {
        "Kayıt detayları"
    }
}
