package com.example.mygameshelf.presentation.shelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.usecase.DeleteGameUseCase
import com.example.mygameshelf.domain.usecase.ObserveLocalGamesUseCase
import com.example.mygameshelf.domain.usecase.SaveGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val observeLocalGamesUseCase: ObserveLocalGamesUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
) : ViewModel() {

    val wantToPlayGames = observeLocalGamesUseCase(GameStatus.WANT_TO_PLAY)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val playingGames = observeLocalGamesUseCase(GameStatus.PLAYING)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val otherGames = observeLocalGamesUseCase(GameStatus.UNKNOWN, GameStatus.PLAYED, GameStatus.DROPPED)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            deleteGameUseCase(game.id)
        }
    }
}