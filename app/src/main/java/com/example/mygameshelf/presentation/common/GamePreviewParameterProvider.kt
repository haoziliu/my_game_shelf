package com.example.mygameshelf.presentation.common

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import java.time.LocalDateTime
import kotlin.random.Random

class GameListPreviewParameterProvider: PreviewParameterProvider<List<Game>> {
    override val values: Sequence<List<Game>>
        get() = sequenceOf(
            listOf(
                Game(
                    id = Random.nextLong(100),
                    title = "new game super super super long name maybe this this long",
                    status = GameStatus.PLAYED,
                    myRating = 5.0f,
                    lastEdit = LocalDateTime.now(),
                ),
                Game(
                    id = Random.nextLong(100),
                    title = "new game2",
                    status = GameStatus.PLAYED,
                    myRating = 4.0f,
                    lastEdit = LocalDateTime.now().plusDays(Random.nextLong(60) - 10),
                ),
                Game(
                    id = Random.nextLong(100),
                    title = "new game3",
                    status = GameStatus.PLAYED,
                    myRating = 3.5f,
                    lastEdit = LocalDateTime.now().plusDays(Random.nextLong(60) - 30),
                ),
            )
        )
}

class GamePreviewParameterProvider : PreviewParameterProvider<Game> {
    override val values: Sequence<Game>
        get() = sequenceOf(
            Game(
                id = Random.nextLong(100),
                title = "new game super super super long name maybe this this long",
                status = GameStatus.PLAYED,
                myRating = 5.0f,
                lastEdit = LocalDateTime.now(),
            ),
            Game(
                id = Random.nextLong(100),
                title = "new game2",
                status = GameStatus.PLAYED,
                myRating = 4.0f,
                lastEdit = LocalDateTime.now().plusDays(Random.nextLong(60) - 10),
            ),
            Game(
                id = Random.nextLong(100),
                title = "new game3",
                status = GameStatus.PLAYED,
                myRating = 3.5f,
                lastEdit = LocalDateTime.now().plusDays(Random.nextLong(60) - 30),
            ),
        )
}