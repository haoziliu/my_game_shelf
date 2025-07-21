package com.example.mygameshelf.presentation.gamedetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.mygameshelf.MainDispatcherRule
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.usecase.FetchRemoteGameUseCase
import com.example.mygameshelf.domain.usecase.ObserveLocalGameUseCase
import com.example.mygameshelf.domain.usecase.SaveGameUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameDetailViewModelTest {
    @get:Rule
    val rule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var observeLocalGameUseCase: ObserveLocalGameUseCase

    @MockK
    lateinit var fetchRemoteGameUseCase: FetchRemoteGameUseCase

    @MockK
    lateinit var saveGameUseCase: SaveGameUseCase

    lateinit var gameDetailViewModel: GameDetailViewModel

    private val testIgdbId = 119402L
    private val local = Game(
        igdbId = testIgdbId,
        title = "The Witcher 3",
        myRating = 4.5f,
        status = GameStatus.PLAYING
    )
    private val remote =
        Game(igdbId = testIgdbId, title = "The Witcher 3", summary = "It is a game")
    val arguments = mapOf("igdbId" to testIgdbId.toString())
    val savedStateHandle = SavedStateHandle(arguments)

    @Before
    fun setup() {
        // empty
    }

    @Test
    fun `local object exists and remote object loaded, should combine two objects`() = runTest {
        coEvery { observeLocalGameUseCase.byIgdbId(testIgdbId) } returns flowOf(local)
        coEvery { fetchRemoteGameUseCase(testIgdbId) } returns Result.success(remote)
        gameDetailViewModel = GameDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeLocalGameUseCase = observeLocalGameUseCase,
            fetchRemoteGameUseCase = fetchRemoteGameUseCase,
            saveGameUseCase = saveGameUseCase
        )
        gameDetailViewModel.gameDetail.test {
            val obj = awaitItem()
            Truth.assertThat(obj).isNotNull()
            Truth.assertThat(obj!!.myRating).isEqualTo(local.myRating)
            Truth.assertThat(obj.summary).isEqualTo(remote.summary)
            coVerify(exactly = 1) { observeLocalGameUseCase.byIgdbId(any()) }
            coVerify(exactly = 1) { fetchRemoteGameUseCase(any()) }
        }
    }

    @Test
    fun `new game with no saved object should see unknown status and no rating`() = runTest {
        coEvery { observeLocalGameUseCase.byIgdbId(testIgdbId) } returns flowOf(null)
        coEvery { fetchRemoteGameUseCase(testIgdbId) } returns Result.success(remote)
        gameDetailViewModel = GameDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeLocalGameUseCase = observeLocalGameUseCase,
            fetchRemoteGameUseCase = fetchRemoteGameUseCase,
            saveGameUseCase = saveGameUseCase
        )
        gameDetailViewModel.gameDetail.test {
            val obj = awaitItem()
            Truth.assertThat(obj).isNotNull()
            Truth.assertThat(obj!!.myRating).isNull()
            Truth.assertThat(obj.status).isEqualTo(GameStatus.UNKNOWN)
            Truth.assertThat(obj.summary).isEqualTo(remote.summary)
            coVerify(exactly = 1) { observeLocalGameUseCase.byIgdbId(any()) }
            coVerify(exactly = 1) { fetchRemoteGameUseCase(any()) }
        }
    }

    @Test
    fun `remote object load failed but local game exists should still see local data`() = runTest {
        coEvery { observeLocalGameUseCase.byIgdbId(testIgdbId) } returns flowOf(local)
        val exception = RuntimeException("Network error")
        coEvery { fetchRemoteGameUseCase(testIgdbId) } returns Result.failure(exception)
        gameDetailViewModel = GameDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeLocalGameUseCase = observeLocalGameUseCase,
            fetchRemoteGameUseCase = fetchRemoteGameUseCase,
            saveGameUseCase = saveGameUseCase
        )
        gameDetailViewModel.gameDetail.test {
            val obj = awaitItem()
            Truth.assertThat(obj).isNotNull()
            Truth.assertThat(obj!!.myRating).isEqualTo(local.myRating)
            Truth.assertThat(obj.status).isEqualTo(local.status)
            Truth.assertThat(obj.summary).isNull()
            coVerify(exactly = 1) { observeLocalGameUseCase.byIgdbId(any()) }
            coVerify(exactly = 1) { fetchRemoteGameUseCase(any()) }
        }
    }

    @Test
    fun `edit local game should update the state flow`() = runTest {
        coEvery { observeLocalGameUseCase.byIgdbId(testIgdbId) } returns flowOf(local)
        coEvery { fetchRemoteGameUseCase(testIgdbId) } returns Result.success(remote)
        gameDetailViewModel = GameDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeLocalGameUseCase = observeLocalGameUseCase,
            fetchRemoteGameUseCase = fetchRemoteGameUseCase,
            saveGameUseCase = saveGameUseCase
        )
        gameDetailViewModel.setRating(3.5f)
        Truth.assertThat(gameDetailViewModel.editMyRating.value).isEqualTo(3.5f)
        Truth.assertThat(gameDetailViewModel.hasUnsavedChanges.value).isEqualTo(true)
        gameDetailViewModel.setStatus(GameStatus.PLAYING)
        Truth.assertThat(gameDetailViewModel.editStatus.value).isEqualTo(GameStatus.PLAYING)
        Truth.assertThat(gameDetailViewModel.hasUnsavedChanges.value).isEqualTo(true)
    }

    @Test
    fun `edit local game then discard, should revert`() = runTest {
        coEvery { observeLocalGameUseCase.byIgdbId(testIgdbId) } returns flowOf(local)
        coEvery { fetchRemoteGameUseCase(testIgdbId) } returns Result.success(remote)
        gameDetailViewModel = GameDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeLocalGameUseCase = observeLocalGameUseCase,
            fetchRemoteGameUseCase = fetchRemoteGameUseCase,
            saveGameUseCase = saveGameUseCase
        )
        gameDetailViewModel.setRating(3.5f)
        gameDetailViewModel.setStatus(GameStatus.PLAYING)
        gameDetailViewModel.resetEdits()
        Truth.assertThat(gameDetailViewModel.editMyRating.value).isEqualTo(local.myRating)
        Truth.assertThat(gameDetailViewModel.editStatus.value).isEqualTo(local.status)
        Truth.assertThat(gameDetailViewModel.hasUnsavedChanges.value).isEqualTo(false)
    }

    @Test
    fun `save updated data should invoke save use case`() = runTest {
        coEvery { observeLocalGameUseCase.byIgdbId(testIgdbId) } returns flowOf(local)
        coEvery { fetchRemoteGameUseCase(testIgdbId) } returns Result.success(remote)
        gameDetailViewModel = GameDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeLocalGameUseCase = observeLocalGameUseCase,
            fetchRemoteGameUseCase = fetchRemoteGameUseCase,
            saveGameUseCase = saveGameUseCase
        )
        coEvery { saveGameUseCase(any()) } returns Unit
        gameDetailViewModel.setRating(3.5f)
        gameDetailViewModel.saveChanges()
        coVerify(exactly = 1) { saveGameUseCase(any()) }
        Truth.assertThat(gameDetailViewModel.hasUnsavedChanges.value).isEqualTo(false)
    }

}