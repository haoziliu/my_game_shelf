package com.example.mygameshelf.presentation.shelf

import SkeuomorphicImagePlate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.mygameshelf.R
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.presentation.addgame.AddGameScreen
import com.example.mygameshelf.presentation.common.CustomImageButton
import com.example.mygameshelf.presentation.common.GameListPreviewParameterProvider
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
    val onClickAdd: () -> Unit = {
        showAddGame = true
    }
    val onClickGame: (Game) -> Unit = { game ->
        navController.navigate("gameDetail/${game.igdbId}")
    }
    val onLongPressedGame: (Game) -> Unit = { game ->
        viewModel.deleteGame(game)
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Shelf(
            gameList = wantToPlayGames,
            "Want to play",
            onClickAdd = onClickAdd,
            onClickGame = onClickGame,
            onLongPressedGame = onLongPressedGame
        )
        Spacer(modifier = Modifier.size(48.dp))
        Shelf(
            gameList = playingGames,
            "Playing",
            onClickAdd = onClickAdd,
            onClickGame = onClickGame,
            onLongPressedGame = onLongPressedGame
        )
        Spacer(modifier = Modifier.size(48.dp))
        Shelf(
            gameList = otherGames,
            "Finished",
            onClickAdd = onClickAdd,
            onClickGame = onClickGame,
            onLongPressedGame = onLongPressedGame
        )
        Spacer(Modifier.weight(1f))

        CustomImageButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 48.dp, bottom = 24.dp),
            text = "Find game",
            onClick = onClickAdd
        )
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
fun Shelf(
    @PreviewParameter(GameListPreviewParameterProvider::class) gameList: List<Game>,
    title: String = "Shelf title",
    onClickAdd: () -> Unit = {},
    onClickGame: (Game) -> Unit = {},
    onLongPressedGame: (Game) -> Unit = {}
) {
    val brassPlatePainter = painterResource(id = R.drawable.background_brass)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SkeuomorphicImagePlate(
            modifier = Modifier,
            painter = brassPlatePainter,
            text = title,
            elevation = 2.dp
        )
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_shelf),
                    contentDescription = "Shelf background",
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 40.dp),
                    contentScale = ContentScale.FillBounds,
                )
                if (gameList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                top = 20.dp
                            )
                            .shadow(elevation = 2.dp)
                            .clickable(onClick = onClickAdd),
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
                    LazyRow(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp
                        )
                    ) {
                        items(gameList, key = { it.id }) { game ->
                            GameItem(
                                game = game,
                                onLongPressed = onLongPressedGame,
                                onClick = onClickGame
                            )
                        }
                    }
                }
            }
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
    var showTitleTooltip by remember { mutableStateOf(false) }
    var barYPosition by remember {
        mutableStateOf(0)
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick(game) },
                    onLongPress = { showTitleTooltip = true }
                )
            }
    ) {
        if (showTitleTooltip) {
            Popup(
                onDismissRequest = { showTitleTooltip = false },
                offset = IntOffset(0, y = -barYPosition),
                properties = PopupProperties(
                    clippingEnabled = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.background_paper),
                        contentDescription = "Shelf background",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.FillBounds,
                    )
                    Column(
                        modifier = Modifier
                            .width(180.dp)
                            .padding(start = 8.dp, end = 8.dp)
                            .onPlaced {
                                barYPosition = it.size.height
                            }, horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = game.title,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        StarRatingBar(rating = game.myRating ?: 0.0f)
                    }
                }
            }
        }
        NetworkImage(
            url = game.coverBigUrl ?: "",
            modifier = Modifier.size(width = 90.dp, height = 120.dp)
        )
//        Column (
//            modifier = Modifier
//                .height(60.dp)
//                .width(90.dp), contentAlignment = Alignment.CenterStart
//        ) {
//            Text(
//                text = game.title,
//                fontSize = 12.sp,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis,
//            )
//        }
//        StarRatingBar(rating = game.myRating ?: 0.0f)
    }
}