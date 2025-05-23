package com.example.mygameshelf.data.remote.api

import com.example.mygameshelf.data.remote.model.GameDTO
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GameApi {

    @POST("search") // search any fields
    suspend fun search(@Body rawQuery: RequestBody): Response<List<GameDTO>>

    @POST("games")
    suspend fun games(@Body rawQuery: RequestBody): Response<List<GameDTO>>
}