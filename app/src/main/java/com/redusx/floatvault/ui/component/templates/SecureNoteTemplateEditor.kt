package com.redusx.floatvault.ui.component.templates

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.redusx.floatvault.R
import com.redusx.floatvault.data.model.FieldItem
import com.redusx.floatvault.data.model.FieldType
import com.redusx.floatvault.ui.theme.MonospaceSecretStyle
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing

@Composable
fun SecureNoteTemplateEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    fields: List<FieldItem>,
    onFieldsChange: (List<FieldItem>) -> Unit,
    titleError: Boolean,
    modifier: Modifier = Modifier
) {
    var isMonospace by remember { mutableStateOf(false) }

    fun getFieldValue(key: String): String = fields.firstOrNull { it.key == key }?.value ?: ""

    fun updateField(key: String, value: String, type: FieldType = FieldType.TEXT, isSensitive: Boolean = false) {
        val list = fields.toMutableList()
        val index = list.indexOfFirst { it.key == key }
        if (index >= 0) {
            list[index] = list[index].copy(value = value, isSensitive = isSensitive)
        } else {
            list.add(FieldItem(key = key, value = value, type = type, isSensitive = isSensitive))
        }
        onFieldsChange(list)
    }

    val content = getFieldValue("note_content")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.note_title_label)) },
            placeholder = { Text(stringResource(R.string.note_title_placeholder)) },
            singleLine = true,
            isError = titleError,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Options Row (Monospace Toggle)
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
                .padding(horizontal = Spacing.m, vertical = Spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.monospace_font_toggle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isMonospace,
                    onCheckedChange = { isMonospace = it }
                )
            }
        }

        // Expansive Note Surface
        OutlinedTextField(
            value = content,
            onValueChange = { updateField("note_content", it, FieldType.MULTILINE, isSensitive = true) },
            label = { Text(stringResource(R.string.note_content_label)) },
            placeholder = { Text("") },
            minLines = 8,
            textStyle = if (isMonospace) MonospaceSecretStyle else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}
