package com.example.mygameshelf.domain.repository

interface TokenRepository {
    suspend fun getValidToken(): Result<String>
}