package com.playlab.superpomodoro.ui.screen.main.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.screen.groups.GroupsScreen
import com.playlab.superpomodoro.ui.screen.login.LoginScreen
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel?,
    onSignUpClick: () -> Unit,
    onGroupSelected: (Group) -> Unit
) {

    LaunchedEffect(key1 = Unit, block = {
        chatViewModel?.getCurrentUser()
    })

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        if (chatViewModel?.isLoggedIn?.value == true) {
            GroupsScreen(
                chatViewModel = chatViewModel,
                onGroupSelected = onGroupSelected
            )
        } else {
            LoginScreen(
                modifier = Modifier.fillMaxSize(0.8f),
                chatViewModel = chatViewModel,
                onSignUpLabelClick = onSignUpClick
            )
        }
    }
}

@DevicesPreviews
@Composable
fun ChatPagePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            ChatPage(onSignUpClick = {}, onGroupSelected = {}, chatViewModel = null)
        }
    }
}