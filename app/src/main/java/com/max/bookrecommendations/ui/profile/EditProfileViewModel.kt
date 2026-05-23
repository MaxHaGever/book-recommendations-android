package com.max.bookrecommendations.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.max.bookrecommendations.data.repository.ProfileRepository

class EditProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _isSaving = MutableLiveData<Boolean>()
    val isSaving: LiveData<Boolean> = _isSaving

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun getCurrentName(): String {
        return profileRepository.getCurrentUser()?.displayName ?: ""
    }

    fun updateProfile(name: String, imageUri: Uri?) {
        _isSaving.value = true
        _errorMessage.value = null

        profileRepository.updateProfile(
            name = name,
            imageUri = imageUri,
            onSuccess = {
                _isSaving.value = false
                _saveSuccess.value = true
            },
            onFailure = { exception ->
                _isSaving.value = false
                _errorMessage.value = exception.message ?: "Update failed"
            }
        )
    }
}