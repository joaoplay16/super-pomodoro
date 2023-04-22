package com.playlab.superpomodoro.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String? = null,
    val username: String,
    val profileUrl: String? = null,
    val email: String
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toUser(): User? {
            return try {
                val username = getString("username")!!
                val profileUrl = getString("profileUrl")
                val email = getString("email")!!
                User(id, username, profileUrl, email)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting user profile", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
        private const val TAG = "Chat"
    }
}