package com.example.mygameshelf.core.network

import retrofit2.HttpException
import retrofit2.Response

object ApiHelper {

    suspend fun <T> call(apiCall: suspend () -> Response<T>): T {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body() ?: throw NullPointerException("Response body is null")
            } else {
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
