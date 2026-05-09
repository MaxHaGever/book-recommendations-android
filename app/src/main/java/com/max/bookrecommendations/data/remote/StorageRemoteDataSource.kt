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

    fun uploadPostImage(
        postId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val imageRef = storage.reference
            .child("post_images")
            .child(postId)
            .child("post.jpg")

        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}