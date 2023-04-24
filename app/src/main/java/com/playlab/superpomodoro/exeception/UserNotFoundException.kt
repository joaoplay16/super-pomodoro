package com.playlab.superpomodoro.exeception

import java.lang.Exception

class UserNotFoundException : Exception() {
    override val message: String = "User not found"
}