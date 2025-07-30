package com.example.mygameshelf.core.di

import com.example.mygameshelf.data.FakeTokenRepository
import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.remote.api.GameApi
import com.example.mygameshelf.domain.repository.GameRepository
import com.example.mygameshelf.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideGameRepository(): GameRepository {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(): TokenRepository {
        return FakeTokenRepository()
    }
}