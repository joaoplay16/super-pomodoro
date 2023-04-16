package com.playlab.superpomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.playlab.superpomodoro.ui.screen.PomodoroViewModel
import com.playlab.superpomodoro.ui.screen.ScreenRoutes
import com.playlab.superpomodoro.ui.screen.main.MainScreen
import com.playlab.superpomodoro.ui.screen.settings.SettingsScreen
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.SoundEffects
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val pomodoroViewModel: PomodoroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val soundEffects = SoundEffects(this)
        setContent {
            SuperPomodoroTheme(false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {
                    DefaultNavHost(
                        pomodoroViewModel = pomodoroViewModel
                    )
                }
            }

            val pomodoroDuration by pomodoroViewModel.pomodoroDurationPreference
                .collectAsState(initial = null)
            val shortBreakDuration by pomodoroViewModel.shortBreakDurationPreference
                .collectAsState(initial = null)
            val longBreakDuration by pomodoroViewModel.longBreakDurationPreference
                .collectAsState(initial = null)

            pomodoroViewModel.initPomodoroValues(
                pomodoroDuration = pomodoroDuration,
                shortBreakDuration = shortBreakDuration,
                longBreakDuration = longBreakDuration
            )
        }
    }
}

@Composable
fun DefaultNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ScreenRoutes.Main.name,
    pomodoroViewModel: PomodoroViewModel?
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(ScreenRoutes.Main.name){
            MainScreen(
                onSettingsClick = {
                    navController.navigate(ScreenRoutes.Settings.name)
                },
                pomodoroViewModel = pomodoroViewModel
            )
        }

        composable(ScreenRoutes.Settings.name){
            SettingsScreen(
                onArrowBackPressed = {
                    navController.popBackStack()
                },
                pomodoroViewModel = pomodoroViewModel
            )
        }
    }
}
