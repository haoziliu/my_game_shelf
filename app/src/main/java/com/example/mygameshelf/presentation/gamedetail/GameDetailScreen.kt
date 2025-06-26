package com.example.mygameshelf.presentation.gamedetail

import SkeuomorphicImagePlate
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mygameshelf.R
import com.example.mygameshelf.core.Utils
import com.example.mygameshelf.domain.model.Game
import com.example.mygameshelf.domain.model.GameStatus
import com.example.mygameshelf.presentation.common.ArtworkGallery
import com.example.mygameshelf.presentation.common.GamePreviewParameterProvider
import com.example.mygameshelf.presentation.common.NetworkImage
import com.example.mygameshelf.presentation.common.StarRatingBar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(viewModel: GameDetailViewModel, onBack: () -> Unit) {
    val gameDetail by viewModel.gameDetail.collectAsStateWithLifecycle()
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
    var showGallery by remember { mutableStateOf(false) }
    var selectedArtworkIndex by remember { mutableStateOf(0) }

    gameDetail?.let { game ->
        GameDetail(game, onClickEdit = {
            showEdit = true
        }, onBack = onBack, onClickArtwork = { index ->
            showGallery = true
            selectedArtworkIndex = index
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
                Spacer(Modifier.height(20.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    GameStatus.entries.filter { it != GameStatus.UNKNOWN }.forEach { status ->
                        val isSelected = (status == editStatus)
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.setStatus(status) }
                                // 方案：为选中的按钮添加一个醒目的边框
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(2.dp) // 让边框和内容之间有点距离
                        ) {
                            SkeuomorphicImagePlate(
                                painter = painterResource(id = R.drawable.background_brass),
                                text = status.displayName,
                                elevation = 2.dp,
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

    if (showGallery && gameDetail?.artworksId?.isNotEmpty() == true) {
        ArtworkGallery(
            artworks = gameDetail!!.artworksId!!,
            initialIndex = selectedArtworkIndex,
            onDismiss = { showGallery = false }
        )
    }

}

@Composable
@Preview(showBackground = true)
fun GameDetail(
    @PreviewParameter(GamePreviewParameterProvider::class) game: Game,
    onClickEdit: () -> Unit = {},
    onBack: () -> Unit = {},
    onClickArtwork: (Int) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            NetworkImage(
                url = Utils.coverBigUrl(game.imageId) ?: "",
                modifier = Modifier
                    .size(width = 220.dp, height = 293.dp)
                    .align(Alignment.Center)
                    .shadow(elevation = 4.dp)
            )
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            }
        }
        Text(
            game.title,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClickEdit)
                .padding(vertical = 12.dp),
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
                SkeuomorphicImagePlate(
                    modifier = Modifier.wrapContentSize(),
                    painter = painterResource(id = R.drawable.background_brass),
                    text = game.status.displayName,
                    elevation = 2.dp
                )
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

        game.artworksId?.let { artworksId ->
            Spacer(Modifier.size(16.dp))
            Text("Artworks:")
            LazyRow {
                itemsIndexed(artworksId) { index, imageId ->
                    NetworkImage(
                        url = Utils.screenshotBigUrl(imageId) ?: "",
                        modifier = Modifier
                            .width(256.dp)
                            .height(144.dp)
                            .padding(4.dp)
                            .clickable(onClick = { onClickArtwork(index) })
                    )
                }
            }
        }

        game.storyline?.let {
            Spacer(Modifier.size(16.dp))
            Text("Storyline:")
            Text(it)
        }

    }
}