package com.playlab.superpomodoro.ui.screen.main

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import com.playlab.superpomodoro.model.Game
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.ui.components.HomeTabBar
import com.playlab.superpomodoro.ui.components.TabPage
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.PomodoroViewModel
import com.playlab.superpomodoro.ui.screen.TimerStatus
import com.playlab.superpomodoro.ui.screen.main.pages.ChatPage
import com.playlab.superpomodoro.ui.screen.main.pages.GamesPage
import com.playlab.superpomodoro.ui.screen.main.pages.HomePage
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    onSettingsClick: () -> Unit,
    onGameSelected: (Game) -> Unit,
    pomodoroViewModel: PomodoroViewModel?,
    chatViewModel: ChatViewModel?,
    onSignUpClick: () -> Unit,
    onGroupSelected: (Group) -> Unit
) {
    var selectedTab by rememberSaveable{ mutableStateOf(TabPage.Home) }

    val isPomodoroRunning = pomodoroViewModel?.isRunning

    val disabledTabs = remember { mutableStateListOf<TabPage>() }

    val pomodoroTimerStatus = pomodoroViewModel?.timerStatus

    val lifeCycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = isPomodoroRunning, block = {
        if(isPomodoroRunning == true){
            pomodoroTimerStatus?.observe(lifeCycleOwner){ timerStatus ->
                // enable tabs when pomodoro is in break time
                if(timerStatus in arrayOf(TimerStatus.LONG_BREAK, TimerStatus.SHORT_BREAK))
                    disabledTabs.clear()
                // disable chat and games tabs
                else disabledTabs.addAll(listOf(TabPage.Chat, TabPage.Games))
            }
        }else{
            // timer is stopped, enable all tabs
            disabledTabs.clear()
        }
    })

    Scaffold(topBar = {
        HomeTabBar(
            tabPage = selectedTab,
            onTabSelected = { selectedTab = it },
            disabledTabs = disabledTabs
        )
    }){
        when(selectedTab){
            TabPage.Home -> HomePage(
                onSettingsClick = onSettingsClick,
                pomodoroViewModel = pomodoroViewModel
            )
            TabPage.Chat -> ChatPage(
                onSignUpClick = onSignUpClick,
                onGroupSelected = onGroupSelected,
                chatViewModel = chatViewModel
            )
            else -> GamesPage(onGameSelected = onGameSelected)
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    SuperPomodoroTheme(false) {
        Surface {
            MainScreen(
                onSettingsClick = {},
                onGameSelected = {},
                pomodoroViewModel = null,
                chatViewModel  = null,
                onGroupSelected = {},
                onSignUpClick = {}
            )
        }
    }
}