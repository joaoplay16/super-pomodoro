package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    thumbnailUrl: String?,
    name: String,
    lastMessage: String?,
    lastMessageDate: String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        val context = LocalContext.current

        val imageRequest = remember {
            ImageRequest.Builder(context)
                .data(thumbnailUrl)
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
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = modifier.weight(2f),
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp
                )
                lastMessageDate?.let{
                    Text(
                        modifier = modifier.weight(1f),
                        text = it,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onSurface.copy(0.6f),
                        fontSize = 12.sp
                    )
                }
            }
           lastMessage?.let {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = it,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onSurface.copy(0.6f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupItem() {
    SuperPomodoroTheme {
        Surface {
            Column(Modifier.padding(16.dp)) {
                GroupItem(
                    name = "Study Group",
                    thumbnailUrl =
                    "https://www.fakepersongenerator.com" +
                            "/Face/female/female102157398572.jpg",
                    lastMessage = "last message",
                    lastMessageDate = "07/04/2023"
                )
                Spacer(modifier = Modifier.padding(8.dp))
                //No image
                GroupItem(
                    name = "Study Group",
                    thumbnailUrl = null,
                    lastMessage =null,
                    lastMessageDate = null
                )
            }
        }
    }
}