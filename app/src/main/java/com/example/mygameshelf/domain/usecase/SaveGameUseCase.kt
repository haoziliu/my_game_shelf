package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class SaveGameUseCase @Inject constructor(private val repository: GameRepository) {
    suspend operator fun invoke(game: Game) = repository.upsertGame(game)
}