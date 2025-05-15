package com.example.mygameshelf.core.network

interface TokenProvider {
    suspend fun getValidToken(): String
}