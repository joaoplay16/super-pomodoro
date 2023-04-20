package com.playlab.superpomodoro.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun login(email: String, password: String) : Flow<Boolean?> {
        return callbackFlow<Boolean> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    trySend(task.isSuccessful).isSuccess
            }

            awaitClose()
        }
    }

    fun logout(){
        firebaseAuth.signOut()
    }
}