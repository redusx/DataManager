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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Casino
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.data.model.PopularServices
import com.example.datamanager.data.model.ServicePreset
import com.example.datamanager.ui.theme.DarkSuccess
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.ui.theme.WarningOrange
import com.example.datamanager.util.PasswordGenerator

@Composable
fun LoginTemplateEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    fields: List<FieldItem>,
    onFieldsChange: (List<FieldItem>) -> Unit,
    titleError: Boolean,
    modifier: Modifier = Modifier
) {
    var isPasswordRevealed by remember { mutableStateOf(false) }
    var showAdditional by remember { mutableStateOf(false) }

    fun getFieldValue(key: String): String = fields.firstOrNull { it.key == key }?.value ?: ""

    fun updateField(key: String, value: String, type: FieldType = FieldType.TEXT, isSensitive: Boolean = false) {
        val list = fields.toMutableList()
        val index = list.indexOfFirst { it.key == key }
        if (index >= 0) {
            list[index] = list[index].copy(value = value)
        } else {
            list.add(FieldItem(key = key, value = value, type = type, isSensitive = isSensitive))
        }
        onFieldsChange(list)
    }

    val username = getFieldValue("username")
    val password = getFieldValue("password")
    val website = getFieldValue("website")
    val notes = getFieldValue("notes")

    val passwordStrength = remember(password) {
        PasswordGenerator.calculateStrength(password)
    }

    fun applyPreset(preset: ServicePreset) {
        onTitleChange(preset.name)
        updateField("website", preset.domain, FieldType.TEXT)
        if (username.isEmpty() && preset.defaultUsernameHint.isNotEmpty()) {
            // Leave blank or guide
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Popular Service Quick-Select
        Column {
            Text(
                text = "HIZLI SERVİS SEÇİMİ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(PopularServices.all, key = { it.name }) { preset ->
                    val isSelected = title.equals(preset.name, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(ShapeTokens.ChipRadius)
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = ShapeTokens.ChipRadius
                            )
                            .clickable { applyPreset(preset) }
                            .padding(horizontal = Spacing.s, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Title / Service
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Hesap / Servis Adı") },
            placeholder = { Text("örn. Google, GitHub, Netflix") },
            singleLine = true,
            isError = titleError,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Username / Email
        OutlinedTextField(
            value = username,
            onValueChange = { updateField("username", it, FieldType.TEXT) },
            label = { Text("Kullanıcı Adı veya E-Posta") },
            placeholder = { Text("ornek@gmail.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Password with Generator + Strength Meter
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = { updateField("password", it, FieldType.PASSWORD, isSensitive = true) },
                label = { Text("Şifre") },
                singleLine = true,
                visualTransformation = if (isPasswordRevealed) VisualTransformation.None else PasswordVisualTransformation(),
                textStyle = if (!isPasswordRevealed) MonospaceSecretStyle else MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                val generated = PasswordGenerator.generate(length = 16)
                                updateField("password", generated, FieldType.PASSWORD, isSensitive = true)
                                isPasswordRevealed = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Casino,
                                contentDescription = "Güçlü Şifre Üret",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(onClick = { isPasswordRevealed = !isPasswordRevealed }) {
                            Icon(
                                imageVector = if (isPasswordRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isPasswordRevealed) "Gizle" else "Göster",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (strengthText, strengthColor, strengthRatio) = when (passwordStrength) {
                        PasswordGenerator.Strength.STRONG -> Triple("Güçlü", DarkSuccess, 1f)
                        PasswordGenerator.Strength.MEDIUM -> Triple("Orta", WarningOrange, 0.6f)
                        PasswordGenerator.Strength.WEAK -> Triple("Zayıf", MaterialTheme.colorScheme.error, 0.3f)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(strengthRatio)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(strengthColor)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.s))

                    Text(
                        text = "$strengthText (${password.length} hane)",
                        style = MaterialTheme.typography.labelSmall,
                        color = strengthColor
                    )
                }
            }
        }

        // Website URL
        OutlinedTextField(
            value = website,
            onValueChange = { updateField("website", it, FieldType.TEXT) },
            label = { Text("Web Sitesi / URL (İsteğe bağlı)") },
            placeholder = { Text("accounts.google.com") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        // Progressive Disclosure: Additional Notes
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
                    text = if (showAdditional) "Ek Bilgileri Gizle" else "＋ Ek Not ve Güvenlik Bilgisi Ekle",
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
            OutlinedTextField(
                value = notes,
                onValueChange = { updateField("notes", it, FieldType.MULTILINE) },
                label = { Text("Güvenlik Notu / 2FA Kurtarma Kodları") },
                placeholder = { Text("Yedek kodlar, gizli sorular...") },
                minLines = 3,
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
