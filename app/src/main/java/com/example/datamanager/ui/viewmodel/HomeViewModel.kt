package com.example.datamanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datamanager.data.dao.CategoryCount
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val categoryCounts: Map<String, Int> = emptyMap(),
    val favorites: List<DataEntry> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<DataEntry> = emptyList(),
    val isSearching: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                dataRepository.getCategoryCounts(),
                dataRepository.getFavorites()
            ) { counts, favorites ->
                val countMap = counts.associate { it.category to it.count }
                _uiState.value = _uiState.value.copy(
                    categoryCounts = countMap,
                    favorites = favorites
                )
            }.collect {}
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            isSearching = query.isNotEmpty()
        )
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                dataRepository.searchEntries(query).collect { results ->
                    _uiState.value = _uiState.value.copy(searchResults = results)
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    fun toggleFavorite(entry: DataEntry) {
        viewModelScope.launch {
            dataRepository.toggleFavorite(entry.id, entry.isFavorite)
        }
    }
}
