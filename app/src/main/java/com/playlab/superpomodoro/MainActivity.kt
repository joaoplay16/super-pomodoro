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
import com.playlab.superpomodoro.model.Game
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.PomodoroViewModel
import com.playlab.superpomodoro.ui.screen.ScreenRoutes
import com.playlab.superpomodoro.ui.screen.conversation.ConversationScreen
import com.playlab.superpomodoro.ui.screen.gameview.GameView
import com.playlab.superpomodoro.ui.screen.groupoverview.GroupOverviewScreen
import com.playlab.superpomodoro.ui.screen.main.MainScreen
import com.playlab.superpomodoro.ui.screen.settings.SettingsScreen
import com.playlab.superpomodoro.ui.screen.signup.SignupScreen
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.SoundEffects
import com.playlab.superpomodoro.util.VibratorUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val pomodoroViewModel: PomodoroViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

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
                        pomodoroViewModel = pomodoroViewModel,
                        chatViewModel = chatViewModel
                    )
                }
            }

            val pomodoroDurationPreference by pomodoroViewModel.pomodoroDurationPreference
                .collectAsState(initial = null)
            val shortBreakDurationPreference by pomodoroViewModel.shortBreakDurationPreference
                .collectAsState(initial = null)
            val longBreakDurationPreference by pomodoroViewModel.longBreakDurationPreference
                .collectAsState(initial = null)

            val isSoundAllowedPreference = pomodoroViewModel.soundPreference
                .collectAsState(null).value
            val isVibrationAllowedPreference = pomodoroViewModel.vibrationPreference
                .collectAsState(null).value

            pomodoroViewModel.initPomodoroValues(
                pomodoroDuration = pomodoroDurationPreference,
                shortBreakDuration = shortBreakDurationPreference,
                longBreakDuration = longBreakDurationPreference,
                isSoundAllowed = isSoundAllowedPreference,
                isVibrationAllowed = isVibrationAllowedPreference
            )

            val isRunning = pomodoroViewModel.isRunning
            val isSoundAllowed by pomodoroViewModel.isSoundAllowed
            val isVibrationAllowed by pomodoroViewModel.isVibrationAllowed

            pomodoroViewModel.endOfCycle.observe(this@MainActivity){
                it?.let {
                    if (isRunning){
                        if (isSoundAllowed) {
                            soundEffects.playSound(R.raw.microwave_oven_notif)
                        }
                        if (isVibrationAllowed) {
                            VibratorUtil.vibrate(this, 200)
                        }
                    }
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
    pomodoroViewModel: PomodoroViewModel?,
    chatViewModel: ChatViewModel?
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
                onGameSelected = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("game", it)
                    navController.navigate(ScreenRoutes.GameView.name)
                },
                onSignUpClick = {
                    navController.navigate(ScreenRoutes.ChatSignup.name)
                },
                onGroupSelected = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("group", it)
                    navController.navigate(ScreenRoutes.Conversation.name)
                },
                pomodoroViewModel = pomodoroViewModel,
                chatViewModel = chatViewModel
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

        composable(ScreenRoutes.GameView.name){
            val game = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Game>("game")

            game?.let {

            GameView(url = game.url)
            }
        }

        composable(ScreenRoutes.ChatSignup.name){
            SignupScreen(
                onArrowBackPressed = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(ScreenRoutes.Conversation.name){
            val group = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Group>("group")

            group?.let {
                ConversationScreen(
                    group = group,
                    onArrowBackPressed = {
                        navController.popBackStack()
                    },
                    onGroupNameClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("group", it)
                        navController.navigate(ScreenRoutes.GroupOverView.name)
                    },
                    onLeaveGroup = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(ScreenRoutes.GroupOverView.name) {
            val group = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Group>("group")

            group?.let {
                GroupOverviewScreen(
                    group = group,
                    chatViewModel = chatViewModel,
                    onArrowBackPressed = {
                        navController.popBackStack()
                    },
                    onDeleteGroup = {
                        navController.popBackStack(
                            route = ScreenRoutes.Main.name,
                            inclusive = false
                        )
                    },
                )
            }
        }
    }
}
