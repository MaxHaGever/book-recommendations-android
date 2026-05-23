package com.max.bookrecommendations.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.UserProfileChangeRequest
import com.max.bookrecommendations.data.repository.AuthRepository

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    fun signup(
        name: String,
        email: String,
        password: String
    ) {

        _isLoading.value = true
        _errorMessage.value = null

        authRepository.signup(
            email = email,
            password = password,
            onSuccess = { user ->

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {

                        _isLoading.value = false
                        _signupSuccess.value = true
                    }
                    ?.addOnFailureListener { exception ->

                        _isLoading.value = false
                        _errorMessage.value =
                            exception.message ?: "Profile update failed"
                    }
            },
            onFailure = { exception ->

                _isLoading.value = false
                _errorMessage.value =
                    exception.message ?: "Signup failed"
            }
        )
    }
}