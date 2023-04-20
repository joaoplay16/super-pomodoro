package com.playlab.superpomodoro.ui.screen.main.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme


@Composable
fun ChatPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
       // TODO show content
    }
}

@DevicesPreviews
@Composable
fun ChatPagePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            ChatPage()
        }
    }
}