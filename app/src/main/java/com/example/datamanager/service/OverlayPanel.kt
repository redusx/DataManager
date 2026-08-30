package com.example.datamanager.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datamanager.R
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.ui.theme.AccountGradientStart
import com.example.datamanager.ui.theme.CustomGradientStart
import com.example.datamanager.ui.theme.FinancialGradientStart
import com.example.datamanager.ui.theme.PersonalGradientStart
import com.example.datamanager.ui.theme.ShieldBlue
import com.example.datamanager.ui.theme.SuccessGreen
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
    onCopiedAndMinimize: () -> Unit = onMinimize,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gson = remember { Gson() }
    val fieldListType = remember { object : TypeToken<List<FieldItem>>() {}.type }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var lastCopiedLabel by remember { mutableStateOf<String?>(null) }
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
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ShieldBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = ShieldBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DataManager",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenMainApp,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Launch,
                            contentDescription = stringResource(R.string.open_app),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = stringResource(R.string.minimize),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    CategoryChip(
                        label = stringResource(R.string.all),
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                item {
                    CategoryChip(
                        label = stringResource(R.string.category_personal),
                        color = PersonalGradientStart,
                        isSelected = selectedCategory == Category.PERSONAL.id,
                        onClick = { selectedCategory = Category.PERSONAL.id }
                    )
                }
                item {
                    CategoryChip(
                        label = stringResource(R.string.category_financial),
                        color = FinancialGradientStart,
                        isSelected = selectedCategory == Category.FINANCIAL.id,
                        onClick = { selectedCategory = Category.FINANCIAL.id }
                    )
                }
                item {
                    CategoryChip(
                        label = stringResource(R.string.category_accounts),
                        color = AccountGradientStart,
                        isSelected = selectedCategory == Category.ACCOUNT.id,
                        onClick = { selectedCategory = Category.ACCOUNT.id }
                    )
                }
                item {
                    CategoryChip(
                        label = stringResource(R.string.category_custom),
                        color = CustomGradientStart,
                        isSelected = selectedCategory == Category.CUSTOM.id,
                        onClick = { selectedCategory = Category.CUSTOM.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Field List
            if (filteredFields.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
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
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredFields, key = { "${it.entryId}_${it.label}" }) { item ->
                        OverlayFieldCard(
                            item = item,
                            isRecentlyCopied = lastCopiedLabel == "${item.entryId}_${item.label}",
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(item.label, item.value)
                                clipboard.setPrimaryClip(clip)

                                Toast.makeText(
                                    context,
                                    context.getString(R.string.copied_item, item.label),
                                    Toast.LENGTH_SHORT
                                ).show()

                                lastCopiedLabel = "${item.entryId}_${item.label}"
                                scope.launch {
                                    delay(200)
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
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        label = "chip_bg"
    )
    val textColor = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun OverlayFieldCard(
    item: FlatField,
    isRecentlyCopied: Boolean,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRevealed by remember { mutableStateOf(false) }

    val displayValue = when {
        item.isSensitive && !isRevealed -> "••••••••"
        else -> item.value
    }

    val categoryColor = when (Category.fromId(item.category)) {
        Category.PERSONAL -> PersonalGradientStart
        Category.FINANCIAL -> FinancialGradientStart
        Category.ACCOUNT -> AccountGradientStart
        Category.CUSTOM -> CustomGradientStart
    }

    val cardBg by animateColorAsState(
        targetValue = if (isRecentlyCopied) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        label = "card_bg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .clickable(onClick = onCopy)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category indicator bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(categoryColor)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.entryTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = " • ${item.label}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = categoryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (item.isSensitive) {
                IconButton(
                    onClick = { isRevealed = !isRevealed },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isRevealed) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecentlyCopied) SuccessGreen.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecentlyCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    tint = if (isRecentlyCopied) SuccessGreen else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
