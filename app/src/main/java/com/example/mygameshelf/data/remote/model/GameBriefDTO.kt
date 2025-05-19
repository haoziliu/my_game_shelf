package com.example.mygameshelf.data.remote.model

import com.example.mygameshelf.domain.model.GameBrief
import com.google.gson.annotations.SerializedName

data class GameBriefDTO(
    val id: Long,
    val name: String,
    val cover: CoverDTO? = null
)

data class CoverDTO(
    val id: Long,
    @SerializedName("image_id")
    val imageId: String
)

fun GameBriefDTO.toDomainModel(): GameBrief {
    return GameBrief(
        igdbId = id,
        title = name,
        imageId = cover?.imageId
    )
}