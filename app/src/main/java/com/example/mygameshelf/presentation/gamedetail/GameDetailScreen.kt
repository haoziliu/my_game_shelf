package com.example.mygameshelf.presentation.gamedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.presentation.common.GamePreviewParameterProvider
import com.example.mygameshelf.presentation.common.NetworkImage
import com.example.mygameshelf.presentation.common.StarRatingBar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(viewModel: GameDetailViewModel) {
//    val localGame by viewModel.localGame.collectAsStateWithLifecycle()
    val gameDetail by viewModel.gameDetail.collectAsStateWithLifecycle()
    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsStateWithLifecycle()
    var showEdit by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    val editStatus by viewModel.editStatus.collectAsStateWithLifecycle()
    val editRating by viewModel.editMyRating.collectAsStateWithLifecycle()
    val hasChangesState by rememberUpdatedState(viewModel.hasUnsavedChanges.collectAsState().value)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue: SheetValue ->
            newValue != SheetValue.Hidden || !hasChangesState
        }
    )

    gameDetail?.let { game ->
        GameDetail(game, onClickEdit = {
            showEdit = true
        })
    } ?: run {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }

    if (showEdit) {
        ModalBottomSheet(sheetState = sheetState, onDismissRequest = {
            if (!hasChangesState) showEdit = false
            else showDiscardDialog = true
        }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StarRatingBar(
                        rating = editRating ?: 0.0f,
                        starSize = 40.dp,
                        onRatingChanged = { newRating ->
                            viewModel.setRating(newRating)
                        })
                }

                GameStatus.entries.forEach { status ->
                    if (status != GameStatus.UNKNOWN) {
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.setStatus(status) }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = status.name,
                                color = if (status == editStatus) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        if (!hasChangesState) showEdit = false
                        else showDiscardDialog = true
                    }) { Text("Cancel") }

                    TextButton(onClick = {
                        viewModel.saveChanges()
                        showEdit = false
                    }) { Text("Save") }
                }
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showEdit = false
                    viewModel.resetEdits()
                    showDiscardDialog = false
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                }) { Text("Keep editing") }
            },
            text = { Text("Discard your changes?") }
        )
    }

}

@Composable
@Preview(showBackground = true)
fun GameDetail(
    @PreviewParameter(GamePreviewParameterProvider::class) game: Game,
    onClickEdit: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row {
            NetworkImage(
                url = game.coverBigUrl ?: "",
                modifier = Modifier.size(width = 90.dp, height = 120.dp)
            )
            Spacer(Modifier.size(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Text(text = game.title, fontSize = 18.sp)
                Text(game.genre ?: "")
            }
        }

        Spacer(Modifier.size(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClickEdit)
                .padding(top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier) {
                Row {
                    Text("My rating:")
                    StarRatingBar(
                        rating = game.myRating ?: 0.0f,
                        starSize = 20.dp
                    )
                }
                if (game.lastEdit != null) {
                    Text("Update: " + game.lastEdit!!.format(DateTimeFormatter.ISO_LOCAL_DATE))
                }
            }
            Spacer(Modifier.weight(1f))
            if (game.status != GameStatus.UNKNOWN) {
                Text(game.status.name)
            }
        }

        game.rating?.let {
            Spacer(Modifier.size(16.dp))
            Text("IGDB rating: $it / 100")
            StarRatingBar(rating = it / 100.0f, starSize = 20.dp)
        }

        Spacer(Modifier.size(16.dp))

        game.summary?.let {
            Spacer(Modifier.size(16.dp))
            Text("Summary:")
            Text(it)
        }

        game.storyline?.let {
            Spacer(Modifier.size(16.dp))
            Text("Storyline:")
            Text(it)
        }

    }
}