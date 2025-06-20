package com.example.mygameshelf.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PaleGold,
    onPrimary = DarkBackground,
    secondary = MutedWood,
    onSecondary = DarkBackground,
    tertiary = PaleGold,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = OffWhite,
    surface = DarkSurface,
    onSurface = OffWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = BrassGold,
    onPrimary = Color.White,
    secondary = DarkWood,
    onSecondary = Color.White,
    tertiary = BrassGold,
    onTertiary = Color.White,
    background = OldPaper,
    onBackground = InkBrown,
    surface = Parchment,
    onSurface = InkBrown,
)

@Composable
fun MyGameShelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}