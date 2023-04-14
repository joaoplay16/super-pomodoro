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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.animation.RemainingTimeAnimation
import com.playlab.superpomodoro.ui.components.PauseButton
import com.playlab.superpomodoro.ui.components.PomodoroDone
import com.playlab.superpomodoro.ui.components.ResumeButton
import com.playlab.superpomodoro.ui.components.StopButton
import com.playlab.superpomodoro.ui.components.TimeText
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.screen.PomodoroViewModel
import com.playlab.superpomodoro.ui.screen.TimerStatus
import com.playlab.superpomodoro.ui.theme.Purple400
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.SoundEffects
import com.playlab.superpomodoro.util.TimeUtil

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    pomodoroViewModel: PomodoroViewModel?
) {

    val pomodoroCount = pomodoroViewModel?.pomodoroCount
    val timerStatus = pomodoroViewModel?.timerStatus?.value
    val timeLeft = pomodoroViewModel?.timeLeft

    val isRunning = pomodoroViewModel?.isRunning

    val currentTimerValue = remember {
        mutableStateMapOf(
            TimerStatus.POMODORO to pomodoroViewModel?.pomodoroDuration,
            TimerStatus.SHORT_BREAK to pomodoroViewModel?.shortBreakDuration,
            TimerStatus.LONG_BREAK to pomodoroViewModel?.longBreakDuration
        )
    }

    val context = LocalContext.current
    val localLifecycleOwner = LocalLifecycleOwner.current

    val soundEffects = remember {
        SoundEffects(context)
    }

    LaunchedEffect(key1 = null){
        pomodoroViewModel?.timerStatus?.observe(localLifecycleOwner){
            if (pomodoroViewModel.isRunning){
                soundEffects.playSound(R.raw.microwave_oven_notif)
            }
        }
    }

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
            value = timeLeft?.toInt() ?: 0,
            maxValue = currentTimerValue[timerStatus]?.toInt() ?: 0
        )

        Spacer(Modifier.padding(8.dp))
        PomodoroDone(done = pomodoroCount ?: 0)
        Spacer(Modifier.padding(12.dp))

        Row {

            StopButton(
                onClick = {
                    pomodoroViewModel?.stopTimer()
                    pomodoroViewModel?.setRunning(false)
                })
            Spacer(Modifier.padding(end = 8.dp))

            if(isRunning == true){
                PauseButton(onClick = {
                    pomodoroViewModel.pauseTimer()
                    pomodoroViewModel.setRunning(false)
                })
            }else{
                ResumeButton(onClick = {
                    pomodoroViewModel?.startTimer()
                    pomodoroViewModel?.setRunning(true)
                })
            }

        }
        Spacer(Modifier.padding(18.dp))

        val status = when(timerStatus){
            TimerStatus.LONG_BREAK -> stringResource(id = R.string.pomodoro_Long_break)
            TimerStatus.SHORT_BREAK -> stringResource(id = R.string.pomodoro_short_break)
            else -> stringResource(id = R.string.pomodoro)
        }

        Text(
            text = status,
            style = MaterialTheme.typography.subtitle1
        )

        Spacer(Modifier.padding(8.dp))

        TimeText(text = TimeUtil.getFormattedTimeString(timeLeft ?: 0))
    }
}


@DevicesPreviews
@Composable
fun HomePagePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            val pomodoroViewModel: PomodoroViewModel = viewModel()

            HomePage(
                onSettingsClick = {},
                pomodoroViewModel = pomodoroViewModel
            )
        }
    }
}