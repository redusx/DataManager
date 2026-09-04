package com.redusx.floatvault.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redusx.floatvault.data.model.Category
import com.redusx.floatvault.data.model.DataEntry
import com.redusx.floatvault.data.model.FieldItem
import com.redusx.floatvault.data.model.FieldType
import com.redusx.floatvault.data.model.TemplateType
import com.redusx.floatvault.data.model.Templates
import com.redusx.floatvault.data.repository.DataRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryUiState(
    val title: String = "",
    val category: String = Category.ACCOUNT.id,
    val templateType: TemplateType = TemplateType.LOGIN,
    val fields: List<FieldItem> = emptyList(),
    val isFavorite: Boolean = false,
    val isEditing: Boolean = false,
    val editingEntryId: Long? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    // Category detail
    val categoryEntries: List<DataEntry> = emptyList(),
    // Entry detail
    val viewEntry: DataEntry? = null,
    val viewFields: List<FieldItem> = emptyList(),
    val viewTemplateType: TemplateType = TemplateType.LOGIN
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gson = Gson()
    private val fieldListType = object : TypeToken<List<FieldItem>>() {}.type

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    fun loadCategoryEntries(category: String) {
        viewModelScope.launch {
            dataRepository.getEntriesByCategory(category).collect { entries ->
                _uiState.value = _uiState.value.copy(categoryEntries = entries)
            }
        }
    }

    fun loadEntryForViewing(entryId: Long) {
        viewModelScope.launch {
            val entry = dataRepository.getEntryById(entryId)
            if (entry != null) {
                val fields: List<FieldItem> = try {
                    gson.fromJson(entry.fieldsJson, fieldListType)
                } catch (e: Exception) {
                    emptyList()
                }
                val detectedTemplate = TemplateType.detect(entry.category, fields)
                _uiState.value = _uiState.value.copy(
                    viewEntry = entry,
                    viewFields = fields,
                    viewTemplateType = detectedTemplate
                )
            }
        }
    }

    fun loadEntryForEditing(entryId: Long) {
        viewModelScope.launch {
            val entry = dataRepository.getEntryById(entryId)
            if (entry != null) {
                val fields: List<FieldItem> = try {
                    gson.fromJson(entry.fieldsJson, fieldListType)
                } catch (e: Exception) {
                    emptyList()
                }
                val detectedTemplate = TemplateType.detect(entry.category, fields)
                _uiState.value = _uiState.value.copy(
                    title = entry.title,
                    category = entry.category,
                    templateType = detectedTemplate,
                    fields = fields,
                    isFavorite = entry.isFavorite,
                    isEditing = true,
                    editingEntryId = entry.id
                )
            }
        }
    }

    fun setupForNewEntry(category: String?, templateId: String? = null) {
        val templateType = if (templateId != null) {
            TemplateType.fromId(templateId)
        } else if (category != null) {
            when (category) {
                Category.ACCOUNT.id -> TemplateType.LOGIN
                Category.FINANCIAL.id -> TemplateType.CARD
                Category.PERSONAL.id -> TemplateType.IDENTITY
                Category.CUSTOM.id -> TemplateType.SECURE_NOTE
                else -> TemplateType.LOGIN
            }
        } else {
            TemplateType.LOGIN
        }

        val initialFields = getInitialFieldsForTemplate(templateType)

        _uiState.value = _uiState.value.copy(
            title = "",
            category = templateType.category.id,
            templateType = templateType,
            fields = initialFields,
            isFavorite = false,
            isEditing = false,
            editingEntryId = null,
            saveSuccess = false,
            error = null
        )
    }

    fun onTemplateTypeChanged(newTemplateType: TemplateType) {
        val currentTitle = _uiState.value.title
        val initialFields = getInitialFieldsForTemplate(newTemplateType)
        _uiState.value = _uiState.value.copy(
            category = newTemplateType.category.id,
            templateType = newTemplateType,
            fields = initialFields
        )
    }

    private fun getInitialFieldsForTemplate(templateType: TemplateType): List<FieldItem> {
        return when (templateType) {
            TemplateType.LOGIN -> listOf(
                FieldItem("username", "", FieldType.TEXT),
                FieldItem("password", "", FieldType.PASSWORD, isSensitive = true),
                FieldItem("website", "", FieldType.TEXT)
            )
            TemplateType.CARD -> listOf(
                FieldItem("card_number", "", FieldType.CARD_NUMBER, isSensitive = true),
                FieldItem("expiry_date", "", FieldType.DATE),
                FieldItem("cvv", "", FieldType.NUMBER, isSensitive = true),
                FieldItem("card_holder", "", FieldType.TEXT)
            )
            TemplateType.BANK_ACCOUNT -> listOf(
                FieldItem("iban", "", FieldType.IBAN, isSensitive = true),
                FieldItem("account_holder", "", FieldType.TEXT),
                FieldItem("bank_name", "", FieldType.TEXT)
            )
            TemplateType.IDENTITY -> listOf(
                FieldItem("id_number", "", FieldType.NUMBER, isSensitive = true),
                FieldItem("full_name", "", FieldType.TEXT),
                FieldItem("birth_date", "", FieldType.DATE),
                FieldItem("serial_number", "", FieldType.TEXT)
            )
            TemplateType.ADDRESS -> listOf(
                FieldItem("address", "", FieldType.MULTILINE),
                FieldItem("city", "", FieldType.TEXT),
                FieldItem("district", "", FieldType.TEXT),
                FieldItem("postal_code", "", FieldType.NUMBER)
            )
            TemplateType.SECURE_NOTE -> listOf(
                FieldItem("note_content", "", FieldType.MULTILINE, isSensitive = true)
            )
            TemplateType.CUSTOM -> listOf(
                FieldItem("", "", FieldType.TEXT)
            )
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title, error = null)
    }

    fun onCategoryChanged(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onFieldsChanged(fields: List<FieldItem>) {
        _uiState.value = _uiState.value.copy(fields = fields)
    }

    fun onFavoriteToggled() {
        _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
    }

    fun saveEntry() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "title_required")
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            try {
                val fieldsJson = gson.toJson(state.fields)
                if (state.isEditing && state.editingEntryId != null) {
                    val entry = DataEntry(
                        id = state.editingEntryId,
                        category = state.category,
                        title = state.title,
                        fieldsJson = fieldsJson,
                        isFavorite = state.isFavorite
                    )
                    dataRepository.updateEntry(entry)
                } else {
                    val entry = DataEntry(
                        category = state.category,
                        title = state.title,
                        fieldsJson = fieldsJson,
                        isFavorite = state.isFavorite
                    )
                    dataRepository.insertEntry(entry)
                }
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun deleteEntry(entry: DataEntry) {
        viewModelScope.launch {
            dataRepository.deleteEntry(entry)
        }
    }

    fun toggleFavorite(entry: DataEntry) {
        viewModelScope.launch {
            dataRepository.toggleFavorite(entry.id, entry.isFavorite)
        }
    }

    fun deleteEntriesByIds(ids: List<Long>) {
        viewModelScope.launch {
            dataRepository.deleteEntriesByIds(ids)
        }
    }

    fun parseFields(fieldsJson: String): List<FieldItem> {
        return try {
            gson.fromJson(fieldsJson, fieldListType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
