package com.playlab.superpomodoro.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.ui.theme.Blue200
import com.playlab.superpomodoro.ui.theme.Green100
import com.playlab.superpomodoro.ui.theme.Purple400
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme

enum class TabPage {
    Home, Chat, Games
}

@Composable
fun HomeTabBar(
    modifier: Modifier = Modifier,
    tabPage: TabPage,
    backgroundColor: Color = MaterialTheme.colors.background,
    onTabSelected: (tabPage: TabPage) -> Unit,
    disabledTabs: List<TabPage> = emptyList()
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = tabPage.ordinal,
        backgroundColor = backgroundColor,
        indicator = { tabPositions ->
            HomeTabIndicator(tabPositions, tabPage)
        }
    ) {

        val isTabDisabled: (TabPage) -> Boolean = remember{{
            it in disabledTabs
        }}

        val disabledModifier: (TabPage) -> Modifier = remember {{ tab ->
            if(isTabDisabled(tab))
                Modifier.alpha(0.5f)else Modifier
        }}

        HomeTab(
            modifier = disabledModifier(TabPage.Home),
            icon = Icons.Rounded.Home,
            title = stringResource(R.string.home_tab),
            onClick = { if(isTabDisabled(TabPage.Home).not()) onTabSelected(TabPage.Home) }
        )
        HomeTab(
            modifier = disabledModifier(TabPage.Chat),
            icon = Icons.Rounded.Chat,
            title = stringResource(R.string.chat_tab),
            onClick = { if(isTabDisabled(TabPage.Chat).not()) onTabSelected(TabPage.Chat) }
        )
        HomeTab(
            modifier = disabledModifier(TabPage.Games),
            icon = Icons.Default.Games,
            title = stringResource(R.string.games_tab),
            onClick = { if(isTabDisabled(TabPage.Games).not()) onTabSelected(TabPage.Games) }
        )
    }
}

@Composable
private fun HomeTabIndicator(
    tabPositions: List<TabPosition>,
    tabPage: TabPage
) {
    val transition = updateTransition(
        tabPage,
        label = "Tab indicator"
    )

    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Indicator left"
    ){ page ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessMedium)
        },
        label = "Indicator right"
    ) { page ->
        tabPositions[page.ordinal].right
    }
    val color by transition.animateColor(
        label = "Border color"
    ) { page ->
        when(page){
            TabPage.Home -> Blue200
            TabPage.Chat -> Green100
            else -> Purple400
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(2.dp, color),
                RoundedCornerShape(4.dp)
            )
    )
}

@Composable
private fun HomeTab(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTabPreview() {
    SuperPomodoroTheme(false) {
        Surface {

            var selectedTab by remember{ mutableStateOf(TabPage.Home) }

            HomeTabBar(
                tabPage = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTabPreview_TabDisabled() {
    SuperPomodoroTheme(false) {
        Surface {

            var selectedTab by remember{ mutableStateOf(TabPage.Home) }

            HomeTabBar(
                tabPage = selectedTab,
                onTabSelected = { selectedTab = it },
                disabledTabs = listOf(TabPage.Chat)
            )
        }
    }
}