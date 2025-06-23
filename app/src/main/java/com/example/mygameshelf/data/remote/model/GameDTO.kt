package com.example.mygameshelf.data.remote.model

import com.example.mygameshelf.domain.model.Game
import com.google.gson.annotations.SerializedName

data class GameDTO(
    val id: Long,
    val name: String,
    val cover: ImageDTO? = null,
    val rating: Float? = null,
    val summary: String? = null,
    val storyline: String? = null,
    val artworks: List<ImageDTO>? = null,
)

data class ImageDTO(
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
        artworksId = artworks?.map { it.imageId }
    )
}