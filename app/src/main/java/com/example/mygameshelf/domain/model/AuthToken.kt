package com.example.mygameshelf.domain.model

data class AuthToken(
    val token: String,
    val expiresIn: Int
)