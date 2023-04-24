package com.playlab.superpomodoro.exeception

import java.lang.Exception

class GroupNotFoundException : Exception() {
    override val message: String = "Group not found"
}