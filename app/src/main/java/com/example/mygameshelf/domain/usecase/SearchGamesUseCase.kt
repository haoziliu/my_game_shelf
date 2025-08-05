package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.repository.GameRepository
import javax.inject.Inject

class SearchGamesUseCase @Inject constructor(private val repository: GameRepository) {
    suspend operator fun invoke(searchText: String, take: Int? = 10, offset: Int? = 0)
        = repository.searchRemoteGames(searchText, take, offset)
}