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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditCard
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.ui.theme.CategoryCardsTint
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.util.CardNumberVisualTransformation
import com.example.datamanager.util.ExpiryDateVisualTransformation

@Composable
fun CardTemplateEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    fields: List<FieldItem>,
    onFieldsChange: (List<FieldItem>) -> Unit,
    titleError: Boolean,
    modifier: Modifier = Modifier
) {
    var showAdditional by remember { mutableStateOf(false) }
    var isCvvRevealed by remember { mutableStateOf(false) }

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

    val cardNumber = getFieldValue("card_number")
    val expiryDate = getFieldValue("expiry_date")
    val cvv = getFieldValue("cvv")
    val cardHolder = getFieldValue("card_holder")
    val bankName = getFieldValue("bank_name")
    val notes = getFieldValue("notes")

    val cardBrand = remember(cardNumber) {
        when {
            cardNumber.startsWith("4") -> "VISA"
            cardNumber.startsWith("5") || cardNumber.startsWith("2") -> "MASTERCARD"
            cardNumber.startsWith("9792") -> "TROY"
            cardNumber.startsWith("34") || cardNumber.startsWith("37") -> "AMEX"
            else -> "KART"
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Physical Card Preview Widget
        PhysicalCardPreview(
            title = title.ifEmpty { "Kredi / Banka Kartı" },
            cardNumber = cardNumber,
            expiryDate = expiryDate,
            cvv = cvv,
            cardHolder = cardHolder,
            cardBrand = cardBrand
        )

        // Essential Fields
        Text(
            text = "KART BİLGİLERİ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Kart Başlığı / Takma Ad") },
            placeholder = { Text("örn. Garanti Bonus, Maaş Kartı") },
            singleLine = true,
            isError = titleError,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        OutlinedTextField(
            value = cardNumber,
            onValueChange = { if (it.length <= 19) updateField("card_number", it.filter { c -> c.isDigit() }, FieldType.CARD_NUMBER, isSensitive = true) },
            label = { Text("Kart Numarası") },
            placeholder = { Text("4242 4242 4242 4242") },
            singleLine = true,
            visualTransformation = CardNumberVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { if (it.length <= 4) updateField("expiry_date", it.filter { c -> c.isDigit() }, FieldType.DATE) },
                label = { Text("Son Kullanma (AA/YY)") },
                placeholder = { Text("12 / 28") },
                singleLine = true,
                visualTransformation = ExpiryDateVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            OutlinedTextField(
                value = cvv,
                onValueChange = { if (it.length <= 4) updateField("cvv", it.filter { c -> c.isDigit() }, FieldType.NUMBER, isSensitive = true) },
                label = { Text("CVV / CVC") },
                placeholder = { Text("•••") },
                singleLine = true,
                visualTransformation = if (isCvvRevealed) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(onClick = { isCvvRevealed = !isCvvRevealed }) {
                        Icon(
                            imageVector = if (isCvvRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (isCvvRevealed) "Gizle" else "Göster",
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

        OutlinedTextField(
            value = cardHolder,
            onValueChange = { updateField("card_holder", it.uppercase(), FieldType.TEXT) },
            label = { Text("Kart Üzerindeki İsim") },
            placeholder = { Text("AHMET YILMAZ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeTokens.InputRadius,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

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
                    text = if (showAdditional) "Ek Bilgileri Gizle" else "＋ Ek Bilgiler Ekle (Banka, Not)",
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
                    value = bankName,
                    onValueChange = { updateField("bank_name", it, FieldType.TEXT) },
                    label = { Text("Banka Adı") },
                    placeholder = { Text("örn. Garanti BBVA, İş Bankası") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeTokens.InputRadius,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { updateField("notes", it, FieldType.MULTILINE) },
                    label = { Text("Kart Notu / Güvenlik İpuçları") },
                    placeholder = { Text("örn. Yurt dışı alışverişe açık, sanal kart") },
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

@Composable
fun PhysicalCardPreview(
    title: String,
    cardNumber: String,
    expiryDate: String,
    cvv: String,
    cardHolder: String,
    cardBrand: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(12.dp, ShapeTokens.CardRadius)
            .clip(ShapeTokens.CardRadius)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E2638),
                        Color(0xFF131722)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0x33ADC6FF),
                shape = ShapeTokens.CardRadius
            )
            .padding(Spacing.m)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CreditCard,
                        contentDescription = null,
                        tint = CategoryCardsTint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = cardBrand,
                    style = MaterialTheme.typography.labelLarge,
                    color = CategoryCardsTint,
                    fontWeight = FontWeight.Bold
                )
            }

            // Card Number
            Text(
                text = formatCardNumberDisplay(cardNumber),
                style = MonospaceSecretStyle.copy(fontSize = 18.sp, letterSpacing = 2.sp),
                color = Color.White
            )

            // Card Bottom Row: Expiry, CVV, Cardholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "KART SAHİBİ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color(0xFF8E9099)
                    )
                    Text(
                        text = cardHolder.ifEmpty { "İSİM SOYİSİM" },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SKT",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color(0xFF8E9099)
                        )
                        Text(
                            text = formatExpiryDisplay(expiryDate),
                            style = MonospaceSecretStyle.copy(fontSize = 13.sp),
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CVV",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color(0xFF8E9099)
                        )
                        Text(
                            text = if (cvv.isNotEmpty()) "•••" else "---",
                            style = MonospaceSecretStyle.copy(fontSize = 13.sp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun formatCardNumberDisplay(raw: String): String {
    if (raw.isEmpty()) return "••••  ••••  ••••  ••••"
    val padded = raw.padEnd(16, '•')
    return "${padded.substring(0, 4)}  ${padded.substring(4, 8)}  ${padded.substring(8, 12)}  ${padded.substring(12, 16)}"
}

fun formatExpiryDisplay(raw: String): String {
    if (raw.isEmpty()) return "MM/YY"
    return if (raw.length >= 2) "${raw.substring(0, 2)}/${raw.substring(2)}" else raw
}
