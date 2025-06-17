package com.example.mygameshelf.presentation.shelf

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mygameshelf.R
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.presentation.addgame.AddGameScreen
import com.example.mygameshelf.presentation.common.GamePreviewParameterProvider
import com.example.mygameshelf.presentation.common.NetworkImage
import com.example.mygameshelf.presentation.common.StarRatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(viewModel: ShelfViewModel = hiltViewModel(), navController: NavController) {
    val wantToPlayGames by viewModel.wantToPlayGames.collectAsStateWithLifecycle()
    val playingGames by viewModel.playingGames.collectAsStateWithLifecycle()
    val otherGames by viewModel.otherGames.collectAsStateWithLifecycle()
    var showAddGame by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val onClickGame: (Game) -> Unit = { game ->
        navController.navigate("gameDetail/${game.igdbId}")
    }
    val onLongPressedGame: (Game) -> Unit = { game ->
        viewModel.deleteGame(game)
    }
    val scrollState = rememberScrollState()

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val fontName = GoogleFont("Press Start 2P")
    val pixelFont = FontFamily(Font(googleFont = fontName, fontProvider = provider))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text("Want to play", modifier = Modifier.padding(start = 12.dp), fontFamily = pixelFont)
        if (wantToPlayGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .shadow(elevation = 4.dp)
                    .clickable { showAddGame = true },
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        } else {
            LazyRow(modifier = Modifier) {
                items(wantToPlayGames, key = { it.id }) { game ->
                    GameItem(game = game, onLongPressed = onLongPressedGame, onClick = onClickGame)
                }
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text("Playing", modifier = Modifier.padding(start = 12.dp), fontFamily = pixelFont)
        if (playingGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .shadow(elevation = 4.dp)
                    .clickable { showAddGame = true },
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        } else {
            LazyRow(modifier = Modifier) {
                items(playingGames, key = { it.id }) { game ->
                    GameItem(game = game, onLongPressed = onLongPressedGame, onClick = onClickGame)
                }
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            "Finished or dropped",
            modifier = Modifier.padding(start = 12.dp),
            fontFamily = pixelFont
        )
        if (otherGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .shadow(elevation = 4.dp)
                    .clickable { showAddGame = true },
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        } else {
            LazyRow(modifier = Modifier) {
                items(otherGames, key = { it.id }) { game ->
                    GameItem(game = game, onLongPressed = onLongPressedGame, onClick = onClickGame)
                }
            }
        }
        Spacer(Modifier.weight(1f))

        Button(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 24.dp),
            content = { Text("Find game", fontFamily = pixelFont) },
            onClick = { showAddGame = true })
    }

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
    @PreviewParameter(GamePreviewParameterProvider::class) game: Game,
    onClick: (Game) -> Unit = {},
    onLongPressed: (Game) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .clickable(onClick = { onClick(game) })
    ) {
        NetworkImage(
            url = game.coverBigUrl ?: "",
            modifier = Modifier.size(width = 90.dp, height = 120.dp)
        )
        Box(
            modifier = Modifier
                .height(60.dp)
                .width(90.dp), contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = game.title,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        StarRatingBar(rating = game.myRating ?: 0.0f)
    }
}