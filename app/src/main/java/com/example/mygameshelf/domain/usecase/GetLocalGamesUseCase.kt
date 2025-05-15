package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class GetLocalGamesUseCase @Inject constructor(private val repository: GameRepository) {
    operator fun invoke(gameStatus: GameStatus) = repository.getLocalGames(gameStatus)
}