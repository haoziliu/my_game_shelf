package com.example.mygameshelf.domain.usecase

import com.example.mygameshelf.domain.repository.TokenRepository
import javax.inject.Inject

class GetValidTokenUseCase @Inject constructor(private val repository: TokenRepository) {
    suspend operator fun invoke(): String = repository.getValidToken()
}