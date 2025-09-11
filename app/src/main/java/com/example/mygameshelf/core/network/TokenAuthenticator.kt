package com.example.mygameshelf.core.network

import com.example.mygameshelf.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRepository: TokenRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val newToken = runBlocking {
            try {
                tokenRepository.fetchAndSaveNewToken()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return if (newToken != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } else {
            null
        }
    }
}