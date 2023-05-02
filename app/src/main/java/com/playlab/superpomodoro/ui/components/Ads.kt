package com.playlab.superpomodoro.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.playlab.superpomodoro.ui.theme.SuperPomodoroTheme
import com.playlab.superpomodoro.util.Constants

@Composable
fun AdvertView(
    modifier: Modifier = Modifier,
    adSize: AdSize
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = Constants.AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdvertPreview() {
    SuperPomodoroTheme (false){
        Surface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AdvertView(adSize = AdSize.FULL_BANNER)
                AdvertView(adSize = AdSize.LARGE_BANNER)
                AdvertView(adSize = AdSize.BANNER)
                AdvertView(adSize = AdSize.MEDIUM_RECTANGLE)
            }
        }
    }
}