package com.example.mygameshelf.presentation.shelf

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.presentation.addgame.AddGameScreen
import com.example.mygameshelf.presentation.common.GamePreviewParameterProvider
import com.example.mygameshelf.presentation.common.StarRatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(viewModel: ShelfViewModel = hiltViewModel(), navController: NavController) {
    val completedGames by viewModel.completedGames.collectAsStateWithLifecycle()
    val wantToPlayGames by viewModel.wantToPlayGames.collectAsStateWithLifecycle()
    var showAddGame by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow {
            items(wantToPlayGames, key = { it.id }) { game ->
                GameItem(game = game, onLongPressed = {
                    viewModel.deleteGame(game)
                })
            }
        }
        LazyRow {
            items(completedGames, key = { it.id }) { game ->
                GameItem(game = game, onLongPressed = {
                    viewModel.deleteGame(game)
                })
            }
        }

        Spacer(Modifier.weight(1f))

        Button(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 24.dp),
            content = { Text("Find game") },
            onClick = { showAddGame = true })
    }

//    Button(
//        content = { Text("Find game") },
//        onClick = {
//            val number = Random.nextLong(100)
//            viewModel.addGame(
//                Game(
//                    title = "new game $number",
//                    status = GameStatus.COMPLETED,
//                    rating = Random.nextFloat() * 5,
//                    lastEdit = LocalDate.now(),
//                )
//            )
//            viewModel.addGame(
//                Game(
//                    title = "new game $number",
//                    status = GameStatus.WANT_TO_PLAY,
//                    rating = Random.nextFloat() * 5,
//                    lastEdit = LocalDate.now(),
//                )
//            )
//        }
//    )

    if (showAddGame) {
        ModalBottomSheet(
            onDismissRequest = { showAddGame = false },
            sheetState = sheetState,
        ) {
            AddGameScreen(onGameClick = { igdbId ->
                showAddGame = false
                navController.navigate("gameDetail/$igdbId")
            })
        }
    }


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
        StarRatingBar(rating = game.myRating ?: 0.0f)
    }
}