package com.example.mygameshelf.domain.repository

import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameBrief
import com.example.mygameshelf.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getLocalGames(gameStatus: GameStatus): Flow<List<Game>>
    suspend fun upsertGame(game: Game)
    suspend fun deleteGame(id: Long)
    suspend fun searchGames(searchText: String): Flow<List<GameBrief>>
}