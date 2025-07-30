package com.example.mygameshelf.data

import com.example.mygameshelf.domain.repository.TokenRepository

class FakeTokenRepository: TokenRepository {
    override suspend fun getValidToken(): Result<String> {
        return Result.success("fake-test-token")
    }
}