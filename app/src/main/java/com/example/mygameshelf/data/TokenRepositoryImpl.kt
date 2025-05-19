package com.example.mygameshelf.data

import com.example.mygameshelf.BuildConfig
import com.example.mygameshelf.core.network.TokenProvider
import com.example.mygameshelf.data.local.TokenStore
import com.example.mygameshelf.data.remote.api.AuthApi
import com.example.mygameshelf.domain.repository.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : TokenRepository, TokenProvider {

    override suspend fun getValidToken(): String {
        if (tokenStore.isTokenExpired()) {
            val response = authApi.auth(
                clientId = BuildConfig.TWITCH_CLIENT_ID,
                clientSecret = BuildConfig.TWITCH_CLIENT_SECRET
            )
            if (!response.isSuccessful) {
                throw Exception("Token fetch failed: ${response.code()}")
            }
            val dto = response.body()!!
            tokenStore.saveToken(dto.accessToken, dto.expiresIn)
            return dto.accessToken
        } else {
            return tokenStore.getToken() ?: throw Exception("Token missing")
        }
    }
}