package com.playlab.superpomodoro.ui.screen.gameview

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun GameView(
    modifier: Modifier = Modifier,
    url: String
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.domStorageEnabled =true
                loadUrl(url)
            }
        },
        update = { view ->
            view.loadUrl(url)
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