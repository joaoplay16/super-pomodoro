package com.playlab.superpomodoro.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.ui.theme.*

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
                        .size(16.dp)
                        .clip(RoundedCornerShape(2000.dp))
                        .background(if(done >= i+1) Grape200 else Grape100)
                )
               if(i != 3) Spacer(modifier = Modifier.padding(horizontal = 12.dp))
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