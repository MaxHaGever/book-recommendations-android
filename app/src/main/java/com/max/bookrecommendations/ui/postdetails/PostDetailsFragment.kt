package com.max.bookrecommendations.ui.postdetails

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.AppContainer
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.ui.common.PostImageLoader

class PostDetailsFragment : Fragment(R.layout.fragment_post_details) {

    private val args: PostDetailsFragmentArgs by navArgs()

    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var ownerTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var ownerActionsLayout: LinearLayout
    private lateinit var backButton: MaterialButton
    private lateinit var editPostButton: MaterialButton
    private lateinit var deletePostButton: MaterialButton

    private lateinit var postDetailsViewModel: PostDetailsViewModel
    private lateinit var postImageLoader: PostImageLoader

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
        backButton = view.findViewById(R.id.postDetailsBackButton)
        editPostButton = view.findViewById(R.id.editPostButton)
        deletePostButton = view.findViewById(R.id.deletePostButton)

        postImageLoader = AppContainer.postImageLoader(requireContext())

        val factory = PostDetailsViewModelFactory(
            requireContext().applicationContext
        )

        postDetailsViewModel =
            ViewModelProvider(this, factory)[PostDetailsViewModel::class.java]

        postId = args.postId

        observeViewModel()

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        editPostButton.setOnClickListener {
            openEditPost()
        }

        deletePostButton.setOnClickListener {
            deleteCurrentPost()
        }

        postDetailsViewModel.loadPost(postId!!)
    }

    private fun observeViewModel() {
        postDetailsViewModel.post.observe(viewLifecycleOwner) { post ->
            currentPost = post
            showPost(post)
        }

        postDetailsViewModel.isOwner.observe(viewLifecycleOwner) { isOwner ->
            ownerActionsLayout.visibility =
                if (isOwner) View.VISIBLE else View.GONE
        }

        postDetailsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        postDetailsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                editPostButton.isEnabled = true
                deletePostButton.isEnabled = true

                Toast.makeText(
                    requireContext(),
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        postDetailsViewModel.deleteSuccess.observe(viewLifecycleOwner) { deleteSuccess ->
            if (deleteSuccess) {
                Toast.makeText(
                    requireContext(),
                    "Post deleted",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()
            }
        }
    }

    private fun showPost(post: Post) {
        titleTextView.text = post.bookTitle
        authorTextView.text = post.bookAuthor
        ownerTextView.text = "Shared by ${post.ownerName}"
        descriptionTextView.text = post.description

        postImageLoader.load(
            lifecycleOwner = viewLifecycleOwner,
            post = post,
            imageView = imageView,
            onDefaultImage = { showDefaultPostImage() },
            onRealImage = { showRealPostImage() }
        )
    }

    private fun showRealPostImage() {
        imageView.setPadding(0, 0, 0, 0)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun showDefaultPostImage() {
        val padding = resources.getDimensionPixelSize(R.dimen.post_placeholder_padding)
        imageView.setPadding(padding, padding, padding, padding)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
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

        editPostButton.isEnabled = false
        deletePostButton.isEnabled = false

        postDetailsViewModel.deletePost(post)
    }
}
