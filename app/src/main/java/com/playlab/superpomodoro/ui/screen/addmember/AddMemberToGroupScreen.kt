package com.playlab.superpomodoro.ui.screen.addmember

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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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
fun AddMemberToGroupScreen(
    modifier: Modifier = Modifier,
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
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.padding(12.dp))
            // TITLE
            Row(Modifier.fillMaxWidth(0.85f)){
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
                Modifier.fillMaxWidth(0.85f),
                text = "",
                onTextChange = {},
                leadingIcon =  Icons.Default.Email,
                placeholder = stringResource(id = R.string.input_email),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                )
            )
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
                chatViewModel = null
            )
        }
    }
}