package com.playlab.superpomodoro.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val groupId: String,
    val adminId: String,
    val name: String,
    val thumbnailUrl: String?
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toGroup(): Group? {
            return try {
                val name = getString("name")!!
                val thumbnailUrl = getString("thumbnailUrl")
                val adminId = getString("adminId")!!
                Group(id, adminId, name, thumbnailUrl)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting group", e)
                FirebaseCrashlytics.getInstance().log("Error converting group")
                FirebaseCrashlytics.getInstance().setCustomKey("groupId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
        private const val TAG = "Chat"
    }
}