package com.example.datamanager.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.datamanager.R
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.Templates
import com.example.datamanager.ui.component.DynamicFieldEditor
import com.example.datamanager.ui.theme.ShieldGold
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) stringResource(R.string.edit_entry)
                        else stringResource(R.string.add_entry),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onFavoriteToggled() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) ShieldGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChanged,
                    label = { Text(stringResource(R.string.entry_title)) },
                    singleLine = true,
                    isError = uiState.error == "title_required",
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (uiState.error == "title_required") {
                    Text(
                        text = stringResource(R.string.title_required),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            // Category selector
            item {
                Column {
                    Text(
                        text = stringResource(R.string.category),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        OutlinedButton(
                            onClick = { showCategoryMenu = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            val categoryName = when (Category.fromId(uiState.category)) {
                                Category.PERSONAL -> stringResource(R.string.category_personal)
                                Category.FINANCIAL -> stringResource(R.string.category_financial)
                                Category.ACCOUNT -> stringResource(R.string.category_accounts)
                                Category.CUSTOM -> stringResource(R.string.category_custom)
                            }
                            Text(text = categoryName)
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
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showTemplateMenu = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.use_template))
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
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                DynamicFieldEditor(
                    fields = uiState.fields,
                    onFieldsChanged = viewModel::onFieldsChanged
                )
            }

            // Save button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.saveEntry() },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (uiState.isSaving) stringResource(R.string.saving)
                        else stringResource(R.string.save),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
