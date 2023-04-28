package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Purple400
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun TimeSelectorDialog(
    modifier: Modifier = Modifier,
    label: String,
    value: Int = 1,
    onValueChange: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ConstraintLayout(
            modifier = modifier
                .size(width = 230.dp, height = 170.dp)
        ) {
            val (
                increaseButton,
                decreaseButton,
                inputLabel,
                inputValue
            ) = createRefs()

            TextLabel(
                modifier = Modifier
                    .constrainAs(inputLabel){
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    },
                text = label,
                textColor = Color.White,
                textStyle = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                )
            )

            TextLabel(
                modifier = Modifier.constrainAs(inputValue){
                    start.linkTo(decreaseButton.end)
                    end.linkTo(increaseButton.start)
                    top.linkTo(decreaseButton.top)
                    bottom.linkTo(decreaseButton.bottom)
                },
                textColor = Color.White,
                text = value.toString(),
                fontSize = dimensionResource(id = R.dimen.time_selector_font_size).value.sp
            )

            IconButton(
                modifier = Modifier
                    .background(Purple400.copy(alpha = 0.6f), CircleShape)
                    .constrainAs(decreaseButton) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                onClick = {  if(value > 1) onValueChange(value - 1)  }) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    "",
                    tint = Color.White,
                )
            }
            IconButton(
                modifier = Modifier
                    .background(Purple400.copy(alpha = 0.6f), CircleShape)
                    .constrainAs(increaseButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                    },
                onClick = { onValueChange(value + 1) }) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    "",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ActionDialog(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    onDismissRequest: () -> Unit,
    onOkClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    AlertDialog(
        modifier = modifier.clip(MaterialTheme.shapes.large),
        title = {
            Text(title)
        },
        text = {
            Text(text)
        },
        onDismissRequest = onDismissRequest,
        buttons = {
            Row (
                modifier = Modifier
                    .padding(
                        start = 22.dp, end = 22.dp, bottom = 8.dp
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,

                ){
                TextButton(
                    onClick = onCancelClick) {
                    Text(
                        text =stringResource(
                            id = R.string.action_dialog_button_cancel
                        ).uppercase()
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = onOkClick) {
                    Text(
                        text = stringResource(
                            id = R.string.action_dialog_button_ok
                        ).uppercase()
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewActionDialog() {
    SuperPomodoroTheme(false) {
        Surface {
            ActionDialog(
                title = "Delete",
                text = "Are you sure?",
                onDismissRequest = {},
                onCancelClick = {},
                onOkClick = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewTimeSelectorDialog() {
    SuperPomodoroTheme(false) {
        Surface {

            var value by remember { mutableStateOf(10) }

            TimeSelectorDialog(
                label = "POMODORO",
                value = value,
                onValueChange = { value = it },
                onDismissRequest = { }
            )
        }
    }
}