package com.playlab.superpomodoro.ui.screen.gameview

import com.playlab.superpomodoro.R
import com.playlab.superpomodoro.model.Game

object GameList {
    private val games = listOf(
        Game(
            1,
            "Hextris",
            "https://hextris.io/",
            R.drawable.hextriz
        ),
        Game(
            2,
            "Peg Solitaire",
            "http://omerkel.github.io/Peg-Solitaire/html5/src/",
            R.drawable.peg_solitaire
        ),
        Game(
            3,
            "15 puzzle",
            "https://bazhanius.github.io/15-puzzle-game/",
            R.drawable.puzzle
        )
    )

    operator fun invoke() = games
}

