package com.playlab.superpomodoro.ui.screen.groupoverview

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.exception.UserAlreadyInTheGroupException
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.ui.components.ActionDialog
import com.playlab.superpomodoro.ui.components.FormInput
import com.playlab.superpomodoro.ui.components.GroupMemberItem
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.Gray400
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.ui.validators.InputValidator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupOverviewScreen(
    modifier: Modifier = Modifier,
    group: Group,
    onArrowBackPressed: ()  -> Unit,
    onDeleteGroup: ()  -> Unit,
    chatViewModel: ChatViewModel?
) {
    val context = LocalContext.current
    var isOptionMenuOpen by remember { mutableStateOf(false) }
    var showDeleteGroupDialog by remember{ mutableStateOf(false) }
    val currentUserId = chatViewModel?.currentUser?.value?.userId
    val isCurrentUserAdmin = currentUserId == group.adminId

    Scaffold(
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.surface),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                onArrowBackPressed()
                            },
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrow_back_icon_content_description)
                    )
                    if (isCurrentUserAdmin) {
                        Box(
                            modifier = Modifier
                                .clickable(onClick = { isOptionMenuOpen = true })
                        ) {
                            Icon(
                                modifier = Modifier,
                                painter = painterResource(id = R.drawable.dots_vertical),
                                contentDescription = stringResource(id = R.string.menu_icon_cd)
                            )

                            DropdownMenu(
                                expanded = isOptionMenuOpen,
                                onDismissRequest = { isOptionMenuOpen = false }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        showDeleteGroupDialog = true
                                        isOptionMenuOpen = false
                                    }
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.delete_the_group_menu_option),
                                        fontSize = 18.sp,
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val imageRequest = remember {
                        ImageRequest.Builder(context)
                            .data(group.thumbnailUrl)
                            .error(R.drawable.crowd)
                            .placeholder(R.drawable.crowd)
                            .crossfade(300)
                            .build()
                    }
                    AsyncImage(
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CircleShape),
                        model = imageRequest,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.padding(8.dp))
                    TextLabel(
                        text = group.name,
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = dimensionResource(id = R.dimen.screen_title_font_size).value.sp
                    )
                }
            }
        }
    ) { paddingValues ->

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

        var showRemoveMemberDialog by remember{ mutableStateOf(false) }

        var selectedUserToRemove by remember { mutableStateOf<User?>(null)}

        LaunchedEffect(key1 = isMemberAdded, block = {
            chatViewModel
                ?.getGroupMembers(group.groupId!!)
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
            if(isCurrentUserAdmin) {
                Column {
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
                        if (isEmailValid.not())
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
                            if (isEmailValid.not()) return@FormInput
                            coroutineScope.launch {
                                chatViewModel?.addMemberToGroup(userEmail, group.groupId!!)
                                    ?.catch { exception ->
                                        when (exception.cause) {
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
                }
            }

            if (members.isEmpty().not()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    stickyHeader {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 12.dp)
                                .height(1.dp)
                                .background(Gray400.copy(0.5f))
                        )
                    }
                    items(members, key = { it.userId!! }) { member ->
                        GroupMemberItem(
                            modifier = Modifier.
                            then(
                                Modifier.combinedClickable(
                                    onLongClick = {
                                        if(isCurrentUserAdmin){
                                            selectedUserToRemove = member
                                            showRemoveMemberDialog = true
                                        }
                                    },
                                    onClick ={}
                                )
                            ),
                            profilePictureUrl = member.profileUrl,
                            name = member.username
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }

        if(showRemoveMemberDialog){
            selectedUserToRemove?.let {
                ActionDialog(
                    title = it.username,
                    text = stringResource(id = R.string.remove_the_member_dialog_text),
                    onDismissRequest = { showRemoveMemberDialog = false },
                    onOkClick = {

                    },
                    onCancelClick = { showRemoveMemberDialog = false }
                )
            }
        }

        if(showDeleteGroupDialog){
            ActionDialog(
                title = group.name,
                text = stringResource(id = R.string.delete_the_group_dialog_text),
                onDismissRequest = { showDeleteGroupDialog = false },
                onOkClick = {
                    chatViewModel?.deleteGroup(group = group)
                    showDeleteGroupDialog = false
                    onDeleteGroup()
                },
                onCancelClick = { showDeleteGroupDialog = false }
            )
        }
    }
}

@DevicesPreviews
@Composable
fun AddGroupMemberScreenPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            val group = Group(
                "0",
                "0",
                "Study group",
                thumbnailUrl = null
            )
            GroupOverviewScreen(
                group = group,
                onArrowBackPressed = {},
                onDeleteGroup = {},
                chatViewModel = null,
            )
        }
    }
}