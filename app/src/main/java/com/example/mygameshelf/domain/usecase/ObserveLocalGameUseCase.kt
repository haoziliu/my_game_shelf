package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class ObserveLocalGameUseCase @Inject constructor(private val repository: GameRepository) {
    fun byId(id: Long) = repository.observeLocalGameById(id)
    fun byIgdbId(igdbId: Long) = repository.observeLocalGameByIgdbId(igdbId)
}