package com.max.bookrecommendations.ui.createeditpost

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.local.DatabaseProvider
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource
import com.max.bookrecommendations.data.repository.PostRepository
import com.squareup.picasso.Picasso

class CreateEditPostFragment : Fragment(R.layout.fragment_create_edit_post) {

    private val args: CreateEditPostFragmentArgs by navArgs()

    private lateinit var titleEditText: TextInputEditText
    private lateinit var reviewEditText: TextInputEditText
    private lateinit var postImagePreview: ImageView
    private lateinit var backButton: MaterialButton
    private lateinit var screenTitleTextView: TextView
    private lateinit var savePostButton: MaterialButton
    private lateinit var authorEditText: TextInputEditText
    private lateinit var bookSearchEditText: TextInputEditText
    private lateinit var searchBookButton: MaterialButton
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var booksAdapter: BookSearchAdapter

    private lateinit var createEditPostViewModel: CreateEditPostViewModel

    private var selectedImageUri: Uri? = null
    private var selectedApiImageUrl: String? = null
    private var postId: String? = null
    private var isEditMode = false

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
        backButton = view.findViewById(R.id.createEditPostBackButton)
        screenTitleTextView = view.findViewById(R.id.createEditPostTitleTextView)
        savePostButton = view.findViewById(R.id.savePostButton)
        authorEditText = view.findViewById(R.id.authorEditText)
        bookSearchEditText = view.findViewById(R.id.bookSearchEditText)
        searchBookButton = view.findViewById(R.id.searchBookButton)
        booksRecyclerView = view.findViewById(R.id.booksRecyclerView)

        val changeImageButton: MaterialButton =
            view.findViewById(R.id.changePostImageButton)

        setupViewModel()
        setupBooksRecyclerView()
        observeViewModel()

        postId = args.postId
        isEditMode = !postId.isNullOrEmpty()

        if (isEditMode) {
            screenTitleTextView.text = "Edit Post"
            savePostButton.text = "Update Post"
            createEditPostViewModel.loadPostForEdit(postId!!)
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        changeImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        searchBookButton.setOnClickListener {
            val query = bookSearchEditText.text.toString().trim()

            if (query.isNotEmpty()) {
                createEditPostViewModel.searchBooks(query)
            }
        }

        savePostButton.setOnClickListener {
            savePost()
        }
    }

    private fun setupViewModel() {
        val database = DatabaseProvider.getDatabase(requireContext())
        val postRepository = PostRepository(database.postDao())
        val authRemoteDataSource = AuthRemoteDataSource()
        val storageRemoteDataSource = StorageRemoteDataSource()

        val factory = CreateEditPostViewModelFactory(
            postRepository = postRepository,
            authRemoteDataSource = authRemoteDataSource,
            storageRemoteDataSource = storageRemoteDataSource
        )

        createEditPostViewModel =
            ViewModelProvider(this, factory)[CreateEditPostViewModel::class.java]
    }

    private fun setupBooksRecyclerView() {
        booksAdapter = BookSearchAdapter(mutableListOf()) { selectedBook ->
            titleEditText.setText(selectedBook.title)
            authorEditText.setText(selectedBook.authors)

            if (!selectedBook.thumbnailUrl.isNullOrEmpty()) {
                val secureImageUrl =
                    selectedBook.thumbnailUrl.replace("http://", "https://")

                selectedApiImageUrl = secureImageUrl

                Picasso.get()
                    .load(secureImageUrl)
                    .placeholder(R.drawable.default_book)
                    .error(R.drawable.default_book)
                    .into(postImagePreview)
            }

            booksRecyclerView.visibility = View.GONE
        }

        booksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        booksRecyclerView.adapter = booksAdapter
    }

    private fun observeViewModel() {
        createEditPostViewModel.postForEdit.observe(viewLifecycleOwner) { post ->
            showPostForEdit(post)
        }

        createEditPostViewModel.bookResults.observe(viewLifecycleOwner) { books ->
            booksAdapter.updateBooks(books)
            booksRecyclerView.visibility =
                if (books.isEmpty()) View.GONE else View.VISIBLE
        }

        createEditPostViewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            searchBookButton.isEnabled = !isSearching
            searchBookButton.text =
                if (isSearching) "Searching..." else "Search Book"
        }

        createEditPostViewModel.isSaving.observe(viewLifecycleOwner) { isSaving ->
            savePostButton.isEnabled = !isSaving
            savePostButton.text = when {
                isSaving && isEditMode -> "Updating..."
                isSaving -> "Posting..."
                isEditMode -> "Update Post"
                else -> "Post"
            }
        }

        createEditPostViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        createEditPostViewModel.saveSuccess.observe(viewLifecycleOwner) { saveSuccess ->
            if (saveSuccess) {
                Toast.makeText(
                    requireContext(),
                    if (isEditMode) {
                        "Post updated successfully"
                    } else {
                        "Post created successfully"
                    },
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()
            }
        }
    }

    private fun showPostForEdit(post: Post) {
        titleEditText.setText(post.bookTitle)
        authorEditText.setText(post.bookAuthor)
        reviewEditText.setText(post.description)

        if (!post.customImageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(post.customImageUrl)
                .placeholder(R.drawable.default_book)
                .error(R.drawable.default_book)
                .into(postImagePreview)
        } else {
            postImagePreview.setImageResource(R.drawable.default_book)
        }
    }

    private fun savePost() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val review = reviewEditText.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || review.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill all fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        createEditPostViewModel.savePost(
            postId = postId,
            title = title,
            author = author,
            review = review,
            selectedImageUri = selectedImageUri,
            selectedApiImageUrl = selectedApiImageUrl
        )
    }
}
