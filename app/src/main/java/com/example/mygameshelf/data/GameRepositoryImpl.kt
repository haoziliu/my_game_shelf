package com.example.mygameshelf.data

import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.local.model.toDomainModel
import com.example.mygameshelf.data.local.model.toEntity
import com.example.mygameshelf.data.remote.api.GameApi
import com.example.mygameshelf.data.remote.model.toDomainModel
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameBrief
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val gameApi: GameApi
) : GameRepository {

    override fun getLocalGames(gameStatus: GameStatus): Flow<List<Game>> {
        return gameDao.getGames(gameStatus)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun upsertGame(game: Game) {
        gameDao.upsertGame(game.toEntity())
    }

    override suspend fun deleteGame(id: Long) {
        gameDao.deleteGameById(id)
    }

    override suspend fun searchGames(searchText: String): Flow<List<GameBrief>> = flow {
        val rawQuery = "fields name,cover.image_id; limit 10; where name ~ *\"$searchText\"*;"
        val response = gameApi.games(
            rawQuery.toRequestBody("text/plain".toMediaType())
        )
        if (response.isSuccessful) {
            val dtoList = response.body() ?: emptyList()
            emit(dtoList.map { it.toDomainModel() })
        } else {
            throw IOException("Error fetching games: ${response.code()}")
        }
    }
}