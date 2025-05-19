package com.example.mygameshelf.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygameshelf.domain.usecase.GetValidTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLauncherViewModel @Inject constructor(
    private val getValidTokenUseCase: GetValidTokenUseCase
) : ViewModel() {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready

    init {
        viewModelScope.launch {
            try {
                getValidTokenUseCase()
                _ready.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _ready.value = false
            }
        }
    }
}