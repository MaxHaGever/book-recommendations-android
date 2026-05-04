package com.max.bookrecommendations.ui.createeditpost

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.PostRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource
import java.util.UUID

class CreateEditPostFragment : Fragment(R.layout.fragment_create_edit_post) {

    private lateinit var titleEditText: TextInputEditText
    private lateinit var reviewEditText: TextInputEditText
    private lateinit var postImagePreview: ImageView
    private lateinit var savePostButton: MaterialButton
    private val storageRemoteDataSource = StorageRemoteDataSource()
    private lateinit var authorEditText: TextInputEditText
    private var selectedImageUri: Uri? = null
    private val authRemoteDataSource = AuthRemoteDataSource()
    private val postRemoteDataSource = PostRemoteDataSource()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                postImagePreview.setImageURI(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleEditText = view.findViewById(R.id.titleEditText)
        reviewEditText = view.findViewById(R.id.reviewEditText)
        postImagePreview = view.findViewById(R.id.postImagePreview)
        savePostButton = view.findViewById(R.id.savePostButton)
        authorEditText = view.findViewById(R.id.authorEditText)

        val changeImageButton: MaterialButton = view.findViewById(R.id.changePostImageButton)

        changeImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        savePostButton.setOnClickListener {
            savePost()
        }
    }

    private fun savePost() {
        val title = titleEditText.text.toString().trim()
        val review = reviewEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || review.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = authRemoteDataSource.getCurrentUser() ?: return

        val postId = java.util.UUID.randomUUID().toString()

        savePostButton.isEnabled = false
        savePostButton.text = "Posting..."

        if (selectedImageUri != null) {

            storageRemoteDataSource.uploadPostImage(
                postId,
                selectedImageUri!!,
                onSuccess = { imageUrl ->

                    val post = Post(
                        id = postId,
                        ownerUid = currentUser.uid,
                        ownerName = currentUser.displayName ?: "User",
                        ownerProfileImageUrl = currentUser.photoUrl?.toString(),
                        bookTitle = title,
                        bookAuthor = author,
                        description = review,
                        customImageUrl = imageUrl,
                        createdAt = System.currentTimeMillis()
                    )

                    saveToFirestore(post)
                },
                onFailure = {
                    handleError(it)
                }
            )

        } else {

            val post = Post(
                id = postId,
                ownerUid = currentUser.uid,
                ownerName = currentUser.displayName ?: "User",
                ownerProfileImageUrl = currentUser.photoUrl?.toString(),
                bookTitle = title,
                bookAuthor = author,
                description = review,
                customImageUrl = null,
                createdAt = System.currentTimeMillis()
            )

            saveToFirestore(post)
        }
    }

    private fun saveToFirestore(post: Post) {
        postRemoteDataSource.createPost(
            post,
            onSuccess = {
                Toast.makeText(requireContext(), "Post created", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onFailure = {
                handleError(it)
            }
        )
    }

    private fun handleError(exception: Exception) {
        savePostButton.isEnabled = true
        savePostButton.text = "Post"

        Toast.makeText(
            requireContext(),
            exception.message ?: "Failed",
            Toast.LENGTH_LONG
        ).show()
    }
}