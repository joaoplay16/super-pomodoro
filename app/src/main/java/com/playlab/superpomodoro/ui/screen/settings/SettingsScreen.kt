package com.playlab.superpomodoro.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.components.SettingsCheckBox
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.components.TimeInput
import com.playlab.superpomodoro.ui.components.TimeSelectorDialog
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.screen.PomodoroViewModel
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onArrowBackPressed: ()  -> Unit,
    pomodoroViewModel: PomodoroViewModel?
) {
    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                onArrowBackPressed()
                            },
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrow_back_icon_content_description)
                    )
                    TextLabel(
                        text = stringResource(id = R.string.settings_screen_title),
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = dimensionResource(id = R.dimen.screen_title_font_size).value.sp
                    )
                }
            }
        }
    ){ paddingValues ->

        var enableSound by remember { mutableStateOf(false) }
        var enableVibration by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val pomodoroLabel = remember { R.string.pomodoro }
            val shortBreakLabel =  remember { R.string.pomodoro_short_break }
            val longBreakLabel = remember { R.string.pomodoro_Long_break }

            val pomodoroValue = pomodoroViewModel?.timeLeft?.milliseconds?.inWholeMinutes?.toInt() ?: 0
            val shortBreakValue = pomodoroViewModel?.shortBreakDuration?.milliseconds?.inWholeMinutes?.toInt() ?: 0
            val longBreakValue = pomodoroViewModel?.longBreakDuration?.milliseconds?.inWholeMinutes?.toInt() ?: 0

            var showInputTimeDialog by remember {
                mutableStateOf(Pair(0, false))
            }

            if(showInputTimeDialog.second){
                TimeSelectorDialog(
                    label = stringResource(id = showInputTimeDialog.first).uppercase(),
                    value = when(showInputTimeDialog.first){
                        shortBreakLabel -> shortBreakValue
                        longBreakLabel -> longBreakValue
                        else -> pomodoroValue
                    },
                    onValueChange = { time ->
                        when(showInputTimeDialog.first){
                            shortBreakLabel -> pomodoroViewModel?.setShortBreakDuration(time.minutes.inWholeMilliseconds)
                            longBreakLabel -> pomodoroViewModel?.setLongBreakDuration(time.minutes.inWholeMilliseconds)
                            else -> pomodoroViewModel?.setTimeLeft(time.minutes.inWholeMilliseconds)
                        }
                    },
                    onDismissRequest = { showInputTimeDialog = Pair(0, false) }
                )
            }

            ConstraintLayout{

                val (
                    durationLabelRef,
                    alertsLabelRef,
                    timeInputsRef,
                    soundCheckBoxRef,
                    vibrationCheckBoxRef
                ) = createRefs()

                TextLabel(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .constrainAs(durationLabelRef) {
                            start.linkTo(parent.start)
                        },
                    text = stringResource(id = R.string.duration_label).uppercase(),
                    textStyle = MaterialTheme.typography.body1
                )
                Row(
                    modifier = Modifier
                        .padding(bottom = 28.dp)
                        .constrainAs(timeInputsRef) {
                            top.linkTo(durationLabelRef.bottom)
                            start.linkTo(parent.start)
                        },
                ) {
                    TimeInput(
                        value = pomodoroValue.toString().padStart(2, '0'),
                        label = stringResource(id = pomodoroLabel).uppercase(),
                        onClick = { showInputTimeDialog = Pair(pomodoroLabel, true) }
                    )
                    Spacer(Modifier.padding(end = 12.dp))
                    TimeInput(
                        value = shortBreakValue.toString().padStart(2, '0'),
                        label = stringResource(id = shortBreakLabel).uppercase(),
                        onClick = { showInputTimeDialog = Pair(shortBreakLabel, true) }
                    )
                    Spacer(Modifier.padding(end = 12.dp))
                    TimeInput(
                        value = longBreakValue.toString().padStart(2, '0'),
                        label = stringResource(id = longBreakLabel).uppercase(),
                        onClick = { showInputTimeDialog = Pair(longBreakLabel, true) }
                    )
                }
                TextLabel(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .constrainAs(alertsLabelRef) {
                            top.linkTo(timeInputsRef.bottom)
                            start.linkTo(parent.start)
                        },
                    text = stringResource(id = R.string.alerts_label).uppercase(),
                    textStyle = MaterialTheme.typography.body1
                )
                SettingsCheckBox(
                    modifier = Modifier
                        .constrainAs(soundCheckBoxRef){
                            top.linkTo(alertsLabelRef.bottom)
                            start.linkTo(parent.start)
                        },
                    label = stringResource(id = R.string.sound_label),
                    checked = enableSound,
                    onCheckChanged = { enableSound = it}
                )
                SettingsCheckBox(
                    modifier = Modifier
                        .constrainAs(vibrationCheckBoxRef){
                            top.linkTo(alertsLabelRef.bottom)
                            start.linkTo(soundCheckBoxRef.end, margin = 8.dp)
                        },
                    label = stringResource(id = R.string.vibration_label),
                    checked = enableVibration,
                    onCheckChanged = { enableVibration = it }
                )
            }
        }
    }
}

@DevicesPreviews
@Composable
fun SettingsScreenPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            SettingsScreen(
                onArrowBackPressed = {},
                pomodoroViewModel = null
            )
        }
    }
}