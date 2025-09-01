package com.example.mygameshelf.data.remote.model

import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.graphql.SearchGamesQuery

fun SearchGamesQuery.SearchGame.toDomainModel(): Game {
    return Game(
        igdbId = id.toLong(),
        title = name,
        imageId = coverImage?.id,
        rating = rating?.toFloat(),
        summary = summary,
        storyline = storyline,
        artworksId = artworks?.filterNotNull()?.map { it.id }
    )
}