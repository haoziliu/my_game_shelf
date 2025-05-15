package com.example.mygameshelf.presentation.shelf

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.presentation.common.StarRatingBar
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun ShelfScreen(viewModel: ShelfViewModel = hiltViewModel()) {
    val completedGames by viewModel.completedGames.collectAsStateWithLifecycle()
    val wantToPlayGames by viewModel.wantToPlayGames.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow {
            items(wantToPlayGames) { game ->
                GameItem(game = game, onLongPressed = {
                    viewModel.deleteGame(game)
                })
            }
        }
        LazyRow {
            items(completedGames) { game ->
                GameItem(game = game, onLongPressed = {
                    viewModel.deleteGame(game)
                })
            }
        }
    }

    ExtendedFloatingActionButton(
        text = { Text("add") },
        icon = { Icons.Filled.Add },
        onClick = {
            val number = Random.nextLong(100)
            viewModel.addGame(
                Game(
                    title = "new game $number",
                    status = GameStatus.COMPLETED,
                    rating = Random.nextFloat() * 5,
                    lastEdit = LocalDate.now(),
                )
            )
            viewModel.addGame(
                Game(
                    title = "new game $number",
                    status = GameStatus.WANT_TO_PLAY,
                    rating = Random.nextFloat() * 5,
                    lastEdit = LocalDate.now(),
                )
            )
        }
    )

}


@Preview(showBackground = true)
@Composable
fun GameItem(
    modifier: Modifier = Modifier,
    @PreviewParameter(GamePreviewParameterProvider::class) game: Game,
    onLongPressed: (Offset) -> Unit = {}
) {
    Column(modifier = Modifier
        .padding(12.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {

                },
                onLongPress = onLongPressed
            )
        }) {
        Box(modifier = Modifier.size(width = 64.dp, height = 96.dp)) {

        }
        Text(
            text = game.title,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        StarRatingBar(rating = game.rating ?: 0.0f)
    }
}

class GamePreviewParameterProvider : PreviewParameterProvider<Game> {
    override val values: Sequence<Game>
        get() = sequenceOf(
            Game(
                id = Random.nextLong(100),
                title = "new game",
                status = GameStatus.COMPLETED,
                rating = 5.0f,
                lastEdit = LocalDate.now(),
            ),
            Game(
                id = Random.nextLong(100),
                title = "new game2",
                status = GameStatus.COMPLETED,
                rating = 4.0f,
                lastEdit = LocalDate.now().plusDays(Random.nextLong(60) - 10),
            ),
            Game(
                id = Random.nextLong(100),
                title = "new game3",
                status = GameStatus.COMPLETED,
                rating = 3.5f,
                lastEdit = LocalDate.now().plusDays(Random.nextLong(60) - 30),
            ),
        )
}