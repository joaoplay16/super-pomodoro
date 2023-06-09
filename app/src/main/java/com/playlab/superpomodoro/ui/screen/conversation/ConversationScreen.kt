package com.playlab.superpomodoro.ui.screen.conversation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.model.Message
import com.playlab.superpomodoro.ui.components.ActionDialog
import com.playlab.superpomodoro.ui.components.MessageInput
import com.playlab.superpomodoro.ui.components.MessageItem
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.Banana100
import com.playlab.superpomodoro.ui.theme.Olive100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.TimeUtil.toFormattedTimeString
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel?,
    onArrowBackPressed: ()  -> Unit,
    group: Group,
    onGroupNameClick: () -> Unit,
    onLeaveGroup: () -> Unit,
) {
    var showActionDialog by remember{ mutableStateOf(false) }
    var isOptionMenuOpen by remember { mutableStateOf(false) }
    val currentUserId = chatViewModel?.currentUser?.value?.userId
    val isAdmin = currentUserId == group.adminId
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
                    verticalAlignment = Alignment.CenterVertically
                ){
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
                        modifier = Modifier.clickable { onGroupNameClick() }.basicMarquee(),
                        text = group.name,
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = dimensionResource(id = R.dimen.screen_title_font_size).value.sp
                    )
                }
                if(isAdmin.not()) {
                    Box(
                        modifier = Modifier
                            .clickable(onClick = { isOptionMenuOpen = true })
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp),
                            painter = painterResource(id = R.drawable.dots_vertical),
                            contentDescription = stringResource(id = R.string.menu_icon_cd)
                        )

                        DropdownMenu(
                            expanded = isOptionMenuOpen,
                            onDismissRequest = { isOptionMenuOpen = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    showActionDialog = true
                                }
                            ) {
                                Text(
                                    text = stringResource(id = R.string.leave_the_group_menu_option),
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }
            }
        }
    ){ paddingValues ->


        var messageText by remember {mutableStateOf("")}

        val coroutineScope = rememberCoroutineScope()

        val messages = chatViewModel?.groupMessages?.toSortedMap(compareBy{ it.timestamp })

        val listState = rememberLazyListState()

        val firstVisibleItemIndex by  remember { derivedStateOf { listState.firstVisibleItemIndex }}

        var mostRecentMessageIndex by remember{ mutableStateOf(0) }

        LaunchedEffect(key1 = firstVisibleItemIndex, block = {
            mostRecentMessageIndex = max(mostRecentMessageIndex, firstVisibleItemIndex)
        })

        LaunchedEffect(key1 = Unit, block = {
            chatViewModel?.getGroupMessages(group.groupId!!)
        })

        LaunchedEffect(key1 = messages, block = {
            if(mostRecentMessageIndex == firstVisibleItemIndex)
                messages?.size?.let { listState.scrollToItem(it) }
        })


        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState
            ) {
                messages?.map {
                    val (message, user) = it
                    val isThisUser = user.userId == currentUserId

                    item(key = message.messageId) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                            if (isThisUser)
                                Arrangement.End
                            else Arrangement.Start
                        ) {
                            MessageItem(
                                text = message.text,
                                senderName = if(isThisUser) null else user.username ,
                                date = message.timestamp.toFormattedTimeString(),
                                backgroundColor = if(isThisUser) Banana100 else Olive100
                            )
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                MessageInput(
                    text = messageText,
                    onTextChange = {
                        messageText = it
                    },
                    onSendClick = {
                        if(messageText.isBlank()) return@MessageInput
                        coroutineScope.launch {
                            currentUserId?.let{
                                val sent = chatViewModel.sendMessageToGroup(
                                    Message(
                                        messageId = "",
                                        groupId = group.groupId!!,
                                        senderId = currentUserId,
                                        text = messageText,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )

                                if (sent) messageText = ""
                            }
                        }
                    }
                )
            }
        }
        if(showActionDialog){
            isOptionMenuOpen = false
            ActionDialog(
                title = group.name,
                text = stringResource(id = R.string.leave_the_group_dialog_text),
                onDismissRequest = { showActionDialog = false },
                onOkClick = {
                    if (currentUserId != null) {
                        chatViewModel.removeUserFromGroup(currentUserId, group.groupId!!)
                    }
                    onLeaveGroup()
                },
                onCancelClick = { showActionDialog = false }
            )
        }
    }
}

@DevicesPreviews
@Composable
fun SettingsScreenPreview() {
    SuperPomodoroTheme(false) {
        Surface {
            ConversationScreen(
                onArrowBackPressed = {},
                chatViewModel = null,
                group = Group("", "", "StudyGroup", ""),
                onGroupNameClick = {},
                onLeaveGroup = {}
            )
        }
    }
}