package com.example.mygameshelf.presentation.search

import app.cash.turbine.test
import com.example.mygameshelf.MainDispatcherRule
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.usecase.SearchGamesUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchGameViewModelTest {
    @get:Rule
    val rule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var searchGamesUseCase: SearchGamesUseCase

    private lateinit var searchGameViewModel: SearchGameViewModel

    @Before
    fun setup() {
        searchGameViewModel = SearchGameViewModel(searchGamesUseCase)
    }

    @Test
    fun `search with debounce should return result`() = runTest {
        val query = "witcher"
        val expectedGames = listOf(Game(title = "The Witcher 3"), Game(title = "The Witcher 2"))
        coEvery { searchGamesUseCase(query) } returns Result.success(expectedGames)

        searchGameViewModel.games.test {
            Truth.assertThat(awaitItem()).isEmpty()
            searchGameViewModel.onSearchTextChanged(query)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(501)
            Truth.assertThat(awaitItem()).isEqualTo(expectedGames)
            coVerify(exactly = 1) { searchGamesUseCase(query) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `short input should not trigger search`() = runTest {
        searchGameViewModel.games.test {
            Truth.assertThat(awaitItem()).isEmpty()
            searchGameViewModel.onSearchTextChanged("w")
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(501)
            expectNoEvents()
            searchGameViewModel.onSearchTextChanged("wi")
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(501)
            expectNoEvents()
            coVerify(exactly = 0) { searchGamesUseCase(any()) }
        }
    }

    @Test
    fun `bounce input should only trigger newest`() = runTest {
        val query1 = "witcher"
        val query2 = "witcher 3"
        val expectedGames2 = listOf(Game(title = "The Witcher 3"))
        coEvery { searchGamesUseCase(query2) } returns Result.success(expectedGames2)

        searchGameViewModel.games.test {
            Truth.assertThat(awaitItem()).isEmpty()
            searchGameViewModel.onSearchTextChanged(query1)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(400)
            expectNoEvents()
            searchGameViewModel.onSearchTextChanged(query2)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(400)
            expectNoEvents()
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(101)
            Truth.assertThat(awaitItem()).isEqualTo(expectedGames2)
            coVerify(exactly = 0) { searchGamesUseCase(query1) }
            coVerify(exactly = 1) { searchGamesUseCase(query2) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `failed use case should return empty games`() = runTest{
        val query = "witcher"
        val expectedGames = listOf(Game(title = "The Witcher 3"), Game(title = "The Witcher 2"))
        coEvery { searchGamesUseCase(query) } returns Result.success(expectedGames)
        val query2 = "witcher3"
        val exception = RuntimeException("Network error")
        coEvery { searchGamesUseCase(query2) } returns Result.failure(exception)

        searchGameViewModel.games.test {
            Truth.assertThat(awaitItem()).isEmpty()
            searchGameViewModel.onSearchTextChanged(query)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(501)
            Truth.assertThat(awaitItem()).isEqualTo(expectedGames)
            searchGameViewModel.onSearchTextChanged(query2)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(501)
            Truth.assertThat(awaitItem()).isEmpty()
            coVerify(exactly = 2) { searchGamesUseCase(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }
}