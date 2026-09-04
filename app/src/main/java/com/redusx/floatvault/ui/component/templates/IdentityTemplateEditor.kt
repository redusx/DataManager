package com.redusx.floatvault.ui.component.templates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.redusx.floatvault.R
import com.redusx.floatvault.data.model.FieldItem
import com.redusx.floatvault.data.model.FieldType
import com.redusx.floatvault.ui.theme.MonospaceSecretStyle
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing

@Composable
fun IdentityTemplateEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    fields: List<FieldItem>,
    onFieldsChange: (List<FieldItem>) -> Unit,
    titleError: Boolean,
    modifier: Modifier = Modifier
) {
    var isIdRevealed by remember { mutableStateOf(false) }
    var isSerialRevealed by remember { mutableStateOf(false) }
    var showAdditional by remember { mutableStateOf(false) }

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

    val idNumber = getFieldValue("id_number")
    val fullName = getFieldValue("full_name")
    val birthDate = getFieldValue("birth_date")
    val serialNumber = getFieldValue("serial_number")
    val expiryDate = getFieldValue("expiry_date")
    val notes = getFieldValue("notes")

    val docTypeItems = listOf(
        R.string.doc_tc_kimlik,
        R.string.doc_passport,
        R.string.doc_driver_license,
        R.string.doc_other
    )
    val docTypeNames = docTypeItems.map { stringResource(it) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Document Type Quick-Selector
        Column {
            Text(
                text = stringResource(R.string.identity_doc_type),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                docTypeNames.forEach { typeName ->
                    val isSelected = title.contains(typeName, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(ShapeTokens.ChipRadius)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainer
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = ShapeTokens.ChipRadius
                            )
                            .clickable {
                                if (title.isEmpty() || docTypeNames.contains(title)) {
                                    onTitleChange(typeName)
                                }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = typeName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.identity_title_label)) },
            placeholder = { Text(stringResource(R.string.identity_title_placeholder)) },
            singleLine = true,
            isError = titleError,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // National ID / Passport Number (Sensitive)
        OutlinedTextField(
            value = idNumber,
            onValueChange = { updateField("id_number", it, FieldType.NUMBER, isSensitive = true) },
            label = { Text(stringResource(R.string.identity_id_label)) },
            placeholder = { Text(stringResource(R.string.identity_id_placeholder)) },
            singleLine = true,
            visualTransformation = if (isIdRevealed) VisualTransformation.None else PasswordVisualTransformation(),
            textStyle = if (!isIdRevealed) MonospaceSecretStyle else MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                IconButton(onClick = { isIdRevealed = !isIdRevealed }) {
                    Icon(
                        imageVector = if (isIdRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (isIdRevealed) stringResource(R.string.hide_value) else stringResource(R.string.reveal_value),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Full Name (Not sensitive - name)
        OutlinedTextField(
            value = fullName,
            onValueChange = { updateField("full_name", it, FieldType.TEXT, isSensitive = false) },
            label = { Text(stringResource(R.string.fullname_label)) },
            placeholder = { Text("Ahmet Yılmaz") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Birth Date & Serial Number Side-by-Side (Both Sensitive)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            OutlinedTextField(
                value = birthDate,
                onValueChange = { updateField("birth_date", it, FieldType.DATE, isSensitive = true) },
                label = { Text(stringResource(R.string.birthdate_label)) },
                placeholder = { Text("DD.MM.YYYY") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            OutlinedTextField(
                value = serialNumber,
                onValueChange = {
                    val loc = if (java.util.Locale.getDefault().language == "tr") java.util.Locale("tr", "TR") else java.util.Locale.getDefault()
                    updateField("serial_number", it.uppercase(loc), FieldType.TEXT, isSensitive = true)
                },
                label = { Text(stringResource(R.string.serial_number_label)) },
                placeholder = { Text("A12B34567") },
                singleLine = true,
                visualTransformation = if (isSerialRevealed) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                trailingIcon = {
                    IconButton(onClick = { isSerialRevealed = !isSerialRevealed }) {
                        Icon(
                            imageVector = if (isSerialRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (isSerialRevealed) stringResource(R.string.hide_value) else stringResource(R.string.reveal_value),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // Progressive Disclosure: Additional Fields
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
                .clickable { showAdditional = !showAdditional }
                .padding(Spacing.m)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (showAdditional) stringResource(R.string.hide_additional_info) else stringResource(R.string.add_identity_additional_info),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (showAdditional) Icons.Rounded.Remove else Icons.Rounded.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = showAdditional,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { updateField("expiry_date", it, FieldType.DATE, isSensitive = true) },
                    label = { Text(stringResource(R.string.identity_expiry_label)) },
                    placeholder = { Text("DD.MM.YYYY") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeTokens.InputRadius,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { updateField("notes", it, FieldType.MULTILINE, isSensitive = false) },
                    label = { Text(stringResource(R.string.identity_note_label)) },
                    placeholder = { Text("") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeTokens.InputRadius,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
        }
    }
}
