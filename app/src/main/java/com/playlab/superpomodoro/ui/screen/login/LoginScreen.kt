package com.playlab.superpomodoro.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.components.FormInput
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel? = hiltViewModel(),
    onSignUpLabelClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.padding(12.dp))
        Row(Modifier.fillMaxWidth()){
            TextLabel(
                text = stringResource(id = R.string.login_screen_title),
                textStyle = MaterialTheme.typography.subtitle1,
                fontSize = 24.sp,
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))
        FormInput(
            text = "",
            onTextChange = {},
            leadingIcon =  Icons.Default.Email,
            placeholder = stringResource(id = R.string.input_email),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        FormInput(
            text = "",
            onTextChange = {},
            leadingIcon =  Icons.Default.Key,
            placeholder = stringResource(id = R.string.input_password),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            onClick = {}) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = stringResource(id = R.string.button_login),
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            onClick = onSignUpLabelClick
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.no_account_label) +
                    stringResource(id = R.string.sign_up_here_label),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@DevicesPreviews
@Composable
fun PreviewLoginScreen() {
    SuperPomodoroTheme(false) {
        Surface {
            Column (
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LoginScreen(
                    modifier = Modifier.fillMaxSize(0.8f),
                    chatViewModel = null
                )
            }
        }
    }
}