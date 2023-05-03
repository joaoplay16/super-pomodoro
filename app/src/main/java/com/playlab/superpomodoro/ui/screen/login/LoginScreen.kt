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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.components.FormInput
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.ui.validators.InputValidator.emailIsValid

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel?,
    onSignUpLabelClick: () -> Unit = {}
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginErrorMessage = when (chatViewModel?.loginUpError?.value) {
        is FirebaseAuthInvalidCredentialsException ->
            stringResource(id = R.string.invalid_credentials_error)
        is FirebaseAuthInvalidUserException ->
            stringResource(id = R.string.invalid_user_error)
        else -> null
    }

    var isEmailValid by remember { mutableStateOf (true) }
    var isPasswordValid by remember { mutableStateOf (true) }

    val validateInputs: () -> Unit = {
        isEmailValid = emailIsValid(email)
        isPasswordValid = password.isNotBlank()
    }

    val areAllInputsValid =
        isEmailValid && isPasswordValid


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
        //E-MAIL
        FormInput(
            text = email,
            onTextChange = { email = it },
            isError = isEmailValid.not(),
            errorMessage = stringResource(id = R.string.invalid_email_error),
            leadingIcon =  Icons.Default.Email,
            placeholder = stringResource(id = R.string.input_email),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        )
        Spacer(modifier = Modifier.padding(8.dp))
        // PASSWORD
        FormInput(
            text = password,
            onTextChange = { password = it },
            isError = isPasswordValid.not(),
            errorMessage = stringResource(id = R.string.invalid_password_error),
            leadingIcon =  Icons.Default.Key,
            placeholder = stringResource(id = R.string.input_password),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        // LOGIN BUTTON
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            onClick = {
                validateInputs()
                if(areAllInputsValid) chatViewModel?.login(email, password)
            }) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = stringResource(id = R.string.button_login),
                fontSize = 18.sp
            )
        }
        loginErrorMessage?.let {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                modifier = Modifier.padding(4.dp),
                text = it,
                fontSize = 18.sp,
                color = Color.Red
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))
        // SIGN IN CALL TO ACTION
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            onClick = onSignUpLabelClick
        ) {
            Row {
                Text(
                    text =
                    stringResource(id = R.string.no_account_label)
                            + " " +
                    stringResource(id = R.string.sign_up_here_label),
                    fontSize = 16.sp
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