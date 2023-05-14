package com.playlab.superpomodoro.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.playlab.superpomodoro.exception.UserAlreadyInTheGroupException
import com.playlab.superpomodoro.exception.UserNotFoundException
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.model.Group.Companion.toGroup
import com.playlab.superpomodoro.model.GroupMember
import com.playlab.superpomodoro.model.GroupMember.Companion.toGroupMember
import com.playlab.superpomodoro.model.Message
import com.playlab.superpomodoro.model.Message.Companion.toMessage
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.model.User.Companion.toUser
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseRepository
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

    companion object {
        private const val TAG = "FIREBASE"

        private const val USERS_COLLECTION = "users"
        private const val GROUPS_COLLECTION = "groups"
        private const val GROUP_MEMBERS_COLLECTION = "group_members"
        private const val MESSAGES_COLLECTION = "messages"
        private const val GROUP_IMAGES = "/images/groups"
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
            val currentUser = getCurrentUserFromFirestore().first()
            currentUser?.let {
                firebaseFirestore.collection(GROUPS_COLLECTION)
                    .add(group)
                    .addOnSuccessListener {documentRef ->
                        trySend(true)
                        launch {
                            val groupCopy = group.copy(
                                groupId = documentRef.id,
                                adminId = currentUser.userId!!,
                                thumbnailUrl = group.thumbnailUrl?.let {
                                    uploadGroupThumbnail(it.toUri())
                                }?.toString()
                            )
                            documentRef.set(groupCopy)
                            addMemberToGroup(currentUser.email, groupCopy.groupId!!).first()
                        }
                    }.addOnFailureListener{
                        cancel("Group creation error", cause = it)
                    }
            }
            awaitClose()
        }
    }

    fun addMemberToGroup(email: String, groupId: String): Flow<Boolean?>{
        return callbackFlow {
            val user = getUserByEmail(email).first()
            user?.let {
                val memberAlreadyExists = verifyMemberInGroup(user.userId!!, groupId).first()
                if (memberAlreadyExists == false) {
                    val groupMember = GroupMember(
                        null,
                        groupId = groupId,
                        userId = user.userId,
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

    suspend fun getUserById(userId: String) : User? {
        return try {
            firebaseFirestore.collection(USERS_COLLECTION)
                .document(userId)
                .get().await().toUser()
        } catch (e: Exception){
            Log.e(TAG, "Error fetching user by id")
            null
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

    suspend fun getGroupMembers(groupId: String) : List<User> {
        return try {
            firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                .whereEqualTo("groupId", groupId)
                .get()
                .await()
                .documents
                .map {
                    it.toGroupMember()
                }.map {
                    getUserById(it!!.userId)!!
                }
        }catch (e: Exception){
            Log.e(TAG,"Error fetching group members: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get the groups that the current user is currently a member of,
     * including the last message sent to the group
     **/
    fun getUserGroupsWithLastMessage() : Flow<Map<Group, Message?>> {
        return callbackFlow {
            val currentUser = getCurrentUserFromFirestore().first()
            firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                .whereEqualTo("userId", currentUser?.userId)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null){
                        cancel("Error fetching user groups", exception)
                    }else{
                        launch {
                            val groupMessages = querySnapshot?.documents
                                ?.mapNotNull {
                                    it.toGroupMember()
                                }?.mapNotNull {
                                    async { getGroupById(it.groupId) }.await()
                                }?.associateWith {
                                    async {getGroupLastMessage(it.groupId!!)}.await()
                                }.orEmpty()
                            trySend(groupMessages)
                        }
                    }
                }

            awaitClose()
        }
    }

    suspend fun getGroupById(groupId: String) : Group? {
        return try {
            firebaseFirestore.collection(GROUPS_COLLECTION)
                .document(groupId)
                .get().await().toGroup()
        } catch (e: Exception){
            Log.e(TAG, "Error fetching group by id")
            null
        }
    }

    suspend fun sendMessageToGroup(
        message: Message
    ): Boolean {
        return try {
            val documentRef = firebaseFirestore.collection(MESSAGES_COLLECTION)
                .add(message)
                .await()
            documentRef.set(message.copy(messageId = documentRef.id))
            true
        }catch (e: Exception) {
            Log.e(TAG, "Error sending message to group")
            false
        }
    }

    fun getGroupMessages(
        groupId: String
    ): Flow<Map<Message, User>> {
        return callbackFlow {
            firebaseFirestore.collection(MESSAGES_COLLECTION)
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(500)
                .addSnapshotListener { querySnapshot, error ->
                    if(error != null) {
                        cancel("Error on fetching messages", error)
                    }
                    launch {
                        val messages = querySnapshot?.map {
                            it.toMessage()!!
                        }?.associateWith {
                            async {  getUserById(it.senderId)}.await()!!
                        }
                        trySend(messages ?: emptyMap())
                    }
                }
            awaitClose()
        }
    }

    private suspend fun getGroupLastMessage(
        groupId: String
    ): Message? {
        return try {
            firebaseFirestore.collection(MESSAGES_COLLECTION)
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await().documents[0].toMessage()
        }catch (e: Exception) {
            Log.e(TAG, "Error getting group last message ${e.cause}")
            null
        }
    }

    suspend fun removeUserFromGroup(userId: String, groupId: String) {
        try {
            val groupMember = firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("groupId", groupId)
                .limit(1)
                .get()
                .await().documents[0].toGroupMember()

            val docRef = firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                .document(groupMember?.id!!)

            docRef.delete().await()
        }catch (e: Exception) {
            Log.e(TAG, "Error removing member from group ${e}")
        }
    }

    suspend fun deleteGroup(group: Group) = coroutineScope{
        try {
            val groupId = group.groupId!!

            val removeMessagesJob = launch {
                removeAllGroupMessages(groupId)
            }
            val removeMembersJob = launch {
                removeAllGroupMembers(groupId)
            }
            val deleteThumbnailJob = launch {
                deleteGroupThumbnail(group.thumbnailUrl!!)
            }

            joinAll(
                removeMessagesJob,
                removeMembersJob,
                deleteThumbnailJob
            )

            firebaseFirestore.collection(GROUPS_COLLECTION)
                .document( groupId )
                .delete().await()
        }catch (e: Exception) {
            Log.e(TAG, "Error deleting group ")
        }
    }

    private suspend fun removeAllGroupMessages(groupId: String) {
        try {
            firebaseFirestore.collection(MESSAGES_COLLECTION)
                .whereEqualTo("groupId", groupId)
                .get()
                .await().documents.forEach{
                    val message = it.toMessage()
                    Log.d(TAG, "deleting message ${message?.messageId} ")

                    val docRef = firebaseFirestore.collection(MESSAGES_COLLECTION)
                        .document(message?.messageId!!)
                    docRef.delete().await()
                }
        }catch (e: Exception) {
            Log.e(TAG, "Error removing member all group messages ${e}")
        }
    }

    private suspend fun removeAllGroupMembers(groupId: String) = coroutineScope {
        try {
            firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                .whereEqualTo("groupId", groupId)
                .get()
                .await().documents.forEach{
                    val groupMember = it.toGroupMember()
                    val docRef = firebaseFirestore.collection(GROUP_MEMBERS_COLLECTION)
                        .document(groupMember?.id!!)

                    docRef.delete().await()
                }
        }catch (e: Exception) {
            Log.e(TAG, "Error removing all member from the group ${e}")
        }
    }

    private suspend fun uploadGroupThumbnail(thumbnailUri: Uri): Uri {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = firebaseStorage.getReference("$GROUP_IMAGES/$filename")
            val taskSnapShot = ref.putFile(thumbnailUri).await()
            return taskSnapShot.task.snapshot.storage.downloadUrl.await()
        }catch (e: Exception){
            Log.e(TAG, "Error uploading group thumbnail ${e}")
            Uri.EMPTY
        }
    }

    private suspend fun deleteGroupThumbnail(thumbnailUrl: String) {
        try {
            val ref = firebaseStorage.getReferenceFromUrl(thumbnailUrl)
            ref.delete().await()
        }catch (e: Exception){
            Log.e(TAG, "Error deleting group thumbnail $e")
        }
    }
}