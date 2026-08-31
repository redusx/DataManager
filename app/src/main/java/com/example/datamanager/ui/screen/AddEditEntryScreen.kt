package com.example.datamanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datamanager.R
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.Templates
import com.example.datamanager.ui.component.DynamicFieldEditor
import com.example.datamanager.ui.theme.ShapeTokens
import com.example.datamanager.ui.theme.Spacing
import com.example.datamanager.ui.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryScreen(
    category: String? = null,
    entryId: Long? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showTemplateMenu by remember { mutableStateOf(false) }

    LaunchedEffect(entryId, category) {
        if (entryId != null && entryId > 0) {
            viewModel.loadEntryForEditing(entryId)
        } else {
            viewModel.setupForNewEntry(category)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    val isEditing = entryId != null && entryId > 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) stringResource(R.string.edit_entry)
                        else stringResource(R.string.add_entry),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(Spacing.touchTargetMin)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onFavoriteToggled() },
                        modifier = Modifier.size(Spacing.touchTargetMin)
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            // Title Input
            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChanged,
                    label = { Text(stringResource(R.string.entry_title)) },
                    singleLine = true,
                    isError = uiState.error == "title_required",
                    modifier = Modifier.fillMaxWidth(),
                    shape = ShapeTokens.InputRadius,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                if (uiState.error == "title_required") {
                    Text(
                        text = stringResource(R.string.title_required),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = Spacing.xs, top = Spacing.xxs)
                    )
                }
            }

            // Category selector
            item {
                Column {
                    Text(
                        text = stringResource(R.string.category),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Row {
                        OutlinedButton(
                            onClick = { showCategoryMenu = true },
                            shape = ShapeTokens.ButtonRadius
                        ) {
                            val categoryName = when (Category.fromId(uiState.category)) {
                                Category.PERSONAL -> stringResource(R.string.category_personal)
                                Category.FINANCIAL -> stringResource(R.string.category_financial)
                                Category.ACCOUNT -> stringResource(R.string.category_accounts)
                                Category.CUSTOM -> stringResource(R.string.category_custom)
                            }
                            Text(text = categoryName, style = MaterialTheme.typography.labelMedium)
                        }
                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            Category.entries.forEach { cat ->
                                val name = when (cat) {
                                    Category.PERSONAL -> stringResource(R.string.category_personal)
                                    Category.FINANCIAL -> stringResource(R.string.category_financial)
                                    Category.ACCOUNT -> stringResource(R.string.category_accounts)
                                    Category.CUSTOM -> stringResource(R.string.category_custom)
                                }
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        viewModel.onCategoryChanged(cat.id)
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Templates (only for new entries)
            if (!isEditing) {
                item {
                    Column {
                        Text(
                            text = stringResource(R.string.templates),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.xxs))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            OutlinedButton(
                                onClick = { showTemplateMenu = true },
                                shape = ShapeTokens.ButtonRadius
                            ) {
                                Text(
                                    text = stringResource(R.string.use_template),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            DropdownMenu(
                                expanded = showTemplateMenu,
                                onDismissRequest = { showTemplateMenu = false }
                            ) {
                                Templates.all().forEach { template ->
                                    val templateName = when (template.name) {
                                        "personal_info" -> stringResource(R.string.template_personal)
                                        "credit_card" -> stringResource(R.string.template_credit_card)
                                        "bank_account" -> stringResource(R.string.template_bank_account)
                                        "login_account" -> stringResource(R.string.template_login)
                                        else -> template.name
                                    }
                                    DropdownMenuItem(
                                        text = { Text(templateName) },
                                        onClick = {
                                            viewModel.applyTemplate(template.name)
                                            showTemplateMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dynamic fields
            item {
                Text(
                    text = stringResource(R.string.fields),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                DynamicFieldEditor(
                    fields = uiState.fields,
                    onFieldsChanged = viewModel::onFieldsChanged
                )
            }

            // Save CTA button
            item {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Button(
                    onClick = { viewModel.saveEntry() },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ShapeTokens.ButtonRadius,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (uiState.isSaving) stringResource(R.string.saving)
                        else stringResource(R.string.save),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}
