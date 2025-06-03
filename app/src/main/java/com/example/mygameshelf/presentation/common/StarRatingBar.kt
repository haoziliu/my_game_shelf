package com.example.mygameshelf.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor

@Composable
fun StarRatingBar(
    rating: Float, // [0, 1]
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Dp = 12.dp,
    tint: Color = Color(0xFFFFD700),
    onRatingChanged: ((Float) -> Unit)? = null,
) {
    val ratingStars = rating * starCount
    val fullStars = floor(ratingStars).toInt()
    val hasHalfStar = (ratingStars - fullStars) >= 0.5f
    Row(modifier = modifier) {
        for (i in 1..starCount) {
            val icon = when {
                i <= fullStars -> Icons.Filled.Star
                i == fullStars + 1 && hasHalfStar -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .size(starSize)
                    .then(if (onRatingChanged != null) {
                        Modifier.clickable {
                            onRatingChanged((1f * i / starCount))
                        }
                    } else {
                        Modifier
                    })
            )
        }
    }
}
