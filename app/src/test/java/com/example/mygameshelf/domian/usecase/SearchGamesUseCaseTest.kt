package com.example.mygameshelf.domian.usecase

import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.repository.GameRepository
import com.example.mygameshelf.domain.usecase.SearchGamesUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchGamesUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: GameRepository

    private lateinit var searchGamesUseCase: SearchGamesUseCase

    @Before
    fun setup() {
        searchGamesUseCase = SearchGamesUseCase(repository)
    }

    @Test
    fun `invoke should return success result when repository search is successful`() = runTest {
        val query = "witcher"
        val expectedGames = listOf(Game(title = "The Witcher 3"), Game(title = "The Witcher 2"))
        val expectedResult = Result.success(expectedGames)
        coEvery { searchGamesUseCase(query) } returns expectedResult

        val actualResult = searchGamesUseCase(query)
        Truth.assertThat(actualResult).isEqualTo(expectedResult)
        coVerify(exactly = 1) { repository.searchRemoteGames(query) }
    }

    @Test
    fun `invoke should return failure result when repository search fails`() = runTest {
        val query = "some game"
        val exception = RuntimeException("Network error")
        val expectedResult = Result.failure<List<Game>>(exception)
        coEvery { searchGamesUseCase(query) } returns expectedResult

        val actualResult = searchGamesUseCase(query)
        Truth.assertThat(actualResult.isFailure).isTrue()
        Truth.assertThat(actualResult.exceptionOrNull()).isEqualTo(exception)
        coVerify(exactly = 1) { repository.searchRemoteGames(query) }
    }
}