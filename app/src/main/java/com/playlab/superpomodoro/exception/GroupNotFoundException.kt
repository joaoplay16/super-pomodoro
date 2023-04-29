package com.playlab.superpomodoro.exception

import java.lang.Exception

class GroupNotFoundException : Exception() {
    override val message: String = "Group not found"
}