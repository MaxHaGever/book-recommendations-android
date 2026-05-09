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

    fun getPostById(
        postId: String,
        onSuccess: (Post) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("posts")
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                val post = document.toObject(Post::class.java)

                if (post != null) {
                    onSuccess(post)
                } else {
                    onFailure(Exception("Post not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun savePost(
        post: Post,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("posts")
            .document(post.id)
            .set(post)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}