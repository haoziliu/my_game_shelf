package com.example.mygameshelf.presentation.shelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.usecase.DeleteGameUseCase
import com.example.mygameshelf.domain.usecase.GetLocalGamesUseCase
import com.example.mygameshelf.domain.usecase.SaveGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val getLocalGamesUseCase: GetLocalGamesUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase,
) : ViewModel() {

    val wantToPlayGames = getLocalGamesUseCase(GameStatus.WANT_TO_PLAY)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val completedGames = getLocalGamesUseCase(GameStatus.COMPLETED)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addGame(game: Game) {
        viewModelScope.launch {
            saveGameUseCase(game)
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            deleteGameUseCase(game.id)
        }
    }
}