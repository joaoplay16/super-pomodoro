package com.playlab.superpomodoro.ui.components

import android.util.TypedValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Blue100
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

@Composable
fun SettingsCheckBox(
    modifier: Modifier = Modifier,
    label: String,
    scale: Float = 0f,
    checked: Boolean = false,
    onCheckChanged: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val typedValue = TypedValue()
    context.resources.getValue(R.dimen.check_box_scale, typedValue, true)

    val dimenScale = typedValue.float

    val checkBoxScale = if (scale > 0) scale else dimenScale

    Row(
        modifier = modifier
            .background(Blue100, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        TextLabel(text = label)
        Checkbox(
            modifier = Modifier.scale(checkBoxScale),
            checked = checked,
            onCheckedChange = onCheckChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsCheckBox() {

    var checked by remember { mutableStateOf(false) }

    SuperPomodoroTheme {
        Surface {
            SettingsCheckBox(
                label = "Sound",
                checked = checked,
                onCheckChanged = {
                    checked = it
                }
            )
        }
    }
}