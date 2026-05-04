package com.max.bookrecommendations.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.max.bookrecommendations.data.model.Post

class PostRemoteDataSource {

    private val db = FirebaseFirestore.getInstance()

    fun createPost(
        post: Post,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("posts")
            .document(post.id)
            .set(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}