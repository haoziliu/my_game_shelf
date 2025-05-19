package com.example.mygameshelf.data.remote.api

import com.example.mygameshelf.data.remote.model.GameBriefDTO
import com.example.mygameshelf.domain.model.GameBrief
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GameApi {

    @POST("games")
    suspend fun games(@Body rawQuery: RequestBody): Response<List<GameBriefDTO>>

}