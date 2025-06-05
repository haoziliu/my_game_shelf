package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class ObserveLocalGamesUseCase @Inject constructor(private val repository: GameRepository) {
    operator fun invoke(vararg gameStatus: GameStatus) = repository.observeLocalGamesByStatus(*gameStatus)
}