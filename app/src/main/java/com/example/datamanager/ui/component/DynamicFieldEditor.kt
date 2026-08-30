package com.example.datamanager.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.datamanager.R
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add field",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.add_field))
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
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp)
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
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Remove button
            AnimatedVisibility(visible = canRemove, enter = fadeIn(), exit = fadeOut()) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Field value
        OutlinedTextField(
            value = field.value,
            onValueChange = { onFieldChanged(field.copy(value = it)) },
            label = { Text(stringResource(R.string.field_value)) },
            singleLine = true,
            visualTransformation = if (field.isSensitive) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { showTypeMenu = true },
                    shape = RoundedCornerShape(8.dp)
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
                    imageVector = if (field.isSensitive) Icons.Filled.Lock else Icons.Filled.LockOpen,
                    contentDescription = "Sensitive",
                    tint = if (field.isSensitive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.sensitive),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = field.isSensitive,
                    onCheckedChange = { onFieldChanged(field.copy(isSensitive = it)) }
                )
            }
        }
    }
}
