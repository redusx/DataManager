package com.redusx.floatvault.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redusx.floatvault.R
import com.redusx.floatvault.ui.theme.MonospaceSecretStyle
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun SensitiveField(
    label: String,
    value: String,
    isSensitive: Boolean,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    var isRevealed by remember { mutableStateOf(!isSensitive) }

    // 30-second auto-remask countdown
    LaunchedEffect(isRevealed) {
        if (isRevealed && isSensitive) {
            delay(30_000) // 30 seconds auto-remask
            isRevealed = false
        }
    }

    val isMultiline = !compact && (
        value.contains("\n") ||
        label.contains("not", ignoreCase = true) ||
        label.contains("note", ignoreCase = true) ||
        label.contains("adres", ignoreCase = true) ||
        label.contains("address", ignoreCase = true) ||
        value.length > 40
    )

    val displayValue = remember(value, isSensitive, isRevealed) {
        if (!isSensitive || isRevealed) {
            value
        } else {
            if (value.contains("\n")) {
                value.lines().joinToString("\n") { "••••••••" }
            } else {
                when {
                    value.length <= 4 -> "•".repeat(value.length.coerceAtLeast(3))
                    value.length in 5..8 -> "••••"
                    else -> "••••••••"
                }
            }
        }
    }

    val currentLocale = remember {
        if (java.util.Locale.getDefault().language == "tr") java.util.Locale("tr", "TR") else java.util.Locale.getDefault()
    }
    val upperLabel = remember(label, currentLocale) {
        label.uppercase(currentLocale)
    }

    val buttonSize = if (compact) 30.dp else 38.dp
    val iconSize = if (compact) 15.dp else 18.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = if (compact) 56.dp else 64.dp)
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .padding(
                horizontal = if (compact) 10.dp else Spacing.m,
                vertical = if (compact) 8.dp else Spacing.s
            ),
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
                    text = upperLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = if (compact) 10.sp else 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                val textStyle = if (isSensitive && !isRevealed) {
                    MonospaceSecretStyle.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (compact) 14.sp else 16.sp,
                        lineHeight = if (compact) 18.sp else 22.sp
                    )
                } else if (isSensitive) {
                    MonospaceSecretStyle.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = if (compact) 14.sp else 16.sp,
                        lineHeight = if (compact) 18.sp else 22.sp
                    )
                } else {
                    MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (compact) 14.sp else 16.sp,
                        lineHeight = if (compact) 18.sp else 22.sp
                    )
                }

                if (isMultiline) {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 115.dp) // Expands up to 5 lines (~22sp line height), scrollable if more
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = displayValue,
                            style = textStyle
                        )
                    }
                } else {
                    Text(
                        text = displayValue,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(if (compact) 4.dp else Spacing.xs))

            // Reveal/Hide Toggle Button
            if (isSensitive) {
                Box(
                    modifier = Modifier
                        .padding(top = if (isMultiline) 2.dp else 0.dp)
                        .size(buttonSize)
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
                        modifier = Modifier.size(iconSize)
                    )
                }
                Spacer(modifier = Modifier.width(if (compact) 4.dp else Spacing.xs))
            }

            // Quick Copy Action
            CopyButton(
                onClick = { onCopy(value) },
                contentDescription = stringResource(R.string.copied_item, label),
                compact = compact,
                modifier = if (isMultiline) Modifier.padding(top = 2.dp) else Modifier
            )
        }
    }
}
