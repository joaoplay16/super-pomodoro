package com.playlab.superpomodoro.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val messageId: String,
    val senderId: String,
    val groupId: String,
    val text: String,
    val timestamp: Long
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toMessage(): Message? {
            return try {
                val senderId = getString("senderId")!!
                val groupId = getString("groupId")!!
                val text = getString("text")!!
                val timestamp = getLong("timestamp")!!
                Message(id, senderId, groupId, text, timestamp)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting group", e)
                FirebaseCrashlytics.getInstance().log("Error converting group message")
                FirebaseCrashlytics.getInstance().setCustomKey("messageId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
        private const val TAG = "Chat"
    }
}