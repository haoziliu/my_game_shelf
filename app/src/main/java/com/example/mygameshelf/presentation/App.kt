package com.example.mygameshelf.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mygameshelf.presentation.gamedetail.GameDetailScreen
import com.example.mygameshelf.presentation.gamedetail.GameDetailViewModel
import com.example.mygameshelf.presentation.shelf.ShelfScreen
import com.example.mygameshelf.presentation.startup.AppLauncherScreen

@Composable
fun App() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                ShelfScreen(navController = navController)
            }
            composable("gameDetail/{igdbId}") { backStackEntry ->
                val detailVm: GameDetailViewModel = hiltViewModel(backStackEntry)
                GameDetailScreen(detailVm)
            }
        }
    }
}

