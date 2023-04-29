package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.playlab.superpomodoro.ui.theme.Banana100
import com.playlab.superpomodoro.ui.theme.Olive100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.TimeUtil.toFormattedTimeString

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    senderName: String?,
    text: String,
    date: String,
    backgroundColor: Color = Olive100
) {

    ConstraintLayout(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .sizeIn(maxWidth = 280.dp, minWidth = 20.dp)
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        val (senderNameRef, textRef, dateRef) = createRefs()
       senderName?.let{
            Text(
                modifier = Modifier.constrainAs(senderNameRef){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                text = senderName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            modifier = Modifier.constrainAs(textRef){
                top.linkTo(senderNameRef.bottom)
                start.linkTo(parent.start)
            },
            text = text,
            fontSize = 16.sp
        )

        Text(
            modifier = Modifier.constrainAs(dateRef){
                top.linkTo(textRef.bottom)
                end.linkTo(parent.end)
            },
            text = date,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageItem() {
    SuperPomodoroTheme(false) {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                MessageItem(
                    senderName = "Jimmy",
                    text = "Hi",
                    date = System.currentTimeMillis().toFormattedTimeString(),
                    )
                Spacer(modifier = Modifier.padding(12.dp))
                MessageItem(
                    senderName = "Mike",
                    text = "Lorem ipsum dolor sit amet. Sit accusamus corporis " +
                            "nam illo excepturi est molestias dolore est sint unde",
                    date = System.currentTimeMillis().toFormattedTimeString(),
                    backgroundColor = Banana100
                )
                Spacer(modifier = Modifier.padding(12.dp))
                MessageItem(
                    senderName = null,
                    text = "Lorem ipsum dolor sit amet. Sit accusamus corporis " +
                            "nam illo excepturi est molestias dolore est sint unde",
                    date = System.currentTimeMillis().toFormattedTimeString(),
                    backgroundColor = Banana100
                )
            }
        }
    }
}