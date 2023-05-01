package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Gray100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun CreateGroupDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    groupName: String,
    groupThumbnail: String,
    onThumbnailClick: () -> Unit,
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
                .padding(start = 32.dp, end= 32.dp, top = 20.dp, bottom = 16.dp)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val p = rememberAsyncImagePainter(
                        model = groupThumbnail,
                        placeholder = painterResource(id = R.drawable.crowd),
                        error = painterResource(id = R.drawable.crowd)
                    )

                    Image(
                        modifier = Modifier
                            .clickable { onThumbnailClick() }
                            .size(100.dp)
                            .clip(CircleShape),
                        painter = p,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
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
                                if (isGroupNameValid) onButtonCreateClick()
                            }
                            .background(Gray100, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                        ,
                        text = stringResource(id = R.string.create_group_dialog_button_create),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
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
                groupThumbnail = "",
                onGroupNameChange = {},
                onThumbnailClick = {},
                onDismissRequest = {},
                onButtonCreateClick = {}
            )
        }
    }
}