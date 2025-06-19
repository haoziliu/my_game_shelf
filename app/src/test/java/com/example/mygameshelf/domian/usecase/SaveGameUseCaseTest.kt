package com.example.mygameshelf.domian.usecase

import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.repository.GameRepository
import com.example.mygameshelf.domain.usecase.SaveGameUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class SaveGameUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: GameRepository
    private lateinit var saveGameUseCase: SaveGameUseCase

    @Before
    fun setup() {
        saveGameUseCase = SaveGameUseCase(repository)
    }

    @Test
    fun `invoke should call repository's saveGame with correct game object`() = runTest {
        val gameToSave = Game(
            id = 1L,
            title = "Game to save",
            status = GameStatus.PLAYING,
            myRating = 0.8f,
            lastEdit = LocalDateTime.now()
        )
        coEvery { repository.saveGame(any()) } returns Unit
        saveGameUseCase(gameToSave)

        coVerify(exactly = 1) { repository.saveGame(gameToSave) }
    }

}