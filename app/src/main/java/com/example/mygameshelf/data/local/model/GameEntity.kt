package com.example.mygameshelf.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import java.time.LocalDateTime

@Entity(
    tableName = "games",
    indices = [Index(value = ["igdbId"], unique = true)]
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val igdbId: Long?,
    val platform: String?,
    val genre: String?,
    val status: GameStatus,
    val rating: Float?,
    var lastEdit: LocalDateTime?,
    var imageId: String?
)

fun GameEntity.toDomainModel(): Game {
    return Game(
        id = id,
        title = title,
        igdbId = igdbId,
        platform = platform,
        genre = genre,
        status = status,
        myRating = rating,
        lastEdit = lastEdit,
        imageId = imageId,
    )
}

fun Game.toEntity(): GameEntity {
    return GameEntity(
        id = id,
        title = title,
        igdbId = igdbId,
        platform = platform,
        genre = genre,
        status = status,
        rating = myRating,
        lastEdit = lastEdit,
        imageId = imageId,
    )
}