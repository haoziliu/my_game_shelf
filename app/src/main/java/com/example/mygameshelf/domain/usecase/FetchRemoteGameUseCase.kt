package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class FetchRemoteGameUseCase @Inject constructor(private val repository: GameRepository) {
    suspend operator fun invoke(igdbId: Long) = repository.fetchGameFromRemote(igdbId)
}