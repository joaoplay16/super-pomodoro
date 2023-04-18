package com.playlab.superpomodoro.ui.screen.gameview

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun GameView(
    modifier: Modifier = Modifier,
    url: String
) {
    val state = rememberWebViewState(url)

    WebView(
        modifier = modifier,
        state = state,
        onCreated = {
            it.settings.domStorageEnabled = true
            it.settings.javaScriptEnabled = true
        }
    )
}

@Preview
@Composable
fun GameViewPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            GameView(url = "http://google.com")
        }
    }
}