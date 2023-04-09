package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun SvgIcon(
    svgResourceId: Int,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val painter: Painter = painterResource(id = svgResourceId)

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(size),
    )
}