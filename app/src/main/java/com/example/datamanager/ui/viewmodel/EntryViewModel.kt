package com.example.datamanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.data.model.Templates
import com.example.datamanager.data.repository.DataRepository
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
    val category: String = Category.PERSONAL.id,
    val fields: List<FieldItem> = listOf(FieldItem("", "", FieldType.TEXT)),
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
    val viewFields: List<FieldItem> = emptyList()
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
                _uiState.value = _uiState.value.copy(
                    viewEntry = entry,
                    viewFields = fields
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
                _uiState.value = _uiState.value.copy(
                    title = entry.title,
                    category = entry.category,
                    fields = fields,
                    isFavorite = entry.isFavorite,
                    isEditing = true,
                    editingEntryId = entry.id
                )
            }
        }
    }

    fun setupForNewEntry(category: String?) {
        _uiState.value = _uiState.value.copy(
            title = "",
            category = category ?: Category.PERSONAL.id,
            fields = listOf(FieldItem("", "", FieldType.TEXT)),
            isFavorite = false,
            isEditing = false,
            editingEntryId = null,
            saveSuccess = false,
            error = null
        )
    }

    fun applyTemplate(templateName: String) {
        val template = Templates.all().firstOrNull { it.name == templateName } ?: return
        _uiState.value = _uiState.value.copy(
            category = template.category.id,
            fields = template.fields
        )
    }

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
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
