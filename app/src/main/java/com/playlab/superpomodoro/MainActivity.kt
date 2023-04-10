package com.playlab.superpomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.playlab.superpomodoro.ui.screen.ScreenRoutes
import com.playlab.superpomodoro.ui.screen.main.MainScreen
import com.playlab.superpomodoro.ui.screen.settings.SettingsScreen
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperPomodoroTheme(false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {
                    DefaultNavHost()
                }
            }
        }
    }
}

@Composable
fun DefaultNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ScreenRoutes.Main.name,
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(ScreenRoutes.Main.name){
            MainScreen(onSettingsClick = {
                navController.navigate(ScreenRoutes.Settings.name)
            })
        }

        composable(ScreenRoutes.Settings.name){
            SettingsScreen(onArrowBackPressed = {
                navController.popBackStack()
            })
        }
    }
}
