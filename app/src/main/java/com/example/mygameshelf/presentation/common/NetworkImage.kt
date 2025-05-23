package com.example.mygameshelf.presentation.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    val imageModel = url.ifBlank { null }

    AsyncImage(
        model = imageModel,
        contentDescription = contentDescription,
        modifier = modifier.clip(RoundedCornerShape(4.dp)), // ✅ 设置裁剪圆角
        contentScale = contentScale,
//        placeholder = placeholder ?: painterResource(R.drawable.placeholder), // ✅ 占位图
//        error = error ?: painterResource(R.drawable.error_placeholder) // ✅ 错误图
    )
}