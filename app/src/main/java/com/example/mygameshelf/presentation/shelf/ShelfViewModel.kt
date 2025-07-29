package com.example.mygameshelf.presentation.shelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.usecase.ObserveLocalGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    observeLocalGamesUseCase: ObserveLocalGamesUseCase,
) : ViewModel() {

    val wantToPlayGames = observeLocalGamesUseCase(GameStatus.WANT_TO_PLAY, GameStatus.ON_HOLD)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val playingGames = observeLocalGamesUseCase(GameStatus.PLAYING)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val otherGames = observeLocalGamesUseCase(GameStatus.UNKNOWN, GameStatus.PLAYED, GameStatus.DROPPED)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}