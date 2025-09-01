package com.example.mygameshelf.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.mygameshelf.data.GameRepositoryImpl
import com.example.mygameshelf.data.TokenRepositoryImpl
import com.example.mygameshelf.data.local.TokenStore
import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.local.database.AppDatabase
import com.example.mygameshelf.data.remote.api.AuthApi
import com.example.mygameshelf.data.remote.api.GameApi
import com.example.mygameshelf.data.remote.api.GameGraphQLApi
import com.example.mygameshelf.domain.repository.GameRepository
import com.example.mygameshelf.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "game_shelf_db")
            .addMigrations().build()
    }

    @Provides
    fun provideGameDao(db: AppDatabase): GameDao {
        return db.gameDao()
    }

    @Provides
    @Singleton
    fun provideGameRepository(dao: GameDao, api: GameApi, gameGraphQLApi: GameGraphQLApi): GameRepository {
        return GameRepositoryImpl(dao, api, gameGraphQLApi)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(authApi: AuthApi, tokenStore: TokenStore): TokenRepository {
        return TokenRepositoryImpl(authApi, tokenStore)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("auth_token_prefs")
        }
    }
}