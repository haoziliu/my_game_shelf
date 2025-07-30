package com.example.mygameshelf

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.repository.GameRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SearchFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeGameRepository: GameRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun searchForGame_selectResult_displaysDetailScreen() {
        val gameToReturn = Game(title = "The Witcher 3", igdbId = 123L, imageId = "fake_image")
        coEvery {
            fakeGameRepository.searchRemoteGames("witcher").getOrNull()
        } returns listOf(gameToReturn)
        coEvery {
            fakeGameRepository.fetchGameFromRemote(123L).getOrNull()
        } returns gameToReturn.copy(summary = "A detailed summary.")
        coEvery {
            fakeGameRepository.observeLocalGameByIgdbId(123L)
        } returns flowOf(null)

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("NavigateToSearchButton")
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("NavigateToSearchButton").performClick()

        val searchField = composeTestRule.onNodeWithTag("SearchTextField")
        searchField.performTextReplacement("witcher")
        composeTestRule.waitUntil(timeoutMillis = 2_000) {
            composeTestRule
                .onAllNodesWithText("The Witcher 3")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("The Witcher 3").assertIsDisplayed()
        composeTestRule.onNodeWithText("The Witcher 3").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText("A detailed summary.")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("A detailed summary.").assertIsDisplayed()
        composeTestRule.onNodeWithText("The Witcher 3", substring = true).assertIsDisplayed()
    }
}