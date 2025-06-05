package com.example.mygameshelf.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import com.example.mygameshelf.data.local.model.GameEntity
import com.example.mygameshelf.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

@Dao
@RewriteQueriesToDropUnusedColumns
interface GameDao {
//    @Query("SELECT * FROM games WHERE status = :gameStatus ORDER BY lastEdit DESC")
//    fun observeGames(gameStatus: GameStatus): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE status IN (:gameStatus) ORDER BY lastEdit DESC")
    fun observeGames(vararg gameStatus: GameStatus): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: Long): GameEntity?

    @Query("SELECT * FROM games WHERE id = :id")
    fun observeGameById(id: Long): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE igdbId = :igdbId")
    fun getGameByIgdbId(igdbId: Long): GameEntity?

    @Query("SELECT * FROM games WHERE igdbId = :igdbId")
    fun observeGameByIgdbId(igdbId: Long): Flow<GameEntity?>
}