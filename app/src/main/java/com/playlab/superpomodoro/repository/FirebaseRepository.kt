package com.playlab.superpomodoro.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.model.User.Companion.toUser
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    fun login(email: String, password: String) : Flow<Boolean?> {
        return callbackFlow<Boolean> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    trySend(task.isSuccessful).isSuccess
                }.addOnFailureListener{
                    cancel("Login error", cause = it)
                }

            awaitClose()
        }
    }

    fun logout(){
        firebaseAuth.signOut()
    }

    fun createUser(user: User, password: String): Flow<User?> {
        return callbackFlow {
            firebaseAuth.createUserWithEmailAndPassword(
                user.email,
                password
            ).addOnSuccessListener {
                val userCopy = user.copy(userId = firebaseAuth.uid.toString())
                saveUserInFirestore(userCopy).addOnSuccessListener {
                    trySend(userCopy)
                }.addOnFailureListener{
                    cancel("Error saving user", cause = it)
                }
            }.addOnFailureListener{
                cancel("Error creating user account", cause = it)
            }
            awaitClose()
        }
    }

    private fun saveUserInFirestore(user: User) : Task<Void> {
        return firebaseFirestore.collection(USERS_COLLECTION)
            .document(user.userId!!)
            .set(user)
    }

     fun getCurrentUserFromFirestore() : Flow<User?> {
        return callbackFlow {
            val listener = firebaseAuth.uid?.let { uid ->
                firebaseFirestore.collection(USERS_COLLECTION)
                    .document(uid)
                    .addSnapshotListener { docSnapshot, exception ->
                        if (exception != null) {
                            cancel(
                                message = "Error fetching user",
                                cause = exception
                            )
                            return@addSnapshotListener
                        }
                        val user = docSnapshot?.toUser()
                        trySend(user)
                    }
            }
            awaitClose{
                listener?.remove()
            }
        }
    }
}