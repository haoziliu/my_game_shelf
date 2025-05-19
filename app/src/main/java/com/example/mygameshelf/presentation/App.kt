package com.example.mygameshelf.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mygameshelf.presentation.addgame.AddGameScreen
import com.example.mygameshelf.presentation.shelf.ShelfScreen
import com.example.mygameshelf.presentation.startup.AppLauncherScreen

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "launcher") {
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
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    ShelfScreen()
                }
            }
        }
        composable("addGame") {
            AddGameScreen()
        }
    }

}

