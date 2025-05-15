package com.example.mygameshelf.domain.model

import java.time.LocalDate

data class Game(
    val id: Long = 0L,
    val title: String,
    val igdbId: Long? = null,
    val platform: String? = null,
    val genre: String? = null,
    val status: GameStatus,
    val rating: Float? = null,
    var lastEdit: LocalDate,
)

enum class GameStatus {
    WANT_TO_PLAY, PLAYING, COMPLETED, PAUSED, DROPPED
}