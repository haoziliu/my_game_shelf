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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchGameViewModel @Inject constructor(
    val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _games = MutableStateFlow<List<Game>>(listOf())
    val games = _games.asStateFlow()
//    private val _loading = MutableStateFlow(false)
//    val loading = _loading.asStateFlow()

    init {
        viewModelScope.launch {
            _searchText.debounce(500)
                .filter { it.length > 2 }
                .distinctUntilChanged()
                .catch { _games.value = emptyList() }
                .collect{ query ->
                    searchGamesUseCase(query).onSuccess {
                        _games.value = it
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
        }
    }

    fun onSearchTextChanged(newText: String) {
        _searchText.value = newText
    }
}