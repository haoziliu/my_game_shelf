package com.example.mygameshelf.presentation.gamedetail

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
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeLocalGameUseCase: ObserveLocalGameUseCase,
    private val fetchRemoteGameUseCase: FetchRemoteGameUseCase,
    private val saveGameUseCase: SaveGameUseCase,
) : ViewModel() {
    private val igdbId: Long = checkNotNull(savedStateHandle.get<String>("igdbId")).toLong()
    val localGame = observeLocalGameUseCase.byIgdbId(igdbId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1_000), null)
    private val _gameDetail = MutableStateFlow<Game?>(null)
    val gameDetail = _gameDetail.asStateFlow()
    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges = _hasUnsavedChanges.asStateFlow()
    private val _editMyRating = MutableStateFlow<Float?>(null)
    val editMyRating = _editMyRating.asStateFlow()
    private val _editStatus = MutableStateFlow<GameStatus?>(null)
    val editStatus = _editStatus.asStateFlow()

    init {
        viewModelScope.launch {
            fetchRemoteGameUseCase(igdbId).onSuccess {
                _gameDetail.value = it
            }.onFailure {
                it.printStackTrace()
            }
            localGame.filterNotNull().first().let { g ->
                _editMyRating.value = g.myRating ?: 0.0f
                _editStatus.value = g.status
                val currentDetail = _gameDetail.value
                if (currentDetail != null) {
                    _gameDetail.value = currentDetail.copy(
                        myRating = g.myRating,
                        status = g.status,
                        lastEdit = g.lastEdit,
                    )
                }
            }
        }
    }

    fun setRating(r: Float) {
        _editMyRating.value = r
        _hasUnsavedChanges.value = true
    }

    fun setStatus(s: GameStatus) {
        _editStatus.value = s
        _hasUnsavedChanges.value = true
    }

    fun saveChanges() {
        val current = localGame.value ?: Game(
            igdbId = igdbId,
            title = _gameDetail.value?.title ?: "",
            imageId = _gameDetail.value?.imageId
        )
        val newRating = _editMyRating.value ?: current.myRating
        val newStatus = _editStatus.value ?: current.status

        val updated = current.copy(
            myRating = newRating,
            status = newStatus,
            lastEdit = LocalDateTime.now()
        )
        viewModelScope.launch {
            saveGameUseCase(updated)
            _hasUnsavedChanges.value = false
        }
        _gameDetail.value = _gameDetail.value!!.copy(
            myRating = updated.myRating,
            status = updated.status,
            lastEdit = updated.lastEdit,
        )
    }

    fun resetEdits() {
        _editMyRating.value = localGame.value?.myRating ?: 0.0f
        _editStatus.value = localGame.value?.status ?: GameStatus.UNKNOWN
        _hasUnsavedChanges.value = false
    }

}