package com.example.mygameshelf.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.usecase.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class SearchGameViewModel @Inject constructor(
    val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {

    private val PAGE_SIZE = 10
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()
    private var paginationOffset = 0

    init {
        viewModelScope.launch {
            _uiState.map { it.searchText }
                .debounce(500)
                .filter { it.length > 2 }
                .distinctUntilChanged()
                .collect { text ->
                    _uiState.update { it.copy(isLoading = true, hasMoreData = true) }
                    val newList = searchGamesUseCase(
                        text,
                        PAGE_SIZE,
                        paginationOffset
                    ).getOrDefault(emptyList())
                    paginationOffset = newList.size
                    _uiState.update {
                        it.copy(
                            hasMoreData = newList.size >= PAGE_SIZE,
                            isLoading = false,
                            games = newList
                        )
                    }
                }
        }
    }

    fun onSearchTextChanged(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
    }

    fun loadMore() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val newList = searchGamesUseCase(
                _uiState.value.searchText,
                PAGE_SIZE,
                paginationOffset
            ).getOrDefault(emptyList())
            paginationOffset += newList.size
            _uiState.update {
                it.copy(
                    hasMoreData = newList.size >= PAGE_SIZE,
                    games = it.games + newList,
                    isLoading = false
                )
            }
        }
    }
}