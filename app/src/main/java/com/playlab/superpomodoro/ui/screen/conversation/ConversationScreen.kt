package com.playlab.superpomodoro.ui.screen.conversation

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.model.Message
import com.playlab.superpomodoro.ui.components.MessageInput
import com.playlab.superpomodoro.ui.components.MessageItem
import com.playlab.superpomodoro.ui.components.TextLabel
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.Banana100
import com.playlab.superpomodoro.ui.theme.Olive100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import kotlinx.coroutines.launch

@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel? = hiltViewModel(),
    onArrowBackPressed: ()  -> Unit,
    groupId: String,
    groupName: String,
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
                        text = groupName,
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = dimensionResource(id = R.dimen.screen_title_font_size).value.sp
                    )
                }
            }
        }
    ){ paddingValues ->

        var messageText by remember {mutableStateOf("")}

        val currentUserId = chatViewModel?.currentUser?.value?.userId

        val coroutineScope = rememberCoroutineScope()

        val messages = chatViewModel?.groupMessages?.toSortedMap(compareBy{ it.timestamp })

        LaunchedEffect(key1 = Unit, block = {
            chatViewModel?.getGroupMessages(groupId)
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                messages?.map {
                    val (message, user) = it
                    val isThisUser = user.userId == currentUserId

                    item {
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
                                date = "${message.timestamp}",
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
                        coroutineScope.launch {
                            currentUserId?.let{
                                val sent = chatViewModel.sendMessageToGroup(
                                    Message(
                                        messageId = "",
                                        groupId = groupId,
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
                groupId = "",
                groupName = "Study group"
            )
        }
    }
}