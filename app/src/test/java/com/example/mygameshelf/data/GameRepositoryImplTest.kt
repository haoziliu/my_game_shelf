package com.example.mygameshelf.data

import com.example.mygameshelf.data.local.dao.GameDao
import com.example.mygameshelf.data.remote.api.GameApi
import com.example.mygameshelf.data.remote.model.GameDTO
import com.example.mygameshelf.data.remote.model.ImageDTO
import com.example.mygameshelf.data.remote.model.toDomainModel
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.RequestBody
import okio.Buffer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class GameRepositoryImplTest {
    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var gameDao: GameDao

    @MockK
    lateinit var gameApi: GameApi

    lateinit var gameRepositoryImpl: GameRepositoryImpl

    @Before
    fun setup() {
        gameRepositoryImpl = GameRepositoryImpl(gameDao, gameApi)
    }

    @Test
    fun `search brief remote games should return correct results`() = runTest {
        val searchText = "witcher 3"
        val requestBodySlot = slot<RequestBody>()
        val gameDTO = GameDTO(
            id = 119431L,
            name = "The Witcher 3",
            cover = ImageDTO(id = 10203L, imageId = "imageId01")
        )
        val gameDTOList = listOf(gameDTO)
        val domainGameList = gameDTOList.map { it.toDomainModel() }
        coEvery { gameApi.games(capture(requestBodySlot)) } returns Response.success(gameDTOList)

        val actualGame = gameRepositoryImpl.searchRemoteGames(searchText).getOrNull()
        Truth.assertThat(actualGame).isEqualTo(domainGameList)
        coVerify(exactly = 1) { gameApi.games(any()) }
        val actualQuery = requestBodySlot.captured.bodyToString()
        Truth.assertThat(actualQuery).contains("name")
        Truth.assertThat(actualQuery).contains("cover.image_id")
        Truth.assertThat(actualQuery).contains("search \"$searchText\"")
    }

    private fun RequestBody.bodyToString(): String {
        val buffer = Buffer()
        this.writeTo(buffer)
        return buffer.readUtf8()
    }

    @Test
    fun `fetch detailed remote game should return correct single result`() = runTest {
        val igdbId = 119431L
        val requestBodySlot = slot<RequestBody>()
        val gameDTO = GameDTO(
            id = igdbId,
            name = "The Witcher 3",
            cover = ImageDTO(id = 10203L, imageId = "imageId01"),
            summary = "a game summary",
            storyline = "a story line"
        )
        val gameDTOList = listOf(gameDTO)
        coEvery { gameApi.games(capture(requestBodySlot)) } returns Response.success(gameDTOList)
        val expectedDomainGame = gameDTO.toDomainModel()

        val actualGame = gameRepositoryImpl.fetchGameFromRemote(119431L).getOrNull()
        Truth.assertThat(actualGame).isEqualTo(expectedDomainGame)
        coVerify(exactly = 1) { gameApi.games(any()) }
        val actualQuery = requestBodySlot.captured.bodyToString()
        Truth.assertThat(actualQuery).contains("name")
        Truth.assertThat(actualQuery).contains("rating")
        Truth.assertThat(actualQuery).contains("cover.image_id")
        Truth.assertThat(actualQuery).contains("storyline")
        Truth.assertThat(actualQuery).contains("summary")
        Truth.assertThat(actualQuery).contains("artworks.image_id")
        Truth.assertThat(actualQuery).contains("limit 1")
        Truth.assertThat(actualQuery).contains("where id = $igdbId")
    }
}