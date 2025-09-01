package com.example.mygameshelf.data

import com.example.mygameshelf.core.network.ApiHelper
import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.local.model.toDomainModel
import com.example.mygameshelf.data.local.model.toEntity
import com.example.mygameshelf.data.remote.api.GameApi
import com.example.mygameshelf.data.remote.api.GameGraphQLApi
import com.example.mygameshelf.data.remote.model.toDomainModel
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val gameApi: GameApi,
    private val gameGraphQLApi: GameGraphQLApi
) : GameRepository {

    override fun observeLocalGamesByStatus(vararg gameStatus: GameStatus): Flow<List<Game>> {
        return gameDao.observeGames(*gameStatus)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun saveGame(game: Game) =
        gameDao.upsertGame(game.toEntity())

    override suspend fun deleteGame(id: Long) =
        gameDao.deleteGameById(id)

    override suspend fun getLocalGameById(id: Long) =
        gameDao.getGameById(id)?.toDomainModel()

    override suspend fun getLocalGameByIgdbId(igdbId: Long) =
        gameDao.getGameByIgdbId(igdbId)?.toDomainModel()

    override fun observeLocalGameById(id: Long): Flow<Game?> =
        gameDao.observeGameById(id).map { it?.toDomainModel() }

    override fun observeLocalGameByIgdbId(igdbId: Long): Flow<Game?> =
        gameDao.observeGameByIgdbId(igdbId).map { it?.toDomainModel() }

    override suspend fun searchRemoteGames(
        searchText: String,
        take: Int?,
        offset: Int?
    ): Result<List<Game>> {
        val rawQuery =
            "fields name,cover.image_id; limit $take; offset $offset; search \"$searchText\";"
        return runCatching {
            val response = ApiHelper.call {
                gameApi.games(
                    rawQuery.toRequestBody("text/plain".toMediaType())
                )
            }
            response.map { it.toDomainModel() }
        }
    }

    override suspend fun fetchGameFromRemote(igdbId: Long): Result<Game> {
        val rawQuery =
            "fields name,rating,cover.image_id,storyline,summary,artworks.image_id; limit 1; where id = $igdbId;"
        return runCatching {
            val response = ApiHelper.call {
                gameApi.games(
                    rawQuery.toRequestBody("text/plain".toMediaType())
                )
            }
            response[0].toDomainModel()
        }
    }

    override suspend fun searchRemoteGamesGraphQL(
        searchText: String,
        take: Int?,
        offset: Int?
    ): Result<List<Game>> {
        return runCatching {
            val response = gameGraphQLApi.searchGames(searchText, take, offset).getOrNull()
            response?.map { it.toDomainModel() } ?: emptyList()
        }
    }
}