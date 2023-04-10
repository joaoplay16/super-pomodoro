package com.playlab.superpomodoro.ui.screen.main.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.animation.RemainingTimeAnimation
import com.playlab.superpomodoro.ui.components.PomodoroDone
import com.playlab.superpomodoro.ui.components.ResumeButton
import com.playlab.superpomodoro.ui.components.StopButton
import com.playlab.superpomodoro.ui.components.TimeText
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.Purple400
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp), Arrangement.End){
            IconButton(onClick =  onSettingsClick ) {
                Icon(
                    modifier = Modifier.padding(16.dp),
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "",
                    tint = Purple400
                    )
            }
        }
        RemainingTimeAnimation(
            canvasSize = 250.dp,
            value = 90,
            maxValue = 100
        )

        Spacer(Modifier.padding(8.dp))
        PomodoroDone()
        Spacer(Modifier.padding(12.dp))

        Row {
           StopButton(onClick = {})
            Spacer(Modifier.padding(end = 8.dp))
            ResumeButton(onClick = {})
            Spacer(Modifier.padding(end = 8.dp))
//            PauseButton(onClick = {})
        }
        Spacer(Modifier.padding(18.dp))

        Text(
            text = stringResource(id = R.string.pomodoro),
            style = MaterialTheme.typography.subtitle1
        )

        Spacer(Modifier.padding(8.dp))

        TimeText(text = "23:59")
    }
}


@DevicesPreviews
@Composable
fun HomePagePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            HomePage(onSettingsClick = {})
        }
    }
}