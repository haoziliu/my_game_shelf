package com.example.mygameshelf.domain.model

import java.time.LocalDate

data class Game(
    val id: Long = 0L,
    var title: String,
    val igdbId: Long? = null,
    val platform: String? = null,
    val genre: String? = null,
    var status: GameStatus = GameStatus.UNKNOWN,
    var myRating: Float? = null,
    var lastEdit: LocalDate? = null,
    val imageId: String? = null,
    val summary: String? = null,
    val storyline: String? = null,
    val rating: Float? = null,
) {
    val coverSmallUrl: String?
        get() = imageId?.let { "https://images.igdb.com/igdb/image/upload/t_cover_small/$it.jpg" }
    val coverBigUrl: String?
        get() = imageId?.let { "https://images.igdb.com/igdb/image/upload/t_cover_big/$it.jpg" }
}

enum class GameStatus {
    WANT_TO_PLAY, PLAYING, COMPLETED, PAUSED, DROPPED, UNKNOWN
}