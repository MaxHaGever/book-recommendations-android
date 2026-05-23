package com.max.bookrecommendations.data.repository

import android.net.Uri
import com.google.firebase.auth.UserProfileChangeRequest
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource

class ProfileRepository(
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource(),
    private val storageRemoteDataSource: StorageRemoteDataSource = StorageRemoteDataSource()
) {

    fun getCurrentUser() = authRemoteDataSource.getCurrentUser()

    fun updateProfile(
        name: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = authRemoteDataSource.getCurrentUser()

        if (currentUser == null) {
            onFailure(Exception("User not found"))
            return
        }

        if (imageUri != null) {
            storageRemoteDataSource.uploadProfileImage(
                uid = currentUser.uid,
                imageUri = imageUri,
                onSuccess = { imageUrl ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(Uri.parse(imageUrl))
                        .build()

                    currentUser.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                },
                onFailure = { exception ->
                    onFailure(exception)
                }
            )
        } else {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            currentUser.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }
}