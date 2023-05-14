package com.playlab.superpomodoro

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import org.junit.Test
import org.junit.runner.RunWith

class FirebaseLoginTest {



    @Test
    fun testFirebaseLogin() {
        val email = "example@example.com"
        val password = "password"

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
               assertThat(task.isSuccessful).isTrue()
            }
    }
}