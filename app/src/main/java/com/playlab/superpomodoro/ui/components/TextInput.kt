package com.playlab.superpomodoro.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Blue100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    value: String = "00",
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .size(dimensionResource(id = R.dimen.input_time_size))
            .clip(RoundedCornerShape(10.dp))
            .background(Blue100)
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = value,
            fontSize = dimensionResource(id = R.dimen.input_time_font_size).value.sp,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.alpha(0.8f),
            text = label,
            fontSize =  dimensionResource(id = R.dimen.input_time_label_font_size).value.sp
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTimeInput() {
    SuperPomodoroTheme(darkTheme = false) {
        Surface{
            TimeInput(
                label = "POMODORO",
                onClick = {}
            )
        }
    }
}