package com.example.datamanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun SensitiveField(
    label: String,
    value: String,
    isSensitive: Boolean,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRevealed by remember { mutableStateOf(!isSensitive) }

    // 30-second auto-remask countdown
    LaunchedEffect(isRevealed) {
        if (isRevealed && isSensitive) {
            delay(30_000) // 30 seconds auto-remask
            isRevealed = false
        }
    }

    val displayValue = remember(value, isSensitive, isRevealed) {
        if (!isSensitive || isRevealed) {
            value
        } else {
            "••••••••••••"
        }
    }

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
            .padding(horizontal = Spacing.m, vertical = Spacing.s)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.xxs))

                Text(
                    text = displayValue,
                    style = if (isSensitive && !isRevealed) {
                        MonospaceSecretStyle.copy(color = MaterialTheme.colorScheme.onSurface)
                    } else if (isSensitive) {
                        MonospaceSecretStyle.copy(color = MaterialTheme.colorScheme.primary)
                    } else {
                        MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Spacing.xs))

            // Reveal/Hide Toggle Button
            if (isSensitive) {
                IconButton(
                    onClick = { isRevealed = !isRevealed },
                    modifier = Modifier.size(Spacing.touchTargetMin)
                ) {
                    Icon(
                        imageVector = if (isRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (isRevealed) "Gizle" else "Göster",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Quick Copy Action
            CopyButton(
                onClick = { onCopy(value) },
                contentDescription = "$label kopyala"
            )
        }
    }
}
