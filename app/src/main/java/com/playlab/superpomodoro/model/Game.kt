package com.playlab.superpomodoro.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: Int,
    val name: String,
    val url: String,
    val thumbnail: Int
) : Parcelable