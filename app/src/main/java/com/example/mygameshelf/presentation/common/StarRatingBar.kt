package com.example.mygameshelf.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
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
    rating: Float,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Dp = 12.dp,
    tint: Color = Color(0xFFFFD700)
) {
    val fullStars = floor(rating).toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5f
    Row(modifier = modifier) {
        for (i in 1..starCount) {
            when {
                i <= fullStars -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(starSize)
                    )
                }

                i == fullStars + 1 && hasHalfStar -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(starSize)
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(starSize)
                    )
                }
            }
        }
    }
}
