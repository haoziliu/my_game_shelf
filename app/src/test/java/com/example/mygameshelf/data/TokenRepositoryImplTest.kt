package com.example.mygameshelf.data

import com.example.mygameshelf.data.local.TokenStore
import com.example.mygameshelf.data.remote.api.AuthApi
import com.example.mygameshelf.data.remote.model.AuthResponseDTO
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class TokenRepositoryImplTest {
    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var authApi: AuthApi

    @MockK
    lateinit var tokenStore: TokenStore

    lateinit var tokenRepositoryImpl: TokenRepositoryImpl

    @Before
    fun setup() {
        tokenRepositoryImpl = TokenRepositoryImpl(authApi, tokenStore)
    }

    @Test
    fun `expired token should trigger auth api call`() = runTest {
        coEvery { tokenStore.isTokenExpired() } returns true
        val authResponse = AuthResponseDTO(accessToken = "abc", expiresIn = 1000, tokenType = "ttt")
        coEvery { authApi.auth(any(), any(), any()) } returns Response.success(authResponse)
        coEvery { tokenStore.saveToken(any(), any()) } returns Unit

        val token = tokenRepositoryImpl.getValidToken().getOrNull()
        Truth.assertThat(token).isEqualTo(authResponse.accessToken)
        coVerify(exactly = 1) { tokenStore.isTokenExpired() }
        coVerify(exactly = 1) { authApi.auth(any(), any()) }
        coVerify(exactly = 1) { tokenStore.saveToken(authResponse.accessToken, authResponse.expiresIn) }
    }

    @Test
    fun `not expired token should return the saved one`() = runTest {
        val savedToken = "abc"
        coEvery { tokenStore.isTokenExpired() } returns false
        coEvery { tokenStore.getToken() } returns savedToken

        val token = tokenRepositoryImpl.getValidToken().getOrNull()
        Truth.assertThat(token).isEqualTo(savedToken)
        coVerify(exactly = 1) { tokenStore.isTokenExpired() }
        coVerify(exactly = 1) { tokenStore.getToken() }
        coVerify(exactly = 0) { authApi.auth(any(), any()) }
    }
}