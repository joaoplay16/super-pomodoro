package com.playlab.superpomodoro.ui.validators

import android.util.Patterns

object InputValidator {
    fun emailIsValid(email: String) : Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}