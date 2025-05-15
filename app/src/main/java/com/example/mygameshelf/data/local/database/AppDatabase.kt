package com.example.mygameshelf.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.local.model.GameEntity

@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}