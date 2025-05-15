package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class DeleteGameUseCase @Inject constructor(private val repository: GameRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteGame(id)
}