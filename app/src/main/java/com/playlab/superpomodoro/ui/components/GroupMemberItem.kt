package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun GroupMemberItem(
    modifier: Modifier = Modifier,
    profilePictureUrl: String?,
    name: String,
    isAdmin: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        val context = LocalContext.current

        val imageRequest = remember {
            ImageRequest.Builder(context)
                .data(profilePictureUrl)
                .error(R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .crossfade(300)
                .build()
        }

        AsyncImage(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape),
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        Spacer(Modifier.padding(8.dp))
        Text(
            modifier = modifier.weight(1f),
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp
        )
        if (isAdmin) {
            Text(
                text = stringResource(id = R.string.group_member_item_admin_label),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.primary,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupMemberItem() {
    SuperPomodoroTheme(false) {
        Surface {
            Column {
                GroupMemberItem(
                    name = "McLovin",
                    profilePictureUrl =
                    "https://www.fakepersongenerator.com" +
                            "/Face/female/female102157398572.jpg"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupMemberItem_NoImage() {
    SuperPomodoroTheme(false) {
        Surface {
            Column {
                GroupMemberItem(
                    name = "McLovin",
                    profilePictureUrl = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupMemberItem_Admin() {
    SuperPomodoroTheme(false) {
        Surface {
            Column {
                GroupMemberItem(
                    name = "McLovin",
                    profilePictureUrl = null,
                    isAdmin = true
                )
            }
        }
    }
}