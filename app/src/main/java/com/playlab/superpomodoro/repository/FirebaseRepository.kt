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
                }.addOnFailureListener{ exception ->
                    val errorMessage = "Error on login"
                    firebaseCrashlytics.run {
                        log(errorMessage)
                        recordException(exception)
                    }
                    cancel(errorMessage, cause = exception)
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
                }.addOnFailureListener{ exception ->
                    val errorMessage = "Error saving user"
                    firebaseCrashlytics.run {
                        log(errorMessage)
                        setCustomKey("user", userCopy.toString())
                        recordException(exception)
                    }
                    cancel(errorMessage, cause = exception)
                }
            }.addOnFailureListener{ exception ->
                val errorMessage = "Error creating user account"
                firebaseCrashlytics.run {
                    log(errorMessage)
                    recordException(exception)
                }
                cancel(errorMessage, cause = exception)
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
                            val errorMessage = "Error fetching user"
                            firebaseCrashlytics.run {
                                log(errorMessage)
                                recordException(exception)
                            }
                            cancel(
                                message = errorMessage,
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
                    }.addOnFailureListener{ exception ->

                        val errorMessage = "Group creation error"
                        firebaseCrashlytics.run {
                            log(errorMessage)
                            setCustomKey("group", group.toString())
                            recordException(exception)
                        }
                        cancel(errorMessage, cause = exception)
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
                        }.addOnFailureListener{ exception ->
                            val errorMessage = "Error on add member to group"
                            firebaseCrashlytics.run {
                                log(errorMessage)
                                setCustomKey("groupId", groupId)
                                setCustomKey("memberEmail", email)
                                recordException(exception)
                            }
                            cancel(errorMessage, cause = exception)
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
                        val errorMessage = "User not found by the given email"
                        firebaseCrashlytics.run {
                            log(errorMessage)
                            setCustomKey("userEmail", email)
                            recordException(UserNotFoundException())
                        }
                        cancel(errorMessage, cause = UserNotFoundException())
                    }
                }.addOnFailureListener { exception ->
                    val errorMessage = "Error on fetching user by email"
                    firebaseCrashlytics.run {
                        log(errorMessage)
                        setCustomKey("userEmail", email)
                        recordException(exception)
                    }
                    cancel(errorMessage, cause = exception)
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
            val errorMessage = "Error on fetching user by id"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("userid", userId)
                recordException(e)
            }
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
                        val errorMessage = "Error the member is already in the group"
                        firebaseCrashlytics.run {
                            log(errorMessage)
                            setCustomKey("userid", userId)
                            setCustomKey("groupId", groupId)
                        }
                        cancel(
                            errorMessage,
                            cause = UserAlreadyInTheGroupException()
                        )
                    }
                }.addOnFailureListener { exception ->
                    val errorMessage = "Error on verifying group member"
                    firebaseCrashlytics.run {
                        log(errorMessage)
                        setCustomKey("userid", userId)
                        setCustomKey("groupId", groupId)
                        recordException(exception)
                    }
                    cancel(errorMessage, cause = exception)
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
            val errorMessage = "Error fetching group members"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("groupId", groupId)
                recordException(e)
            }
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
                        val errorMessage = "Error fetching user groups"
                        firebaseCrashlytics.run {
                            log(errorMessage)
                            currentUser?.userId?.let { setCustomKey("userId", it) }
                            recordException(exception)
                        }
                        cancel(errorMessage, exception)
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
            val errorMessage = "Error fetching group by id"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("groupId", groupId)
                recordException(e)
            }
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
            val errorMessage = "Error sending message to group"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("message", message.toString())
                recordException(e)
            }
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
                .addSnapshotListener { querySnapshot, exception ->
                    if(exception != null) {
                        val errorMessage = "Error on fetching messages"
                        firebaseCrashlytics.run {
                            log(errorMessage)
                            setCustomKey("groupId", groupId)
                            recordException(exception)
                        }
                        cancel(errorMessage, exception)
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
            val errorMessage = "Error getting group last message"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("groupId", groupId)
                recordException(e)
            }
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
            val errorMessage = "Error removing member from group"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("memberId", userId)
                setCustomKey("groupId", groupId)
                recordException(e)
            }
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
            val errorMessage = "Error deleting group"
            firebaseCrashlytics.run {
                log(errorMessage)
                group.groupId?.let { setCustomKey("groupId", it) }
                recordException(e)
            }
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
            val errorMessage = "Error removing member all group messages"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("groupId", groupId)
                recordException(e)
            }
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
            val errorMessage = "Error removing all member from the group"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("groupId", groupId)
                recordException(e)
            }
        }
    }

    private suspend fun uploadGroupThumbnail(thumbnailUri: Uri): Uri {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = firebaseStorage.getReference("$GROUP_IMAGES/$filename")
            val taskSnapShot = ref.putFile(thumbnailUri).await()
            return taskSnapShot.task.snapshot.storage.downloadUrl.await()
        }catch (e: Exception){
            val errorMessage = "Error uploading group thumbnail"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("thumbnailUri", thumbnailUri.toString())
                recordException(e)
            }
            Uri.EMPTY
        }
    }

    private suspend fun deleteGroupThumbnail(thumbnailUrl: String) {
        try {
            val ref = firebaseStorage.getReferenceFromUrl(thumbnailUrl)
            ref.delete().await()
        }catch (e: Exception){
            val errorMessage = "Error deleting group thumbnail $e"
            firebaseCrashlytics.run {
                log(errorMessage)
                setCustomKey("thumbnailUri", thumbnailUrl)
                recordException(e)
            }
        }
    }
}