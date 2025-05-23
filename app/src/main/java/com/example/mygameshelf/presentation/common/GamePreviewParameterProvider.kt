package com.example.mygameshelf.presentation.common

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import java.time.LocalDate
import kotlin.random.Random

class GamePreviewParameterProvider : PreviewParameterProvider<Game> {
    override val values: Sequence<Game>
        get() = sequenceOf(
            Game(
                id = Random.nextLong(100),
                title = "new game super super super long name maybe this this long",
                status = GameStatus.COMPLETED,
                myRating = 5.0f,
                lastEdit = LocalDate.now(),
            ),
            Game(
                id = Random.nextLong(100),
                title = "new game2",
                status = GameStatus.COMPLETED,
                myRating = 4.0f,
                lastEdit = LocalDate.now().plusDays(Random.nextLong(60) - 10),
            ),
            Game(
                id = Random.nextLong(100),
                title = "new game3",
                status = GameStatus.COMPLETED,
                myRating = 3.5f,
                lastEdit = LocalDate.now().plusDays(Random.nextLong(60) - 30),
            ),
        )
}