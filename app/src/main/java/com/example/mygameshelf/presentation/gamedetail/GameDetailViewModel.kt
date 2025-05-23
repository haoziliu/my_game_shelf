package com.example.mygameshelf.presentation.gamedetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.usecase.FetchRemoteGameUseCase
import com.example.mygameshelf.domain.usecase.ObserveLocalGameUseCase
import com.example.mygameshelf.domain.usecase.SaveGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeLocalGameUseCase: ObserveLocalGameUseCase,
    private val fetchRemoteGameUseCase: FetchRemoteGameUseCase,
    private val saveGameUseCase: SaveGameUseCase,
) : ViewModel() {
    private val igdbId: Long = checkNotNull(savedStateHandle.get<String>("igdbId")).toLong()
    val game = observeLocalGameUseCase.byIgdbId(igdbId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val _gameDetail = MutableStateFlow<Game?>(null)
    val gameDetail = _gameDetail.asStateFlow()
    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges = _hasUnsavedChanges.asStateFlow()
    private val _editRating = MutableStateFlow<Float?>(null)
    val editRating = _editRating.asStateFlow()
    private val _editStatus = MutableStateFlow<GameStatus?>(null)
    val editStatus = _editStatus.asStateFlow()

    init {
        // Wait for the first non-null game, then seed the edit buffers
        viewModelScope.launch {
//            game.filterNotNull().first().let { g ->
//                _editRating.value = g.myRating
//                _editStatus.value = g.status
//            }
            _gameDetail.value = fetchRemoteGameUseCase(igdbId)
        }
    }

    fun setRating(r: Float) {
        _editRating.value = r
        _hasUnsavedChanges.value = true
    }

    fun setStatus(s: GameStatus) {
        _editStatus.value = s
        _hasUnsavedChanges.value = true
    }

    fun saveChanges() {
        val current = game.value ?: return
        val newRating = editRating.value ?: current.myRating
        val newStatus = editStatus.value ?: current.status

        val updated = current.copy(
            myRating = newRating,
            status = newStatus,
            lastEdit = LocalDate.now()
        )

        viewModelScope.launch {
            saveGameUseCase(updated)
            _hasUnsavedChanges.value = false
        }
    }

    fun resetEdits() {
        game.value?.let { g ->
            _editRating.value = g.myRating
            _editStatus.value = g.status
        }
        _hasUnsavedChanges.value = false
    }

}