package com.example.mygameshelf.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mygameshelf.presentation.gamedetail.GameDetailScreen
import com.example.mygameshelf.presentation.gamedetail.GameDetailViewModel
import com.example.mygameshelf.presentation.search.SearchGameScreen
import com.example.mygameshelf.presentation.shelf.ShelfScreen
import com.example.mygameshelf.presentation.startup.AppLauncherScreen
import noiseBackground

@Composable
fun App() {
    val navController = rememberNavController()
    val onClickGame: (Long) -> Unit = { igdbId ->
        navController.navigate("gameDetail/${igdbId}")
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .noiseBackground(
                baseColor = MaterialTheme.colorScheme.background,
                noiseColor = Color.Black.copy(alpha = 0.4f),
                noiseDensity = 0.08f
            ),
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "launcher",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("launcher") {
                AppLauncherScreen(
                    onReady = {
                        navController.navigate("main") {
                            popUpTo("launcher") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("main") {
                ShelfScreen(onClickAdd = {
                    navController.navigate("searchGame")
                }, onClickGame = onClickGame)
            }
            composable("searchGame") {
                SearchGameScreen(onClickGame = onClickGame, onBack = { navController.popBackStack() })
            }
            composable("gameDetail/{igdbId}") { backStackEntry ->
                val detailVm: GameDetailViewModel = hiltViewModel(backStackEntry)
                GameDetailScreen(detailVm, onBack = { navController.popBackStack() })
            }
        }
    }
}

