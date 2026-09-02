package com.example.datamanager.ui.component.templates

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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.datamanager.R
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.util.PhoneNumberVisualTransformation

@Composable
fun AddressTemplateEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    fields: List<FieldItem>,
    onFieldsChange: (List<FieldItem>) -> Unit,
    titleError: Boolean,
    modifier: Modifier = Modifier
) {
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

    val address = getFieldValue("address")
    val city = getFieldValue("city")
    val district = getFieldValue("district")
    val neighborhood = getFieldValue("neighborhood")
    val postalCode = getFieldValue("postal_code")
    val recipientName = getFieldValue("recipient_name")
    val phone = getFieldValue("phone")
    val notes = getFieldValue("notes")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Title / Nickname
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.address_title_label)) },
            placeholder = { Text(stringResource(R.string.address_title_placeholder)) },
            singleLine = true,
            isError = titleError,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Open Street Address (Text Area)
        OutlinedTextField(
            value = address,
            onValueChange = { updateField("address", it, FieldType.MULTILINE, isSensitive = false) },
            label = { Text(stringResource(R.string.address_line_label)) },
            placeholder = { Text("") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Neighborhood & District Side-by-Side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            OutlinedTextField(
                value = neighborhood,
                onValueChange = { updateField("neighborhood", it, FieldType.TEXT, isSensitive = false) },
                label = { Text(stringResource(R.string.neighborhood_label)) },
                placeholder = { Text("") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            OutlinedTextField(
                value = district,
                onValueChange = { updateField("district", it, FieldType.TEXT, isSensitive = false) },
                label = { Text(stringResource(R.string.district_label)) },
                placeholder = { Text("") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // City & Postal Code Side-by-Side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = { updateField("city", it, FieldType.TEXT, isSensitive = false) },
                label = { Text(stringResource(R.string.city_label)) },
                placeholder = { Text("") },
                singleLine = true,
                modifier = Modifier.weight(1.2f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            OutlinedTextField(
                value = postalCode,
                onValueChange = { updateField("postal_code", it, FieldType.NUMBER, isSensitive = false) },
                label = { Text(stringResource(R.string.postal_code_label)) },
                placeholder = { Text("34000") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.8f),
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
                    value = recipientName,
                    onValueChange = { updateField("recipient_name", it, FieldType.TEXT, isSensitive = false) },
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

                OutlinedTextField(
                    value = phone,
                    onValueChange = { updateField("phone", it, FieldType.NUMBER, isSensitive = false) },
                    label = { Text(stringResource(R.string.field_phone)) },
                    placeholder = { Text("05XX XXX XX XX") },
                    singleLine = true,
                    visualTransformation = PhoneNumberVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                    label = { Text(stringResource(R.string.address_note_label)) },
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
