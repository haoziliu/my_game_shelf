package com.example.mygameshelf.data.remote.model

import com.example.mygameshelf.domain.model.Game
import com.google.gson.annotations.SerializedName

data class GameDTO(
    val id: Long,
    val name: String,
    val cover: CoverDTO? = null,
    val rating: Float? = null,
    val summary: String? = null,
    val storyline: String? = null
)

data class CoverDTO(
    val id: Long,
    @SerializedName("image_id")
    val imageId: String
)

fun GameDTO.toDomainModel(): Game {
    return Game(
        igdbId = id,
        title = name,
        imageId = cover?.imageId,
        rating = rating,
        summary = summary,
        storyline = storyline,
    )
}