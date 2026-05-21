package com.max.bookrecommendations.ui.postdetails

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.local.DatabaseProvider
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.PostRemoteDataSource
import com.max.bookrecommendations.data.repository.PostRepository
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class PostDetailsFragment : Fragment(R.layout.fragment_post_details) {

    private val args: PostDetailsFragmentArgs by navArgs()
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var ownerTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var ownerActionsLayout: LinearLayout
    private lateinit var editPostButton: MaterialButton
    private lateinit var deletePostButton: MaterialButton

    private val postRemoteDataSource = PostRemoteDataSource()
    private val authRemoteDataSource = AuthRemoteDataSource()
    private lateinit var postRepository: PostRepository

    private var currentPost: Post? = null
    private var postId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.postDetailsProgressBar)
        imageView = view.findViewById(R.id.postDetailsImageView)
        titleTextView = view.findViewById(R.id.postDetailsTitleTextView)
        authorTextView = view.findViewById(R.id.postDetailsAuthorTextView)
        ownerTextView = view.findViewById(R.id.postDetailsOwnerTextView)
        descriptionTextView = view.findViewById(R.id.postDetailsDescriptionTextView)
        ownerActionsLayout = view.findViewById(R.id.ownerActionsLayout)
        editPostButton = view.findViewById(R.id.editPostButton)
        deletePostButton = view.findViewById(R.id.deletePostButton)

        val database = DatabaseProvider.getDatabase(requireContext())
        postRepository = PostRepository(database.postDao())

        postId = args.postId

        editPostButton.setOnClickListener {
            openEditPost()
        }

        deletePostButton.setOnClickListener {
            deleteCurrentPost()
        }

        loadPost(postId!!)
    }

    private fun loadPost(postId: String) {
        progressBar.visibility = View.VISIBLE

        postRemoteDataSource.getPostById(
            postId = postId,
            onSuccess = { post ->
                progressBar.visibility = View.GONE
                currentPost = post
                showPost(post)
            },
            onFailure = { exception ->
                progressBar.visibility = View.GONE

                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Failed to load post",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().popBackStack()
            }
        )
    }

    private fun showPost(post: Post) {
        titleTextView.text = post.bookTitle
        authorTextView.text = post.bookAuthor
        ownerTextView.text = "Shared by ${post.ownerName}"
        descriptionTextView.text = post.description

        val imageUrl = post.customImageUrl ?: post.bookThumbnailUrl

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.default_book)
                .error(R.drawable.default_book)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.default_book)
        }

        val currentUserId = authRemoteDataSource.getCurrentUserId()
        val isOwner = post.ownerUid == currentUserId

        ownerActionsLayout.visibility = if (isOwner) View.VISIBLE else View.GONE
    }

    private fun openEditPost() {
        val post = currentPost ?: return

        val action =
            PostDetailsFragmentDirections
                .actionPostDetailsFragmentToCreateEditPostFragment(post.id)

        findNavController().navigate(action)
    }


    private fun deleteCurrentPost() {
        val post = currentPost ?: return
        val currentUserId = authRemoteDataSource.getCurrentUserId()

        if (post.ownerUid != currentUserId) {
            Toast.makeText(
                requireContext(),
                "You can delete only your own posts",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        editPostButton.isEnabled = false
        deletePostButton.isEnabled = false

        postRepository.deletePostFromRemote(
            postId = post.id,
            onSuccess = {
                lifecycleScope.launch {
                    postRepository.deletePost(post.id)

                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Post deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().popBackStack()
                }
            },
            onFailure = { exception ->
                progressBar.visibility = View.GONE
                editPostButton.isEnabled = true
                deletePostButton.isEnabled = true

                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Failed to delete post",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}
