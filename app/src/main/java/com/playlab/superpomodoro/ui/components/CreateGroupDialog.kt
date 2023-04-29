package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Gray100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun CreateGroupDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    onButtonCreateClick: () -> Unit
) {

    var isGroupNameValid by remember {
        mutableStateOf(true)
    }

    val validateGroupName: () -> Unit = {
        isGroupNameValid = groupName.isNotBlank()
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            Modifier
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colors.surface) {
            Column (modifier = modifier
                .padding(horizontal = 32.dp, vertical = 20.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    TextLabel(
                        text = stringResource(R.string.create_group_dialog_title),
                        textStyle = MaterialTheme.typography.subtitle2,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                FormInput(
                    text = groupName,
                    isError = isGroupNameValid.not(),
                    errorMessage = stringResource(id = R.string.empty_group_name_error),
                    onTextChange = onGroupNameChange,
                    leadingIcon = Icons.Default.GroupAdd,
                    placeholder = stringResource(id = R.string.input_group_name_placeholder)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ){
                    Text(
                        modifier = Modifier
                            .clickable {
                                validateGroupName()
                                if(isGroupNameValid) onButtonCreateClick()
                            }
                            .background(Gray100, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                        ,
                        text = stringResource(id = R.string.create_group_dialog_button_create),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateGroupDialog() {
    SuperPomodoroTheme(false) {
        Surface {
            CreateGroupDialog(
                groupName = "",
                onGroupNameChange = {},
                onDismissRequest = {},
                onButtonCreateClick = {}
            )
        }
    }
}