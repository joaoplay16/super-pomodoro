package com.playlab.superpomodoro.ui.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlab.superpomodoro.model.Group
import com.playlab.superpomodoro.model.User
import com.playlab.superpomodoro.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
    @Inject constructor(
        private val firebaseRepository: FirebaseRepository
    ) : ViewModel() {

    private var _currentUser = mutableStateOf<User?>(null)
    val currentUser = _currentUser

    private var _isLoggedIn = mutableStateOf( _currentUser.value != null)
    val isLoggedIn = _isLoggedIn


    private var _signUpError = mutableStateOf<Throwable?>(null)
    val signUpError = _signUpError

    private var _loginUpError = mutableStateOf<Throwable?>(null)
    val loginUpError = _loginUpError

    init {
        getCurrentUser()
    }

    fun login(email: String, password: String)  {
        viewModelScope.launch {
           firebaseRepository.login(email, password)
               .catch {
                   _loginUpError.value = it.cause
               }.collect{
               it?.let {
                   _isLoggedIn.value = it
               }
           }
        }
    }

    fun logout(){
        firebaseRepository.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun createUser(user: User, password: String) {
        viewModelScope.launch {
            firebaseRepository
                .createUser(user = user, password = password)
                .catch {
                    _signUpError.value = it.cause
                }
               .collect{
                   _currentUser.value = it
                }
        }
    }

    fun getCurrentUser()  {
        viewModelScope.launch {
            firebaseRepository.getCurrentUserFromFirestore()
                .collect{
                    _currentUser.value = it
                    _isLoggedIn.value = it != null
            }
        }
    }

    fun createGroup(group: Group): Flow<Boolean?> {
        return firebaseRepository.createGroup(group)
    }
}