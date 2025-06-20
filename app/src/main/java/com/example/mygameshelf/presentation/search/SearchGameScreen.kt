package com.example.mygameshelf.presentation.search

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.presentation.common.NetworkImage

@Composable
fun SearchGameScreen(viewModel: SearchGameViewModel = hiltViewModel(),
                     onClickGame: (Long) -> Unit,
                     onBack: () -> Unit) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val games by viewModel.games.collectAsStateWithLifecycle()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { viewModel.onSearchTextChanged(it) },
            leadingIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.onSearchTextChanged("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            },
            label = { Text("Search game...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        if (games.isEmpty()) {
            Text("No games matching!", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        LazyColumn {
            items(games) { game ->
                GameRow(game, onClick = { onClickGame(game.igdbId!!) })
            }
        }

    }
}

@Composable
@Preview
fun GameRow(
    @PreviewParameter(GameRowPreview::class) game: Game,
    onClick: (Offset) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = onClick,
                )
            }
    ) {
        NetworkImage(
            url = game.coverSmallUrl ?: "",
            modifier = Modifier.size(width = 48.dp, height = 72.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(modifier = Modifier.align(Alignment.CenterVertically), text = game.title)
    }
}

class GameRowPreview : PreviewParameterProvider<Game> {
    override val values = sequenceOf(
        Game(
            title = "Game 1",
            igdbId = 119402,
            imageId = "co5uct"
        ),
        Game(
            title = "Game 2",
            igdbId = 12503,
            imageId = "co1t7q"
        ),
        Game(
            title = "Game 3",
            igdbId = 157580,
            imageId = "co5mw9"
        )
    )
}