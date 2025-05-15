package com.example.mygameshelf.data

import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.local.model.toDomainModel
import com.example.mygameshelf.data.local.model.toEntity
import com.example.mygameshelf.data.remote.api.GameApi
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl constructor(val gameDao: GameDao, val gameApi: GameApi) : GameRepository {
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
}