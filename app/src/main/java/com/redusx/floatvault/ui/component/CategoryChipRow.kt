package com.redusx.floatvault.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.redusx.floatvault.R
import com.redusx.floatvault.ui.theme.CategoryCardsTint
import com.redusx.floatvault.ui.theme.CategoryIdentityTint
import com.redusx.floatvault.ui.theme.CategoryLoginsTint
import com.redusx.floatvault.ui.theme.CategoryNotesTint
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing

data class CategoryFilterItem(
    val id: String?,
    val labelRes: Int,
    val icon: ImageVector,
    val count: Int
)

@Composable
fun CategoryChipRow(
    selectedCategoryId: String?,
    onSelectCategory: (String?) -> Unit,
    categoryCounts: Map<String, Int>,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val categories = remember(categoryCounts, totalCount) {
        listOf(
            CategoryFilterItem(
                id = null,
                labelRes = R.string.all,
                icon = Icons.Rounded.GridView,
                count = totalCount
            ),
            CategoryFilterItem(
                id = "account",
                labelRes = R.string.category_accounts,
                icon = Icons.Rounded.Lock,
                count = categoryCounts["account"] ?: 0
            ),
            CategoryFilterItem(
                id = "financial",
                labelRes = R.string.category_financial,
                icon = Icons.Rounded.CreditCard,
                count = categoryCounts["financial"] ?: 0
            ),
            CategoryFilterItem(
                id = "personal",
                labelRes = R.string.category_personal,
                icon = Icons.Rounded.AccountCircle,
                count = categoryCounts["personal"] ?: 0
            ),
            CategoryFilterItem(
                id = "custom",
                labelRes = R.string.category_custom,
                icon = Icons.Rounded.Description,
                count = categoryCounts["custom"] ?: 0
            )
        )
    }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Spacing.m),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        items(categories, key = { it.id ?: "all" }) { item ->
            val isSelected = selectedCategoryId == item.id
            CategoryFilterChip(
                item = item,
                isSelected = isSelected,
                onClick = { onSelectCategory(item.id) }
            )
        }
    }
}

@Composable
fun CategoryFilterChip(
    item: CategoryFilterItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val iconTint = when (item.id) {
        "account" -> CategoryLoginsTint
        "financial" -> CategoryCardsTint
        "personal" -> CategoryIdentityTint
        "custom" -> CategoryNotesTint
        else -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(Spacing.touchTargetMin)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .height(36.dp)
                .clip(ShapeTokens.ChipRadius)
                .background(containerColor)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = ShapeTokens.ChipRadius
                )
                .padding(horizontal = Spacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(Spacing.xxs))

            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )

            if (item.count > 0) {
                Spacer(modifier = Modifier.width(Spacing.xxs))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
