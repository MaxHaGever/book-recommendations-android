package com.max.bookrecommendations.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.max.bookrecommendations.data.repository.AuthRepository

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _returningUser = MutableLiveData<Boolean>()
    val returningUser: LiveData<Boolean> = _returningUser

    fun checkReturningUser() {
        _returningUser.value = authRepository.isUserLoggedIn()
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        authRepository.login(
            email = email,
            password = password,
            onSuccess = {
                _isLoading.value = false
                _loginSuccess.value = true
            },
            onFailure = { exception ->
                _isLoading.value = false
                _errorMessage.value = exception.message ?: "Login failed"
            }
        )
    }
}