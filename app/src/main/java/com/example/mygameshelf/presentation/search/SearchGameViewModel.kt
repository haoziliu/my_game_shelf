package com.example.mygameshelf.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.usecase.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class SearchGameViewModel @Inject constructor(
    val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {
    private val PAGE_SIZE = 10
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData = _hasMoreData.asStateFlow()
    private var paginationOffset = 0
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games = _games.asStateFlow()

    init {
        viewModelScope.launch {
            _searchText.debounce(500)
                .filter { it.length > 2 }
                .distinctUntilChanged()
                .collect { text ->
                    paginationOffset = 0
                    _loading.value = true
                    _hasMoreData.value = true
                    val newList = searchGamesUseCase(text, PAGE_SIZE, paginationOffset).getOrDefault(emptyList())
                    if (newList.size < PAGE_SIZE) {
                        _hasMoreData.value = false
                    } else {
                        _hasMoreData.value = true
                        paginationOffset += newList.size
                    }
                    _loading.value = false
                    _games.value = newList
                }
        }
    }

    fun onSearchTextChanged(newText: String) {
        _searchText.value = newText
    }

    fun loadMore() {
        if (loading.value) return
        viewModelScope.launch {
            _loading.value = true
            val newList = searchGamesUseCase(_searchText.value, PAGE_SIZE, paginationOffset).getOrDefault(emptyList())
            if (newList.size < PAGE_SIZE) {
                _hasMoreData.value = false
            } else {
                _hasMoreData.value = true
                paginationOffset += newList.size
            }
            _loading.value = false
            _games.value += newList
        }
    }
}