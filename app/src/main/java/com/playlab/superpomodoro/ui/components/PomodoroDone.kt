package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Grape100
import com.playlab.superpomodoro.ui.theme.Grape200
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun PomodoroDone(
    modifier: Modifier = Modifier,
    done: Int = 1
) {
    LazyRow(
        modifier = modifier,
    ){
       for (i in 0 until 4){
            item (key = i){
                Box(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.pomodoro_done_dot_size))
                        .clip(CircleShape)
                        .background(if(done >= i+1) Grape200 else Grape100)
                )
               if(i != 3) Spacer(
                   modifier = Modifier.padding(
                       horizontal = dimensionResource(id = R.dimen.pomodoro_done_dot_spacing)
                   )
               )
            }
       }
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroDonePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            Column(Modifier.fillMaxWidth()) {
                
            PomodoroDone(done = 1)
            }
        }
    }
}