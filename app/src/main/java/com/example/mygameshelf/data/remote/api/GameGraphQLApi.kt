package com.example.mygameshelf.data.remote.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.mygameshelf.graphql.SearchGamesQuery
import dagger.Provides
import javax.inject.Inject

class GameGraphQLApi @Inject constructor(
    val apolloClient: ApolloClient
) {
    suspend fun searchGames(
        searchText: String,
        limit: Int?,
        offset: Int?
    ): Result<List<SearchGamesQuery.SearchGame>> {
        val response = apolloClient.query(
            SearchGamesQuery(
                searchText = searchText,
                limit = Optional.presentIfNotNull(limit),
                offset = Optional.presentIfNotNull(offset)
            )
        ).execute()
        if (response.hasErrors()) {
            return Result.failure(Exception(response.errors?.first()?.message))
        }
        return Result.success(response.data?.searchGames?.filterNotNull() ?: emptyList())
    }
}