package com.example.mygameshelf.domain.usecase

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.repository.GameRepository
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
        coEvery { repository.searchRemoteGames(query) } returns expectedResult

        val actualResult = searchGamesUseCase(query)
        Truth.assertThat(actualResult).isEqualTo(expectedResult)
        coVerify(exactly = 1) { repository.searchRemoteGames(query) }
    }

    @Test
    fun `invoke should return failure result when repository search fails`() = runTest {
        val query = "some game"
        val exception = RuntimeException("Network error")
        val expectedResult = Result.failure<List<Game>>(exception)
        coEvery { repository.searchRemoteGames(query) } returns expectedResult

        val actualResult = searchGamesUseCase(query)
        Truth.assertThat(actualResult.isFailure).isTrue()
        Truth.assertThat(actualResult.exceptionOrNull()).isEqualTo(exception)
        coVerify(exactly = 1) { repository.searchRemoteGames(query) }
    }

    @Test
    fun `search pagination should work well`() = runTest {
        val query = "witcher"
        val page1Games = mutableListOf<Game>()
        repeat(5) {
            page1Games += Game(title = "witcher 1-$it")
        }
        val page2Games = mutableListOf<Game>()
        repeat(5) {
            page2Games += Game(title = "witcher 2-$it")
        }
        val expectedPage1Result = Result.success(page1Games)
        val expectedPage2Result = Result.success(page1Games)
        coEvery { repository.searchRemoteGames(query, 5, 0) } returns expectedPage1Result
        coEvery { repository.searchRemoteGames(query, 5, 5) } returns expectedPage2Result

        val actualPage1Result = searchGamesUseCase(query, 5, 0)
        Truth.assertThat(actualPage1Result).isEqualTo(expectedPage1Result)
        val actualPage2Result = searchGamesUseCase(query, 5, 5)
        Truth.assertThat(actualPage2Result).isEqualTo(expectedPage2Result)
        coVerify(exactly = 2) { repository.searchRemoteGames(any(), any(), any()) }
    }
}