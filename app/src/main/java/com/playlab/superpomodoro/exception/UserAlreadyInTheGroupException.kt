package com.playlab.superpomodoro.exception

import java.lang.Exception

class UserAlreadyInTheGroupException : Exception() {
    override val message: String = "The user is already in the group"
}