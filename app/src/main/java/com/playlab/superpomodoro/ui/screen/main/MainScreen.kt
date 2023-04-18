package com.playlab.superpomodoro.ui.screen.main

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.playlab.superpomodoro.model.Game
import com.playlab.superpomodoro.ui.components.HomeTabBar
import com.playlab.superpomodoro.ui.components.TabPage
import com.playlab.superpomodoro.ui.screen.PomodoroViewModel
import com.playlab.superpomodoro.ui.screen.main.pages.ChatPage
import com.playlab.superpomodoro.ui.screen.main.pages.GamesPage
import com.playlab.superpomodoro.ui.screen.main.pages.HomePage
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    onSettingsClick: () -> Unit,
    onGameSelected: (Game) -> Unit,
    pomodoroViewModel: PomodoroViewModel?
) {
    var selectedTab by remember{ mutableStateOf(TabPage.Home) }

    Scaffold(topBar = {
        HomeTabBar(
            tabPage = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }){
        when(selectedTab){
            TabPage.Home -> HomePage(
                onSettingsClick = onSettingsClick,
                pomodoroViewModel = pomodoroViewModel
            )
            TabPage.Chat -> ChatPage()
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
                pomodoroViewModel = null
            )
        }
    }
}