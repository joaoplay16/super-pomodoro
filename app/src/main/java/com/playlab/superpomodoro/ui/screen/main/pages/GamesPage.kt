package com.playlab.superpomodoro.ui.screen.main.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdSize
import com.playlab.superpomodoro.model.Game
import com.playlab.superpomodoro.ui.components.AdvertView
import com.playlab.superpomodoro.ui.components.GameItem
import com.playlab.superpomodoro.ui.screen.DevicesPreviews
import com.playlab.superpomodoro.ui.screen.gameview.GameList
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GamesPage(
    modifier: Modifier = Modifier,
    onGameSelected: (Game) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        itemsIndexed(items = GameList()){ index, game ->
            GameItem(
                modifier = Modifier.clickable { onGameSelected(game) },
                name = game.name,
                thumbResId = game.thumbnail,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            if (index == 0){
                AdvertView(adSize = AdSize.MEDIUM_RECTANGLE)
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@DevicesPreviews
@Composable
fun GamePagePreview() {
    SuperPomodoroTheme(false) {
        Surface {
            GamesPage()
        }
    }
}