package com.max.bookrecommendations.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class StorageRemoteDataSource {

    private val storage = FirebaseStorage.getInstance()

    fun uploadProfileImage(
        uid: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val imageRef = storage.reference.child("profile_images/$uid.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { uploadTask ->
                uploadTask.storage.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}