package com.example.mygameshelf.presentation.search

import com.example.mygameshelf.domain.model.Game

data class SearchUiState(
    val searchText: String = "",
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreData: Boolean = true
)