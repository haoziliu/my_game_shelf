package com.example.mygameshelf.data.remote.api

import com.example.mygameshelf.data.remote.model.AuthResponseDTO
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("oauth2/token")
    suspend fun auth(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String? = "client_credentials"
    ): Response<AuthResponseDTO>

}