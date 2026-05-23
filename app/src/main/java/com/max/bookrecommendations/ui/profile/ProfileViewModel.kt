package com.max.bookrecommendations.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.max.bookrecommendations.data.repository.AuthRepository

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _profileState = MutableLiveData<ProfileUiState>()
    val profileState: LiveData<ProfileUiState> = _profileState

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    fun loadProfile() {
        val currentUser = authRepository.getCurrentUser()

        if (currentUser == null) {
            _profileState.value = ProfileUiState(isLoggedIn = false)
            return
        }

        _profileState.value = ProfileUiState(
            name = currentUser.displayName ?: "Book Lover",
            email = currentUser.email ?: "No email",
            photoUrl = currentUser.photoUrl?.toString(),
            isLoggedIn = true
        )
    }

    fun logout() {
        authRepository.logout()
        _logoutSuccess.value = true
    }
}