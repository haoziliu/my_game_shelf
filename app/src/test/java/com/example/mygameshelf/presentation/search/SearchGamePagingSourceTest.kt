package com.example.mygameshelf.presentation.search

import androidx.paging.PagingSource
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
class SearchGamePagingSourceTest {
    @get:Rule
    val rule = MockKRule(this)

    @MockK
    private lateinit var searchGamesUseCase: SearchGamesUseCase

    @Before
    fun setup() {
    }

    @Test
    fun `load returns page when use case is successful`() = runTest {
        val query = "witcher"
        val expectedGames = listOf(
            Game(igdbId = 101L, title = "The Witcher 1"),
            Game(igdbId = 102L, title = "The Witcher 2"),
            Game(igdbId = 103L, title = "The Witcher 3"),
        )
        coEvery { searchGamesUseCase(query, any(), 0) } returns Result.success(expectedGames)

        val pagingSource = SearchGamePagingSource(searchGamesUseCase, query)
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )
        Truth.assertThat(loadResult).isEqualTo(
            PagingSource.LoadResult.Page(
                data = expectedGames,
                prevKey = null,
                nextKey = 3
            )
        )
        coVerify(exactly = 1) { searchGamesUseCase(query, any(), 0) }
    }

    @Test
    fun `load returns empty list and mutes exception when use case fails`() = runTest {
        val query = "witcher"
        val exception = RuntimeException("Network Error")
        coEvery { searchGamesUseCase(query, any(), 0) } returns Result.failure(exception)

        val pagingSource = SearchGamePagingSource(searchGamesUseCase, query)
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )
        Truth.assertThat(loadResult).isEqualTo(
            PagingSource.LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        )
        coVerify(exactly = 1) { searchGamesUseCase(query, any(), 0) }
    }
}