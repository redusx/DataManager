package com.example.datamanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.repository.DataRepository
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
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        dataRepository.getAllEntries(),
        dataRepository.getCategoryCounts(),
        _selectedCategory,
        _searchQuery
    ) { allEntries, counts, selectedCategory, searchQuery ->
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
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategory.value = categoryId
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
