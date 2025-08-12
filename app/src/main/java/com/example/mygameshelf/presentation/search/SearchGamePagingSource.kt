package com.example.mygameshelf.presentation.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.usecase.SearchGamesUseCase

class SearchGamePagingSource(
    private val searchGamesUseCase: SearchGamesUseCase,
    private val searchText: String
) : PagingSource<Int, Game>() { // Int represents offset

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val currentOffset = params.key ?: 0
        val requestedLoadSize = params.loadSize
        return searchGamesUseCase(
            searchText = searchText,
            take = requestedLoadSize,
            offset = currentOffset
        ).fold(
            onSuccess = { games ->
                LoadResult.Page(
                    data = games,
                    prevKey = if (currentOffset == 0) null else currentOffset - requestedLoadSize,
                    nextKey = if (games.size < requestedLoadSize) null else currentOffset + games.size
                )
            }, onFailure = { throwable ->
                throwable.printStackTrace()
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

}