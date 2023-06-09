package com.playlab.superpomodoro.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playlab.superpomodoro.ui.theme.Gray100
import com.playlab.superpomodoro.ui.theme.Red600
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun FormInput(
    modifier: Modifier = Modifier,
    text: String,
    description: String = "",
    placeholder: String = "",
    leadingIcon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next
    ),
    onTextChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    onImeAction: () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(
        modifier = Modifier.animateContentSize(),
    ) {

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = description
                },
            value = text,
            shape = RoundedCornerShape(10.dp),
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alpha = ContentAlpha.medium),
                    text = placeholder,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            },
            textStyle = MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.onSurface
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(alpha = ContentAlpha.medium),
                    enabled = false,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions (
                onDone = { onImeAction() }
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.primaryVariant,
                unfocusedIndicatorColor = MaterialTheme.colors.primary,
                backgroundColor = Gray100,
                cursorColor = MaterialTheme.colors.onSurface
            ),
            isError = isError,
            visualTransformation = visualTransformation
        )
        if(isError && errorMessage != null){
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = Red600
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormInput(){
    SuperPomodoroTheme {
        Surface {
            FormInput(
                text = "",
                placeholder = "E-mail",
                leadingIcon = Icons.Default.Email,
                onTextChange = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormInputError(){
    SuperPomodoroTheme {
        Surface {
            FormInput(
                text = "",
                placeholder = "E-mail",
                leadingIcon = Icons.Default.Email,
                onTextChange = {},
                isError = true,
                errorMessage = "Unknown error"
            )
        }
    }
}