package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun GameItem(
    modifier: Modifier = Modifier,
    thumbResId: Int,
    name: String
) {
    Box(
        modifier = modifier
            .size(300.dp)
            .clip(RoundedCornerShape(10.dp)),
        Alignment.BottomCenter
    ) {
        val painter = painterResource(id = thumbResId )
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = stringResource(id = R.string.game_item_image_description),
            contentScale = ContentScale.Fit
        )
        Row(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(color = Color.Black.copy(alpha = 0.6f)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextLabel(
                text = name,
                textStyle = MaterialTheme.typography.subtitle2,
                textColor = MaterialTheme.colors.onPrimary,
                fontSize = 26.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePagePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            GameItem(
                thumbResId = R.drawable.hextriz,
                name = "Hextris"
            )
        }
    }
}