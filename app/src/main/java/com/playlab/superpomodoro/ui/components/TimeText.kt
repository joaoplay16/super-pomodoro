package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.ui.theme.Gray100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun TimeText(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Gray100)
    ){
        Text(
            modifier = Modifier.padding(horizontal = 38.dp, vertical = 8.dp),
            text = text,
            style = MaterialTheme.typography.body2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeTextPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            TimeText(text = "23:59")
        }
    }
}
