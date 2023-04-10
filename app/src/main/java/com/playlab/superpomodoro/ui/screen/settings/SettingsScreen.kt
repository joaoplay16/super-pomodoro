package com.playlab.superpomodoro.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.playlab.superpomodoro.ui.components.TimeSelectorDialog
import com.playlab.superpomodoro.ui.components.SettingsCheckBox
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.components.TimeInput
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onArrowBackPressed: ()  -> Unit,
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

            var showInputTimeDialog by remember {
                mutableStateOf(Pair("", false))
            }

            if(showInputTimeDialog.second){
                TimeSelectorDialog(
                    label = showInputTimeDialog.first,
                    value = 1,
                    onValueChange = { },
                    onDismissRequest = { showInputTimeDialog = Pair("", false) }
                )
            }

            ConstraintLayout{

                val pomodoroLabel = stringResource(id = R.string.pomodoro).uppercase()
                val breakLabel = stringResource(id = R.string.pomodoro_short_break).uppercase()
                val longBreakLabel = stringResource(id = R.string.pomodoro_Long_break).uppercase()

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
                        label = pomodoroLabel,
                        onClick = { showInputTimeDialog = Pair(pomodoroLabel, true) }
                    )
                    Spacer(Modifier.padding(end = 12.dp))
                    TimeInput(
                        label = breakLabel,
                        onClick = { showInputTimeDialog = Pair(breakLabel, true) }
                    )
                    Spacer(Modifier.padding(end = 12.dp))
                    TimeInput(label = longBreakLabel,
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
            SettingsScreen(onArrowBackPressed = {})
        }
    }
}