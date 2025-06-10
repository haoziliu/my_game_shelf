package com.example.mygameshelf.data

import com.example.mygameshelf.BuildConfig
import com.example.mygameshelf.core.network.ApiHelper
import com.example.mygameshelf.core.network.TokenProvider
import com.example.mygameshelf.data.local.TokenStore
import com.example.mygameshelf.data.remote.api.AuthApi
import com.example.mygameshelf.domain.repository.TokenRepository
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : TokenRepository, TokenProvider {

    override suspend fun getValidToken(): Result<String> {
        return kotlin.runCatching {
            if (tokenStore.isTokenExpired()) {
                val response = ApiHelper.call {
                    authApi.auth(
                        clientId = BuildConfig.TWITCH_CLIENT_ID,
                        clientSecret = BuildConfig.TWITCH_CLIENT_SECRET
                    )
                }
                tokenStore.saveToken(response.accessToken, response.expiresIn)
                response.accessToken
            } else {
                tokenStore.getToken() ?: throw Exception("Token missing")
            }
        }
    }
}