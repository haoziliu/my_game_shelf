package com.example.mygameshelf.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygameshelf.R
import com.example.mygameshelf.presentation.theme.PixelFont

@Preview
@Composable
fun CustomImageButton(
    modifier: Modifier = Modifier,
    text: String = "Find Game",
    onClick: () -> Unit = {}
) {
    // 用于检测按钮是否被按下
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 按下时给按钮一个轻微缩小的效果
    val scale = if (isPressed) 0.98f else 1f

    Box(
        modifier = modifier
            // 1. 添加点击事件
            .clickable(
                interactionSource = interactionSource,
                indication = null, // 禁用默认的涟漪效果，因为我们有自己的视觉反馈
                role = Role.Button,
                onClick = onClick
            )
            // 2. 应用按压动画
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        // 3. 背景图片
        Image(
            painter = painterResource(id = R.drawable.background_paper),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // 4. 按钮文字
        Text(
            modifier = Modifier.padding(horizontal = 44.dp, vertical = 16.dp),
            text = text,
            color = Color(0xff663f13),
            fontSize = 12.sp,
            fontFamily = PixelFont
        )
    }
}