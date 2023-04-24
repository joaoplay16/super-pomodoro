package com.playlab.superpomodoro.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.playlab.superpomodoro.exeception.UserAlreadyInTheGroupException
import com.playlab.superpomodoro.exeception.UserNotFoundException
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.model.GroupMember
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.model.User.Companion.toUser
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FirebaseRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val GROUPS_COLLECTION = "groups"
        private const val GROUP_MEMBERS_COLLECTION = "group_members"
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

    fun createGroup(group: Group): Flow<Boolean?>{
        return callbackFlow {
            firebaseFirestore.collection(GROUPS_COLLECTION)
                .add(group)
                .addOnSuccessListener {documentRef ->
                    trySend(true)
                    documentRef.set(group.copy(groupId = documentRef.id))
                }.addOnFailureListener{
                    cancel("Group creation error", cause = it)
                }

            awaitClose()
        }
    }

    fun addMemberToGroup(email: String, groupId: String): Flow<Boolean?>{
        return callbackFlow {
            val user = getUserByEmail(email).first()
            user?.let {
                val memberAlreadyExists = verifyMemberInGroup(user.email, groupId).first()
                if (memberAlreadyExists == false) {
                    val groupMember = GroupMember(
                        null,
                        groupId = groupId,
                        userId = user.email,
                    )
                    firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                        .add(groupMember)
                        .addOnSuccessListener { documentRef ->
                            documentRef.set(groupMember.copy(id = documentRef.id))
                            trySend(true)
                        }.addOnFailureListener{
                            cancel("Error on add member to group", cause = it)
                        }
                }
            }

            awaitClose()
        }
    }

    private fun getUserByEmail(email: String): Flow<User?> {
        return callbackFlow {
            firebaseFirestore.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if(querySnapshot.isEmpty.not()){
                        val user = querySnapshot.documents[0].toUser()!!
                        trySend(user)
                    }else{
                        cancel("User not found by the given email", cause = UserNotFoundException())
                    }
                }.addOnFailureListener {
                    cancel("Error on fetching user by email", cause = it)
                }

            awaitClose()
        }
    }

    private fun verifyMemberInGroup(userId: String, groupId: String): Flow<Boolean?> {
        return callbackFlow {
            firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("groupId", groupId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        trySend(false)
                    } else {
                        cancel(
                            "Error the member is already in the group",
                            cause = UserAlreadyInTheGroupException()
                        )
                    }
                }.addOnFailureListener {
                    cancel("Error on verifying group member", cause = it)
                }
            awaitClose()
        }
    }
}