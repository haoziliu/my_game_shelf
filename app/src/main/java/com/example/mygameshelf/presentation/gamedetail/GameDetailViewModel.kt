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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    private val localGameFlow = observeLocalGameUseCase.byIgdbId(igdbId)
    private val remoteGameFlow = flow {
        emit(fetchRemoteGameUseCase(igdbId).onFailure { it.printStackTrace() })
    }

    private val _eventFlow = MutableSharedFlow<GameDetailUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val gameDetail = combine(
        remoteGameFlow,
        localGameFlow
    ) { remoteResult, localGame ->
        val remoteGame = remoteResult.getOrNull() ?: return@combine localGame
        if (localGame == null) {
            remoteGame
        } else {
            remoteGame.copy(
                id = localGame.id,
                myRating = localGame.myRating,
                status = localGame.status,
                lastEdit = localGame.lastEdit,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges = _hasUnsavedChanges.asStateFlow()
    private val _editMyRating = MutableStateFlow<Float?>(null)
    val editMyRating = _editMyRating.asStateFlow()
    private val _editStatus = MutableStateFlow<GameStatus?>(null)
    val editStatus = _editStatus.asStateFlow()

    init {
        viewModelScope.launch {
            gameDetail.filterNotNull().first().let { g ->
                _editMyRating.value = g.myRating ?: 0.0f
                _editStatus.value = g.status
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
        val currentDetail = gameDetail.value ?: return
        val updated = Game(
            id = currentDetail.id,
            igdbId = igdbId,
            title = currentDetail.title,
            imageId = currentDetail.imageId,
            myRating = _editMyRating.value,
            status = _editStatus.value ?: GameStatus.UNKNOWN,
            lastEdit = LocalDateTime.now()
        )
        viewModelScope.launch {
            saveGameUseCase(updated)
            _hasUnsavedChanges.value = false
            _eventFlow.emit(GameDetailUiEvent.ShowToast("Update saved!"))
        }
    }

    fun resetEdits() {
        _editMyRating.value = gameDetail.value?.myRating ?: 0.0f
        _editStatus.value = gameDetail.value?.status ?: GameStatus.UNKNOWN
        _hasUnsavedChanges.value = false
    }

}