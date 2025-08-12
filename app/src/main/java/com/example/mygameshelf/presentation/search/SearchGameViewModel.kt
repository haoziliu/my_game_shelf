package com.example.mygameshelf.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.mygameshelf.domain.usecase.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class SearchGameViewModel @Inject constructor(
    val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val seenIds = mutableSetOf<Long>()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val games = _searchText.debounce(500)
        .filter { it.length > 2 }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            seenIds.clear()
            Pager(
                config = PagingConfig(pageSize = SearchGamePagingSource.DEFAULT_PAGE_SIZE),
                pagingSourceFactory = { SearchGamePagingSource(searchGamesUseCase, query) }
            ).flow.map { pagingData ->
                pagingData.filter { game -> seenIds.add(game.igdbId!!) }
            }
        }
        .cachedIn(viewModelScope)

    fun onSearchTextChanged(newText: String) {
        _searchText.value = newText
    }
}