package com.example.mygameshelf.presentation.gamedetail

sealed class GameDetailUiEvent {
    data class ShowToast(val message: String) : GameDetailUiEvent()
}