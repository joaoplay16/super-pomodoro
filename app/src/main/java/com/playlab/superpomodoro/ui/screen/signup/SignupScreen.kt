package com.playlab.superpomodoro.ui.screen.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.ui.components.FormInput
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.ui.validators.InputValidator
import com.playlab.superpomodoro.util.Constants.MIN_PASSWORD_LENGTH
import com.playlab.superpomodoro.util.Constants.MIN_USERNAME_LENGTH

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel? = hiltViewModel(),
    onArrowBackPressed: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                onArrowBackPressed()
                            },
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrow_back_icon_content_description)
                    )
                    TextLabel(
                        text = stringResource(id = R.string.signup_screen_title),
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = dimensionResource(id = R.dimen.screen_title_font_size).value.sp
                    )
                }
            }
        }
    ) { paddingValues ->

        val context = LocalContext.current

        var email by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var repeatPassword by remember { mutableStateOf("") }

        val signupErrorMessage = when (chatViewModel?.signUpError?.value) {
            is FirebaseAuthUserCollisionException ->
                stringResource(id = R.string.email_already_in_use_error)

            is FirebaseFirestoreException ->
                stringResource(id = R.string.account_creation_error)

            else -> null
        }

        var isEmailValid by remember { mutableStateOf (true) }
        var isUserNameLengthValid by remember { mutableStateOf (true) }
        var isPasswordLengthValid by remember { mutableStateOf (true) }
        var isRepeatPasswordLengthValid by remember { mutableStateOf (true) }
        var repeatPasswordMatch by remember { mutableStateOf (true) }

        val validateInputs: () -> Unit = {
            isEmailValid = InputValidator.emailIsValid(email)
            isUserNameLengthValid = username.length >= MIN_USERNAME_LENGTH
            repeatPasswordMatch = password == repeatPassword
            isPasswordLengthValid = password.length >= MIN_PASSWORD_LENGTH
            isRepeatPasswordLengthValid = repeatPassword.length >= MIN_PASSWORD_LENGTH
        }

        val areAllInputsValid = (
            isEmailValid &&
            isUserNameLengthValid &&
            isPasswordLengthValid &&
            isRepeatPasswordLengthValid &&
            repeatPasswordMatch
        )

        Column(
            modifier = modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.padding(12.dp))
            Row(Modifier.fillMaxWidth()) {
                TextLabel(
                    text = stringResource(id = R.string.register_screen_title),
                    textStyle = MaterialTheme.typography.subtitle1,
                    fontSize = 24.sp,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.padding(12.dp))
            // EMAIL
            FormInput(
                text = email,
                onTextChange = { email = it },
                isError = isEmailValid.not(),
                errorMessage = stringResource(id = R.string.invalid_email_error),
                leadingIcon = Icons.Default.Email,
                placeholder = stringResource(id = R.string.input_email),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                )
            )
            Spacer(modifier = Modifier.padding(8.dp))
            // USERNAME
            FormInput(
                text = username,
                isError = isUserNameLengthValid.not(),
                errorMessage = context.getString(
                    R.string.invalid_username_length_error, MIN_USERNAME_LENGTH
                ),
                onTextChange = { username = it },
                leadingIcon = Icons.Default.Person,
                placeholder = stringResource(id = R.string.input_username),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                )
            )
            Spacer(modifier = Modifier.padding(8.dp))
            //PASSWORD
            FormInput(
                text = password.replace(".", ""),
                isError = isPasswordLengthValid.not(),
                errorMessage = context.getString(
                    R.string.invalid_password_length_error, MIN_PASSWORD_LENGTH
                ),
                onTextChange = { password = it },
                leadingIcon = Icons.Default.Key,
                placeholder = stringResource(id = R.string.input_password),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            // REPEAT PASSWORD
            FormInput(
                text = repeatPassword,
                isError = isRepeatPasswordLengthValid.not() || repeatPasswordMatch.not(),
                errorMessage =
                if(isRepeatPasswordLengthValid.not()) {
                    context.getString(
                        R.string.invalid_password_length_error, MIN_PASSWORD_LENGTH
                    )
                } else if(repeatPasswordMatch.not()){
                    context.getString(
                        R.string.passwords_did_not_match_error
                    )
                } else null,
                onTextChange = { repeatPassword = it },
                leadingIcon = Icons.Default.Key,
                placeholder = stringResource(id = R.string.input_repeat_password),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            // REGISTER BUTTON
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                onClick = {
                    validateInputs()
                    if(areAllInputsValid){
                        chatViewModel?.createUser(
                            User(
                                username = username,
                                email = email
                            ), password
                        )
                        if(chatViewModel?.currentUser?.value != null) {
                            onSignUpSuccess()
                        }
                    }
                }
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(id = R.string.button_register),
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            signupErrorMessage?.let {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = signupErrorMessage,
                    fontSize = 18.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@DevicesPreviews
@Composable
fun PreviewSignupScreen() {
    SuperPomodoroTheme(false) {
        Surface {
            Column (
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                SignupScreen(
                    modifier = Modifier.fillMaxSize(0.8f),
                    chatViewModel = null,
                    onArrowBackPressed = {},
                    onSignUpSuccess = {}
                )
            }
        }
    }
}