package com.playlab.superpomodoro.ui.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun  RemainingTimeAnimation(
    canvasSize: Dp = 300.dp,
    color: Color = MaterialTheme.colors.primary,
    value: Int = 23,
    maxValue: Int = 60
) {
    var percentage = 1f
    if(value <= maxValue){
        percentage = (value.toFloat() / maxValue.toFloat())
    }
    val sweepAngle by animateFloatAsState(
        targetValue = ( percentage * 360  ),
        animationSpec = tween(1000),
    )

    Column(modifier = Modifier
        .size(canvasSize)
        .drawBehind {
            val componentSize = size / 1.25f

            foregroundIndicator(
                componentSize = componentSize,
                sweepAngle = sweepAngle,
                color = color
            )

        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
}

fun DrawScope.foregroundIndicator(
    componentSize: Size,
    sweepAngle: Float,
    color: Color
){
    drawArc(
        size = componentSize,
        color = color,
        startAngle =270f,
        sweepAngle = sweepAngle,
        useCenter = true,

        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

@Composable
@Preview(showBackground = true)
fun PercentageCircleAnimationPreview() {
    SuperPomodoroTheme {
        Surface(color = MaterialTheme.colors.surface){
            RemainingTimeAnimation(
                canvasSize= 200.dp,
                value = 88,
                maxValue = 100
            )
        }
    }
}
