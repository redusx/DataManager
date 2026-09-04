package com.redusx.floatvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redusx.floatvault.data.model.DataEntry
import com.redusx.floatvault.data.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val entries: List<DataEntry> = emptyList(),
    val categoryCounts: Map<String, Int> = emptyMap(),
    val totalCount: Int = 0,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isViewingEntries: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isViewingEntries = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        dataRepository.getAllEntries(),
        dataRepository.getCategoryCounts(),
        _selectedCategory,
        _searchQuery,
        _isViewingEntries
    ) { allEntries, counts, selectedCategory, searchQuery, isViewingEntries ->
        val countMap = counts.associate { it.category to it.count }
        val total = allEntries.size

        val filtered = allEntries.filter { entry ->
            val matchesCategory = selectedCategory == null || entry.category == selectedCategory
            val matchesQuery = searchQuery.isEmpty() ||
                    entry.title.contains(searchQuery, ignoreCase = true) ||
                    entry.fieldsJson.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }

        HomeUiState(
            entries = filtered,
            categoryCounts = countMap,
            totalCount = total,
            selectedCategory = selectedCategory,
            searchQuery = searchQuery,
            isViewingEntries = isViewingEntries || searchQuery.isNotEmpty(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isNotEmpty()) {
            _isViewingEntries.value = true
        }
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategory.value = categoryId
        _isViewingEntries.value = true
    }

    fun openCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        _isViewingEntries.value = true
    }

    fun returnToCategoryGrid() {
        _isViewingEntries.value = false
        _selectedCategory.value = null
        _searchQuery.value = ""
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun toggleFavorite(entry: DataEntry) {
        viewModelScope.launch {
            dataRepository.toggleFavorite(entry.id, entry.isFavorite)
        }
    }
}
