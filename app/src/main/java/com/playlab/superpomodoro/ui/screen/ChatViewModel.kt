package com.playlab.superpomodoro.ui.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlab.superpomodoro.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
    @Inject constructor(
        private val firebaseRepository: FirebaseRepository
    )
    : ViewModel() {

    private var _isLoggedIn = mutableStateOf<Boolean?>(null)
    val isLoggedIn = _isLoggedIn

    fun login(email: String, password: String)  {
        viewModelScope.launch {
           firebaseRepository.login(email, password).collect{
               _isLoggedIn.value = it
           }
        }
    }

    fun logout(){
        firebaseRepository.logout()
    }

}