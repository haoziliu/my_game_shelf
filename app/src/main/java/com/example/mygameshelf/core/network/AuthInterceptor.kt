package com.example.mygameshelf.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenProvider.getValidToken() }.getOrDefault("")

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            response.close()
            val newToken = runBlocking { tokenProvider.getValidToken() }.getOrDefault("")
            val retryRequest = chain.request()
                .newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $newToken")
                .build()
            return chain.proceed(retryRequest)
        }

        return response
    }
}