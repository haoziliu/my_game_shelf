package com.example.mygameshelf.data.local.database

import androidx.room.TypeConverter
import com.example.mygameshelf.domain.model.GameStatus
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString() // ISO-8601 format, e.g. "2025-03-31"
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun fromGameStatus(gameStatus: GameStatus): String {
        return gameStatus.name
    }

    @TypeConverter
    fun toGameStatus(gameStatusString: String): GameStatus {
        return GameStatus.valueOf(gameStatusString)
    }

}