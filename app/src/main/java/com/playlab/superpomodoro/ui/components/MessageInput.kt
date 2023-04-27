package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Pearl100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val context = LocalContext.current

    Row (
        modifier =  modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ){
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = context.getString(R.string.message_input_cd)
                },
            value = text,
            shape = RoundedCornerShape(32.dp),
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(alpha = ContentAlpha.medium),
                    text = stringResource(id = R.string.message_input_placeholder),
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface,
                )
            },
            textStyle = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp
            ),
            maxLines = 8,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.secondary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary.copy(0.6f),
                backgroundColor = Pearl100,
                cursorColor = MaterialTheme.colors.onSurface
            ),
        )
        Spacer(Modifier.padding(2.dp))
        IconButton(
            modifier = Modifier.size(56.dp)
                .background(MaterialTheme.colors.secondary, CircleShape),
            onClick = onSendClick
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = MaterialTheme.colors.surface
            )
        }
    }
}

@Preview
@Composable
fun PreviewMessageInput() {
    SuperPomodoroTheme {
        MessageInput(
            text = "",
            onTextChange = {},
            onSendClick = {}
        )
    }
}
@Preview
@Composable
fun PreviewMessageInputMultiline() {
    SuperPomodoroTheme {
        MessageInput(
            text ="Lorem ipsum dolor sit amet. Sit accusamus corporis " +
                    "nam illo excepturi est molestias dolore est sint unde" +
                    "nam illo excepturi est molestias dolore est sint unde" +
                    "nam illo excepturi est molestias dolore est sint unde" +
                    "nam illo excepturi est molestias dolore est sint unde" +
                    "nam illo excepturi est molestias dolore est sint unde"
            ,
            onTextChange = {},
            onSendClick = {}
        )
    }
}

