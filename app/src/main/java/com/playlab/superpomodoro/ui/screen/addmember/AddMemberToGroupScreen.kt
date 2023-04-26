package com.playlab.superpomodoro.ui.screen.addmember

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.exeception.UserAlreadyInTheGroupException
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.ui.components.FormInput
import com.playlab.superpomodoro.ui.components.GroupMemberItem
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.ui.validators.InputValidator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Composable
fun AddMemberToGroupScreen(
    modifier: Modifier = Modifier,
    groupId: String,
    groupName: String,
    onArrowBackPressed: ()  -> Unit,
    chatViewModel: ChatViewModel? = hiltViewModel()
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
                        text = groupName,
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = dimensionResource(id = R.dimen.screen_title_font_size).value.sp
                    )
                }
            }
        }
    ) { paddingValues ->

        val context = LocalContext.current

        val coroutineScope = rememberCoroutineScope()

        var userEmail by remember {
            mutableStateOf("")
        }
        var isEmailValid by remember {
            mutableStateOf(true)
        }

        var isMemberAdded by remember {
            mutableStateOf(Pair("", false))
        }

        var members by remember { mutableStateOf<List<User>>(emptyList()) }

        LaunchedEffect(key1 = isMemberAdded, block = {
            chatViewModel
                ?.getGroupMembers(groupId)
                ?.let { members = it }
            userEmail = ""
        })

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.padding(12.dp))
            // TITLE
            Row(Modifier.fillMaxWidth()) {
                TextLabel(
                    text = stringResource(id = R.string.add_members_title),
                    textStyle = MaterialTheme.typography.subtitle1,
                    fontSize = 24.sp,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.padding(12.dp))
            // EMAIL
            FormInput(
                Modifier.fillMaxWidth(),
                text = userEmail,
                isError = isEmailValid.not(),
                errorMessage =
                if(isEmailValid.not())
                    stringResource(id = R.string.invalid_email_error)
                else null,
                onTextChange = { userEmail = it },
                leadingIcon = Icons.Default.Email,
                placeholder = stringResource(id = R.string.input_email),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Email
                ),

                onImeAction = {
                    isEmailValid = InputValidator.emailIsValid(userEmail)
                    if(isEmailValid.not()) return@FormInput
                    coroutineScope.launch {
                        chatViewModel?.addMemberToGroup(userEmail, groupId)
                            ?.catch{ exception ->
                                when(exception.cause){
                                    is UserAlreadyInTheGroupException -> {
                                        Toast.makeText(
                                            context,
                                            R.string.user_already_in_the_group_error,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            context,
                                            R.string.user_not_found_error,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }?.collect { added ->
                                isMemberAdded = Pair(userEmail, added == true)
                            }
                    }
                }
            )

            if (members.isEmpty().not()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    items(members, key = { it.userId!! }) { member ->
                        GroupMemberItem(
                            profilePictureUrl = member.profileUrl,
                            name = member.email
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}

@DevicesPreviews
@Composable
fun AddGroupMemberScreenPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            AddMemberToGroupScreen(
                groupName = "Study group",
                onArrowBackPressed = {},
                chatViewModel = null,
                groupId = "1"
            )
        }
    }
}