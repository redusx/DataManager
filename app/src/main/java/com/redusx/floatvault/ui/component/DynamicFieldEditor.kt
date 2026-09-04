package com.redusx.floatvault.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.redusx.floatvault.R
import com.redusx.floatvault.data.model.FieldItem
import com.redusx.floatvault.data.model.FieldType
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing

@Composable
fun DynamicFieldEditor(
    fields: List<FieldItem>,
    onFieldsChanged: (List<FieldItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.s)
    ) {
        fields.forEachIndexed { index, field ->
            FieldEditorRow(
                field = field,
                onFieldChanged = { updated ->
                    val newList = fields.toMutableList()
                    newList[index] = updated
                    onFieldsChanged(newList)
                },
                onRemove = {
                    val newList = fields.toMutableList()
                    newList.removeAt(index)
                    onFieldsChanged(newList)
                },
                canRemove = fields.size > 1
            )
        }

        OutlinedButton(
            onClick = {
                val newList = fields.toMutableList()
                newList.add(FieldItem("", "", FieldType.TEXT))
                onFieldsChanged(newList)
            },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = ShapeTokens.ButtonRadius
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Alan ekle",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = stringResource(R.string.add_field),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun FieldEditorRow(
    field: FieldItem,
    onFieldChanged: (FieldItem) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
    modifier: Modifier = Modifier
) {
    var showTypeMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShapeTokens.CardRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = ShapeTokens.CardRadius
            )
            .padding(Spacing.s)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Field label
            OutlinedTextField(
                value = field.key,
                onValueChange = { onFieldChanged(field.copy(key = it)) },
                label = { Text(stringResource(R.string.field_label)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.width(Spacing.xs))

            // Remove button
            AnimatedVisibility(visible = canRemove, enter = fadeIn(), exit = fadeOut()) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(Spacing.touchTargetMin)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Kaldır",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        // Field value
        OutlinedTextField(
            value = field.value,
            onValueChange = { onFieldChanged(field.copy(value = it)) },
            label = { Text(stringResource(R.string.field_value)) },
            singleLine = true,
            visualTransformation = if (field.isSensitive) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { showTypeMenu = true },
                    shape = ShapeTokens.ButtonRadius
                ) {
                    Text(
                        text = field.type.name,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                DropdownMenu(
                    expanded = showTypeMenu,
                    onDismissRequest = { showTypeMenu = false }
                ) {
                    FieldType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                val sensitive = type == FieldType.PASSWORD || type == FieldType.CARD_NUMBER
                                onFieldChanged(field.copy(type = type, isSensitive = sensitive))
                                showTypeMenu = false
                            }
                        )
                    }
                }
            }

            // Sensitive toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (field.isSensitive) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                    contentDescription = "Hassas",
                    tint = if (field.isSensitive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xxs))
                Text(
                    text = stringResource(R.string.sensitive),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(Spacing.xxs))
                Switch(
                    checked = field.isSensitive,
                    onCheckedChange = { onFieldChanged(field.copy(isSensitive = it)) }
                )
            }
        }
    }
}
