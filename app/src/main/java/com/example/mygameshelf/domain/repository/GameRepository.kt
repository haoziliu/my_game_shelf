package com.example.mygameshelf.domain.repository

import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeLocalGamesByStatus(vararg gameStatus: GameStatus): Flow<List<Game>>
    suspend fun saveGame(game: Game)
    suspend fun deleteGame(id: Long)
    suspend fun getLocalGameById(id: Long): Game?
    suspend fun getLocalGameByIgdbId(igdbId: Long): Game?
    fun observeLocalGameById(id: Long): Flow<Game?>
    fun observeLocalGameByIgdbId(igdbId: Long): Flow<Game?>
    suspend fun searchRemoteGames(
        searchText: String,
        take: Int? = 10,
        offset: Int? = 0
    ): Result<List<Game>>

    suspend fun fetchGameFromRemote(igdbId: Long): Result<Game>
}