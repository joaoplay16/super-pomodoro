package com.playlab.superpomodoro.exception

import java.lang.Exception

class UserNotFoundException : Exception() {
    override val message: String = "User not found"
}