package com.example.mygameshelf.domain.model

import java.time.LocalDateTime

data class Game(
    val id: Long = 0L,
    var title: String,
    val igdbId: Long? = null,
    val platform: String? = null,
    val genre: String? = null,
    var status: GameStatus = GameStatus.UNKNOWN,
    var myRating: Float? = null,
    var lastEdit: LocalDateTime? = null,
    val imageId: String? = null,
    val summary: String? = null,
    val storyline: String? = null,
    val rating: Float? = null,
    val artworksId: List<String>? = null,
) {

}

enum class GameStatus(val displayName: String) {
    WANT_TO_PLAY("Want to Play"),
    PLAYING("Playing"),
    ON_HOLD("On Hold"),
    PLAYED("Played"),
    DROPPED("Dropped"),
    UNKNOWN("Unknown")
}