package com.playlab.superpomodoro.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Blue200
import kotlin.math.roundToInt


@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun ExpandableFAB(
    onLogout: () -> Unit,
    onAddNewGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFabClicked = remember { mutableStateOf(false) }
    val animationSpec: AnimationSpec<Float> = spring(
        stiffness = Spring.StiffnessLow,
        dampingRatio = Spring.DampingRatioLowBouncy
    )
    val density = LocalDensity.current.density
    val offsetDeleteY = animateFloatAsState(
        targetValue = if (isFabClicked.value) -250f * density / 2.75f else 0f,
        animationSpec = animationSpec
    )
    val offsetAddNewY = animateFloatAsState(
        targetValue = if (isFabClicked.value) -500f * density / 2.75f else 0f,
        animationSpec = animationSpec
    )
    val rotationAngle = animateFloatAsState(
        targetValue = if (isFabClicked.value) 180f else 0f,
        animationSpec = animationSpec
    )

    val plusFabBackgroundColor = animateColorAsState(
        animationSpec = tween(durationMillis = 200),
        targetValue = if (isFabClicked.value) White else Blue200
    )

    val plusFabIconColor = animateColorAsState(
        animationSpec = tween(durationMillis = 200),
        targetValue = if (isFabClicked.value) Blue200 else White
    )
    AnimatedVisibility(
        enter = fadeIn(),
        exit = fadeOut(),
        visible = isFabClicked.value
    ) {
        Box(modifier = Modifier
            .pointerInteropFilter {
                isFabClicked.value = !isFabClicked.value
                false
            }
            .alpha(0.8f)
            .background(Color.Black)
            .fillMaxSize())
    }

    Box(modifier = modifier) {

        FABItem(
            offset = IntOffset(0, offsetAddNewY.value.roundToInt()),
            onClick = {
                isFabClicked.value = !isFabClicked.value
                onLogout()
            },
            size = 33.dp,
            resId = R.drawable.exit_to_app,
            backgroundColor = White,
            colorFilter = ColorFilter.tint(Blue200)
        )

        FABItem(
            offset = IntOffset(0, offsetDeleteY.value.roundToInt()),
            onClick = {
                isFabClicked.value = !isFabClicked.value
                onAddNewGroup()
            },
            size = 33.dp,
            resId = R.drawable.account_multiple_plus,
            backgroundColor = White,
            colorFilter = ColorFilter.tint(Blue200)
        )

        FABItem(
            modifier = Modifier.rotate(rotationAngle.value),
            onClick = {
                isFabClicked.value = !isFabClicked.value
            },
            size = 25.dp,
            resId = R.drawable.menu_up_outline,
            backgroundColor = plusFabBackgroundColor.value,
            colorFilter = ColorFilter.tint(plusFabIconColor.value)
        )
    }
}