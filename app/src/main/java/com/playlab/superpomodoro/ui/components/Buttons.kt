package com.playlab.superpomodoro.ui.components

import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme


@Composable
fun StopButton(
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ){
        SvgIcon(
            svgResourceId = R.drawable.button_stop,
            size = dimensionResource(id = R.dimen.button_stop_size)
        )
    }
}

@Composable
fun ResumeButton(
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {


    IconButton(
        modifier = modifier,
        onClick = onClick
    ){
        SvgIcon(
            svgResourceId = R.drawable.button_resume,
            size = dimensionResource(id = R.dimen.button_resume_size)
        )
    }
}


@Composable
fun PauseButton(
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {

    IconButton(
        modifier = modifier,
        onClick = onClick
    ){
        SvgIcon(
            svgResourceId = R.drawable.button_pause,
            size = dimensionResource(id = R.dimen.button_pause_size)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PauseButtonPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            PauseButton(onClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResumeButtonPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            ResumeButton(onClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StopButtonPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            StopButton(onClick = {})
        }
    }
}