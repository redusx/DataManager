package com.redusx.floatvault.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.redusx.floatvault.R
import com.redusx.floatvault.data.model.TemplateType
import com.redusx.floatvault.ui.component.DynamicFieldEditor
import com.redusx.floatvault.ui.component.templates.AddressTemplateEditor
import com.redusx.floatvault.ui.component.templates.BankAccountTemplateEditor
import com.redusx.floatvault.ui.component.templates.CardTemplateEditor
import com.redusx.floatvault.ui.component.templates.IdentityTemplateEditor
import com.redusx.floatvault.ui.component.templates.LoginTemplateEditor
import com.redusx.floatvault.ui.component.templates.SecureNoteTemplateEditor
import com.redusx.floatvault.ui.theme.ShapeTokens
import com.redusx.floatvault.ui.theme.Spacing
import com.redusx.floatvault.ui.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryScreen(
    category: String? = null,
    templateId: String? = null,
    entryId: Long? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entryId, category, templateId) {
        if (entryId != null && entryId > 0) {
            viewModel.loadEntryForEditing(entryId)
        } else {
            viewModel.setupForNewEntry(category = category, templateId = templateId)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }

    val isEditing = entryId != null && entryId > 0
    val screenTitle = if (isEditing) {
        stringResource(R.string.edit_entry)
    } else {
        stringResource(uiState.templateType.titleRes)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
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
                            contentDescription = stringResource(R.string.overlay_back),
                            tint = MaterialTheme.colorScheme.onSurface
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
            // Contextual Guided Template Editor
            item {
                when (uiState.templateType) {
                    TemplateType.CARD -> {
                        CardTemplateEditor(
                            title = uiState.title,
                            onTitleChange = viewModel::onTitleChanged,
                            fields = uiState.fields,
                            onFieldsChange = viewModel::onFieldsChanged,
                            titleError = uiState.error == "title_required"
                        )
                    }
                    TemplateType.LOGIN -> {
                        LoginTemplateEditor(
                            title = uiState.title,
                            onTitleChange = viewModel::onTitleChanged,
                            fields = uiState.fields,
                            onFieldsChange = viewModel::onFieldsChanged,
                            titleError = uiState.error == "title_required"
                        )
                    }
                    TemplateType.IDENTITY -> {
                        IdentityTemplateEditor(
                            title = uiState.title,
                            onTitleChange = viewModel::onTitleChanged,
                            fields = uiState.fields,
                            onFieldsChange = viewModel::onFieldsChanged,
                            titleError = uiState.error == "title_required"
                        )
                    }
                    TemplateType.ADDRESS -> {
                        AddressTemplateEditor(
                            title = uiState.title,
                            onTitleChange = viewModel::onTitleChanged,
                            fields = uiState.fields,
                            onFieldsChange = viewModel::onFieldsChanged,
                            titleError = uiState.error == "title_required"
                        )
                    }
                    TemplateType.SECURE_NOTE -> {
                        SecureNoteTemplateEditor(
                            title = uiState.title,
                            onTitleChange = viewModel::onTitleChanged,
                            fields = uiState.fields,
                            onFieldsChange = viewModel::onFieldsChanged,
                            titleError = uiState.error == "title_required"
                        )
                    }
                    TemplateType.BANK_ACCOUNT -> {
                        BankAccountTemplateEditor(
                            title = uiState.title,
                            onTitleChange = viewModel::onTitleChanged,
                            fields = uiState.fields,
                            onFieldsChange = viewModel::onFieldsChanged,
                            titleError = uiState.error == "title_required"
                        )
                    }
                    TemplateType.CUSTOM -> {
                        DynamicFieldEditor(
                            fields = uiState.fields,
                            onFieldsChanged = viewModel::onFieldsChanged
                        )
                    }
                }
            }

            // Save CTA Button
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
