package com.example.mygameshelf.data.local.database

import androidx.room.TypeConverter
import com.example.mygameshelf.domain.model.GameStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class Converters {
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.toString() // ISO-8601 format, e.g. "2007-12-03T10:15:30"
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String): LocalDateTime {
        val result: LocalDateTime = try {
            LocalDateTime.parse(dateString)
        } catch (e: DateTimeParseException) {
            val localDate = LocalDate.parse(dateString)
            localDate.atTime(0, 0)
        }
        return result
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