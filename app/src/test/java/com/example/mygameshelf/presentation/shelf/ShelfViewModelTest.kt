package com.example.mygameshelf.presentation.shelf

import app.cash.turbine.test
import com.example.mygameshelf.MainDispatcherRule
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.domain.usecase.ObserveLocalGamesUseCase
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShelfViewModelTest {
    @get:Rule
    val rule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var observeLocalGamesUseCase: ObserveLocalGamesUseCase

    lateinit var shelfViewModel: ShelfViewModel

    private val c1g1 = Game(
        igdbId = 119411L,
        title = "The Witcher 3",
        myRating = 4.3f,
        status = GameStatus.WANT_TO_PLAY
    )

    private val c1g2 = Game(
        igdbId = 119412L,
        title = "The Witcher 3",
        myRating = 4.4f,
        status = GameStatus.ON_HOLD
    )

    private val c2g1 = Game(
        igdbId = 119421L,
        title = "The Witcher 3",
        myRating = 4.5f,
        status = GameStatus.PLAYING
    )

    private val c3g1 = Game(
        igdbId = 119431L,
        title = "The Witcher 3",
        myRating = 4.5f,
        status = GameStatus.UNKNOWN
    )

    private val c3g2 = Game(
        igdbId = 119432L,
        title = "The Witcher 3",
        myRating = 4.5f,
        status = GameStatus.PLAYED
    )

    private val c3g3 = Game(
        igdbId = 119433L,
        title = "The Witcher 3",
        myRating = 2.5f,
        status = GameStatus.DROPPED
    )

    @Before
    fun setup() {
        coEvery { observeLocalGamesUseCase(*anyVararg()) } returns flowOf(emptyList())
    }

    @Test
    fun `wantToPlayGames should contain games with want-to-play and on-hold statuses`() = runTest {
        val wantToPlayList = listOf(c1g1, c1g2)
        coEvery {
            observeLocalGamesUseCase(GameStatus.WANT_TO_PLAY, GameStatus.ON_HOLD)
        } returns flowOf(wantToPlayList)

        shelfViewModel = ShelfViewModel(observeLocalGamesUseCase)
        shelfViewModel.wantToPlayGames.test {
            Truth.assertThat(awaitItem()).isEqualTo(wantToPlayList)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { observeLocalGamesUseCase(GameStatus.WANT_TO_PLAY, GameStatus.ON_HOLD) }
    }

    @Test
    fun `playingGames should contain games with want-to-play and on-hold statuses`() = runTest {
        val playingList = listOf(c2g1)
        coEvery {
            observeLocalGamesUseCase(GameStatus.PLAYING)
        } returns flowOf(playingList)

        shelfViewModel = ShelfViewModel(observeLocalGamesUseCase)
        shelfViewModel.playingGames.test {
            Truth.assertThat(awaitItem()).isEqualTo(playingList)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { observeLocalGamesUseCase(GameStatus.PLAYING) }
    }

    @Test
    fun `otherGames should contain games with finished statuses`() = runTest {
        val playedList =  listOf(c3g1, c3g2, c3g3)
        coEvery {
            observeLocalGamesUseCase(GameStatus.UNKNOWN, GameStatus.PLAYED, GameStatus.DROPPED)
        } returns flowOf(playedList)

        shelfViewModel = ShelfViewModel(observeLocalGamesUseCase)
        shelfViewModel.otherGames.test {
            Truth.assertThat(awaitItem()).isEqualTo(playedList)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { observeLocalGamesUseCase(GameStatus.UNKNOWN, GameStatus.PLAYED, GameStatus.DROPPED)}
    }

    @Test
    fun `no game in shelf should display empty`() = runTest {
        shelfViewModel = ShelfViewModel(observeLocalGamesUseCase)
        shelfViewModel.wantToPlayGames.test {
            Truth.assertThat(awaitItem()).isEmpty()
        }
        shelfViewModel.playingGames.test {
            Truth.assertThat(awaitItem()).isEmpty()
        }
        shelfViewModel.otherGames.test {
            Truth.assertThat(awaitItem()).isEmpty()
        }
    }
}