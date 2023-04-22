package com.playlab.superpomodoro.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupMember(
    val id: String,
    val groupId: String,
    val userId: String
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toGroupMember(): GroupMember? {
            return try {
                val userId = getString("userId")!!
                val groupId = getString("groupId")!!
                GroupMember(id, userId, groupId)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting group", e)
                FirebaseCrashlytics.getInstance().log("Error converting group member")
                FirebaseCrashlytics.getInstance().setCustomKey("id", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
        private const val TAG = "Chat"
    }
}