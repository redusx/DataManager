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
import androidx.compose.material.icons.rounded.AccountBalance
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datamanager.R
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.ui.theme.MonospaceSecretStyle
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.util.CardNumberVisualTransformation
import com.example.datamanager.util.ExpiryDateVisualTransformation
import com.example.datamanager.util.IbanVisualTransformation

private enum class FinancialSubTab {
    CARD,
    BANK_ACCOUNT
}

@Composable
fun CardTemplateEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    fields: List<FieldItem>,
    onFieldsChange: (List<FieldItem>) -> Unit,
    titleError: Boolean,
    modifier: Modifier = Modifier
) {
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

    val initialTab = if (getFieldValue("iban").isNotEmpty() && getFieldValue("card_number").isEmpty()) {
        FinancialSubTab.BANK_ACCOUNT
    } else {
        FinancialSubTab.CARD
    }

    var activeTab by remember { mutableStateOf(initialTab) }
    var showAdditional by remember { mutableStateOf(false) }
    var isCvvRevealed by remember { mutableStateOf(false) }

    val cardNumber = getFieldValue("card_number")
    val expiryDate = getFieldValue("expiry_date")
    val cvv = getFieldValue("cvv")
    val cardHolder = getFieldValue("card_holder").ifEmpty { getFieldValue("cardholder_name") }
    val bankName = getFieldValue("bank_name")
    val iban = getFieldValue("iban")
    val accountHolder = getFieldValue("account_holder").ifEmpty { getFieldValue("cardholder_name") }
    val accountNumber = getFieldValue("account_number")
    val branchCode = getFieldValue("branch_code")
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
        // Financial Sub-Tab Selector (Card vs IBAN)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ShapeTokens.CardRadius)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val isCardSelected = activeTab == FinancialSubTab.CARD
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapeTokens.ButtonRadius)
                    .background(if (isCardSelected) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent)
                    .clickable { activeTab = FinancialSubTab.CARD }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CreditCard,
                        contentDescription = null,
                        tint = if (isCardSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = stringResource(R.string.subtab_card),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isCardSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCardSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val isIbanSelected = activeTab == FinancialSubTab.BANK_ACCOUNT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(ShapeTokens.ButtonRadius)
                    .background(if (isIbanSelected) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent)
                    .clickable { activeTab = FinancialSubTab.BANK_ACCOUNT }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalance,
                        contentDescription = null,
                        tint = if (isIbanSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = stringResource(R.string.subtab_bank_account),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isIbanSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isIbanSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (activeTab == FinancialSubTab.CARD) {
            // Physical Card Preview Widget
            PhysicalCardPreview(
                title = title.ifEmpty { stringResource(R.string.subtab_card) },
                cardNumber = cardNumber,
                expiryDate = expiryDate,
                cvv = cvv,
                cardHolder = cardHolder,
                cardBrand = cardBrand
            )

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.card_title_label)) },
                placeholder = { Text(stringResource(R.string.card_title_placeholder)) },
                singleLine = true,
                isError = titleError,
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Card Number (Sensitive)
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 19) updateField("card_number", it.filter { c -> c.isDigit() }, FieldType.CARD_NUMBER, isSensitive = true) },
                label = { Text(stringResource(R.string.card_number_label)) },
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

            // Expiry & CVV Row (Both Sensitive)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { if (it.length <= 4) updateField("expiry_date", it.filter { c -> c.isDigit() }, FieldType.DATE, isSensitive = true) },
                    label = { Text(stringResource(R.string.expiry_label)) },
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
                    label = { Text(stringResource(R.string.cvv_label)) },
                    placeholder = { Text("•••") },
                    singleLine = true,
                    visualTransformation = if (isCvvRevealed) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { isCvvRevealed = !isCvvRevealed }) {
                            Icon(
                                imageVector = if (isCvvRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isCvvRevealed) stringResource(R.string.hide_value) else stringResource(R.string.reveal_value),
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

            // Cardholder Name (Not sensitive - name)
            OutlinedTextField(
                value = cardHolder,
                onValueChange = {
                    val loc = if (java.util.Locale.getDefault().language == "tr") java.util.Locale("tr", "TR") else java.util.Locale.getDefault()
                    updateField("card_holder", it.uppercase(loc), FieldType.TEXT, isSensitive = false)
                },
                label = { Text(stringResource(R.string.cardholder_label)) },
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
                        text = if (showAdditional) stringResource(R.string.hide_additional_info) else stringResource(R.string.add_card_additional_info),
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
                        onValueChange = { updateField("bank_name", it, FieldType.TEXT, isSensitive = false) },
                        label = { Text(stringResource(R.string.bank_name_label)) },
                        placeholder = { Text(stringResource(R.string.bank_name_placeholder)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeTokens.InputRadius,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    OutlinedTextField(
                        value = iban,
                        onValueChange = { updateField("iban", it.replace(" ", "").uppercase(), FieldType.IBAN, isSensitive = true) },
                        label = { Text(stringResource(R.string.linked_iban_label)) },
                        placeholder = { Text("TR33 0006 1005 ...") },
                        singleLine = true,
                        visualTransformation = IbanVisualTransformation(),
                        textStyle = MonospaceSecretStyle,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
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
                        label = { Text(stringResource(R.string.card_note_label)) },
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
        } else {
            // IBAN / Bank Account Form
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.account_title_label)) },
                placeholder = { Text(stringResource(R.string.account_title_placeholder)) },
                singleLine = true,
                isError = titleError,
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // IBAN (Sensitive)
            OutlinedTextField(
                value = iban,
                onValueChange = { updateField("iban", it.replace(" ", "").uppercase(), FieldType.IBAN, isSensitive = true) },
                label = { Text(stringResource(R.string.field_iban)) },
                placeholder = { Text("TR33 0006 1005 ...") },
                singleLine = true,
                visualTransformation = IbanVisualTransformation(),
                textStyle = MonospaceSecretStyle,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Account Holder (Not sensitive - name)
            OutlinedTextField(
                value = if (accountHolder.isNotEmpty()) accountHolder else cardHolder,
                onValueChange = {
                    val loc = if (java.util.Locale.getDefault().language == "tr") java.util.Locale("tr", "TR") else java.util.Locale.getDefault()
                    updateField("account_holder", it.uppercase(loc), FieldType.TEXT, isSensitive = false)
                    updateField("card_holder", it.uppercase(loc), FieldType.TEXT, isSensitive = false)
                },
                label = { Text(stringResource(R.string.account_holder_label)) },
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

            // Bank Name (Not sensitive)
            OutlinedTextField(
                value = bankName,
                onValueChange = { updateField("bank_name", it, FieldType.TEXT, isSensitive = false) },
                label = { Text(stringResource(R.string.bank_name_label)) },
                placeholder = { Text(stringResource(R.string.bank_name_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = ShapeTokens.InputRadius,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Progressive Disclosure: Additional Account Fields
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
                        text = if (showAdditional) stringResource(R.string.hide_additional_info) else stringResource(R.string.add_account_additional_info),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        // Branch Code (Sensitive)
                        OutlinedTextField(
                            value = branchCode,
                            onValueChange = { updateField("branch_code", it, FieldType.TEXT, isSensitive = true) },
                            label = { Text(stringResource(R.string.branch_code_label)) },
                            placeholder = { Text("1234") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = ShapeTokens.InputRadius,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        // Account Number (Sensitive)
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { updateField("account_number", it, FieldType.NUMBER, isSensitive = true) },
                            label = { Text(stringResource(R.string.account_number_label)) },
                            placeholder = { Text("5678901") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = ShapeTokens.InputRadius,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { updateField("notes", it, FieldType.MULTILINE, isSensitive = false) },
                        label = { Text(stringResource(R.string.account_note_label)) },
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
}

@Composable
fun PhysicalCardPreview(
    title: String,
    cardNumber: String,
    expiryDate: String,
    cvv: String,
    cardHolder: String,
    cardBrand: String
) {
    var isRevealed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E293B),
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(Spacing.l)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Header: Title + EMV Chip + Eye Toggle + Brand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold EMV Chip
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFCD34D), Color(0xFFD97706))
                            )
                        )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isRevealed = !isRevealed },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isRevealed) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (isRevealed) stringResource(R.string.hide_value) else stringResource(R.string.reveal_value),
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.xs))

                    Text(
                        text = cardBrand,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 2.sp
                    )
                }
            }

            // Card Number (Masked unless isRevealed is true)
            val formattedNumber = if (cardNumber.isEmpty()) {
                "•••• •••• •••• ••••"
            } else if (!isRevealed) {
                val last4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else cardNumber
                "•••• •••• •••• $last4"
            } else {
                cardNumber.chunked(4).joinToString(" ").padEnd(19, '•')
            }

            Text(
                text = formattedNumber,
                style = MonospaceSecretStyle.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )

            // Card Footer: Cardholder + Expiry & CVV
            val cardLocale = remember {
                if (java.util.Locale.getDefault().language == "tr") java.util.Locale("tr", "TR") else java.util.Locale.getDefault()
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.cardholder_label).uppercase(cardLocale),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp
                    )
                    Text(
                        text = cardHolder.ifEmpty { "AD SOYAD" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.expiry_label).take(3).uppercase(cardLocale),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 9.sp
                        )
                        val formattedExpiry = if (!isRevealed) {
                            "••/••"
                        } else if (expiryDate.length >= 2) {
                            "${expiryDate.take(2)}/${expiryDate.drop(2)}"
                        } else {
                            expiryDate.ifEmpty { "MM/YY" }
                        }
                        Text(
                            text = formattedExpiry,
                            style = MonospaceSecretStyle.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }

                    if (cvv.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "CVV",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 9.sp
                            )
                            Text(
                                text = if (isRevealed) cvv else "•••",
                                style = MonospaceSecretStyle.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
