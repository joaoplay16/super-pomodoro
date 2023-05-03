package com.playlab.superpomodoro.ui.screen.groups

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.ads.AdSize
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.ui.components.AdvertView
import com.playlab.superpomodoro.ui.components.CreateGroupDialog
import com.playlab.superpomodoro.ui.components.ExpandableFAB
import com.playlab.superpomodoro.ui.components.GroupItem
import com.playlab.superpomodoro.ui.screen.ChatViewModel
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.Constants.MAX_GROUP_NAME_LENGTH
import com.playlab.superpomodoro.util.ImageCompressor.compressImage
import com.playlab.superpomodoro.util.TimeUtil.toFormattedTimeString
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel?,
    onGroupSelected: (Group) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
    ) {

        val (
            floatActionButtonRef,
            adsBannerRef
            ) = createRefs()

        val groups = chatViewModel?.getUserGroupsWithLastMessage()?.collectAsState(null)?.value

        val currentUser = chatViewModel?.currentUser?.value

        var showCreateGroupDialog by remember { mutableStateOf(false) }

        var newGroupName by remember { mutableStateOf("") }

        var newGroupThumbnail by remember { mutableStateOf<Uri?>(Uri.EMPTY) }

        val coroutineScope = rememberCoroutineScope()

        val context = LocalContext.current

        val launcherImage = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = {
                it?.let{
                    coroutineScope.launch {
                        newGroupThumbnail = compressImage(context, it)
                    }
                }
            }
        )

        if(showCreateGroupDialog){
            CreateGroupDialog(
                onDismissRequest = { showCreateGroupDialog = false },
                groupName = newGroupName,
                groupThumbnail = newGroupThumbnail.toString(),
                onThumbnailClick = {
                    launcherImage.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onGroupNameChange = { if(it.length < MAX_GROUP_NAME_LENGTH) newGroupName = it },
                onButtonCreateClick = {
                    coroutineScope.launch {
                        chatViewModel?.createGroup(
                            Group(null, "", newGroupName, newGroupThumbnail.toString())
                        )?.catch{
                            Toast.makeText(context, "Error on creating group ", Toast.LENGTH_SHORT).show()
                        }?.collect{ success ->
                            if(success == true){
                                newGroupName = ""
                                newGroupThumbnail = Uri.EMPTY
                                Toast.makeText(context, "Group created", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    showCreateGroupDialog = false
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            groups?.map {
                val group = it.key
                val lastMessage = it.value

                val lastMessageDate = if(lastMessage?.timestamp == null)
                    null
                else lastMessage.timestamp.toFormattedTimeString()

                val lastMessageText = lastMessage?.text ?:
                if(currentUser?.userId == group.adminId)
                    context.getString(R.string.group_creation_message)
                else context.getString(R.string.added_to_the_group_message)

                item(group.groupId){
                    Row(
                        modifier = Modifier.clickable { onGroupSelected(group) },
                    ){
                        GroupItem(
                            thumbnailUrl = group.thumbnailUrl,
                            name = group.name,
                            lastMessageDate = lastMessageDate,
                            lastMessage = lastMessageText
                        )
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        }
        ExpandableFAB(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 20.dp)
                .constrainAs(floatActionButtonRef) {
                    end.linkTo(parent.end)
                    bottom.linkTo(adsBannerRef.top)
                },
            onLogout = { chatViewModel?.logout() },
            onAddNewGroup = { showCreateGroupDialog = true }
        )
        AdvertView(
            modifier = Modifier.constrainAs(adsBannerRef){
                 start.linkTo(parent.start)
                 end.linkTo(parent.end)
                 bottom.linkTo(parent.bottom)
            },
            adSize = AdSize.BANNER
        )
    }
}

@DevicesPreviews
@Composable
fun PreviewGroupsScreen() {
    SuperPomodoroTheme(false) {
        Surface {
            GroupsScreen(
                chatViewModel = null,
                onGroupSelected = {}
            )
        }
    }
}