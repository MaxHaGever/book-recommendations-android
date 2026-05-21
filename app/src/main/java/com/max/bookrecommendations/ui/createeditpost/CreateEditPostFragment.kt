package com.max.bookrecommendations.ui.createeditpost

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
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
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max.bookrecommendations.BuildConfig
import com.max.bookrecommendations.data.model.BookSearchItem
import com.max.bookrecommendations.data.remote.api.GoogleBooksResponse
import com.max.bookrecommendations.data.remote.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateEditPostFragment : Fragment(R.layout.fragment_create_edit_post) {

    private val args: CreateEditPostFragmentArgs by navArgs()
    private lateinit var titleEditText: TextInputEditText
    private lateinit var reviewEditText: TextInputEditText
    private lateinit var postImagePreview: ImageView
    private lateinit var savePostButton: MaterialButton
    private val storageRemoteDataSource = StorageRemoteDataSource()
    private lateinit var authorEditText: TextInputEditText
    private var selectedImageUri: Uri? = null
    private val authRemoteDataSource = AuthRemoteDataSource()
    private val postRemoteDataSource = PostRemoteDataSource()
    private var postId: String? = null
    private var isEditMode = false
    private var existingImageUrl: String? = null
    private var existingCreatedAt: Long = 0L
    private lateinit var bookSearchEditText: TextInputEditText
    private lateinit var searchBookButton: MaterialButton
    private lateinit var booksRecyclerView: RecyclerView
    private var selectedApiImageUrl: String? = null

    private lateinit var booksAdapter: BookSearchAdapter

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
        bookSearchEditText = view.findViewById(R.id.bookSearchEditText)
        searchBookButton = view.findViewById(R.id.searchBookButton)
        booksRecyclerView = view.findViewById(R.id.booksRecyclerView)

        val changeImageButton: MaterialButton = view.findViewById(R.id.changePostImageButton)

        booksAdapter = BookSearchAdapter(mutableListOf()) { selectedBook ->

            titleEditText.setText(selectedBook.title)
            authorEditText.setText(selectedBook.authors)

            if (!selectedBook.thumbnailUrl.isNullOrEmpty()) {
                val secureImageUrl = selectedBook.thumbnailUrl.replace("http://", "https://")

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

        postId = args.postId
        isEditMode = !postId.isNullOrEmpty()

        if (isEditMode) {
            loadPostForEdit(postId!!)
            savePostButton.text = "Update Post"
        }

        changeImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        searchBookButton.setOnClickListener {

            val query = bookSearchEditText.text.toString().trim()

            if (query.isNotEmpty()) {
                searchBooks(query)
            }
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

        val finalPostId = if (isEditMode) {
            postId!!
        } else {
            java.util.UUID.randomUUID().toString()
        }

        savePostButton.isEnabled = false
        savePostButton.text = if (isEditMode) "Updating..." else "Posting..."

        if (selectedImageUri != null) {
            storageRemoteDataSource.uploadPostImage(
                finalPostId,
                selectedImageUri!!,
                onSuccess = { imageUrl ->
                    val post = Post(
                        id = finalPostId,
                        ownerUid = currentUser.uid,
                        ownerName = currentUser.displayName ?: "User",
                        ownerProfileImageUrl = currentUser.photoUrl?.toString(),
                        bookTitle = title,
                        bookAuthor = author,
                        description = review,
                        customImageUrl = imageUrl,
                        createdAt = if (isEditMode) existingCreatedAt else System.currentTimeMillis()
                    )

                    saveToFirestore(post)
                },
                onFailure = {
                    handleError(it)
                })
        } else if (selectedApiImageUrl != null) {

            storageRemoteDataSource.uploadPostImageFromUrl(
                finalPostId,
                selectedApiImageUrl!!,
                onSuccess = { imageUrl ->

                    val post = Post(
                        id = finalPostId,
                        ownerUid = currentUser.uid,
                        ownerName = currentUser.displayName ?: "User",
                        ownerProfileImageUrl = currentUser.photoUrl?.toString(),
                        bookTitle = title,
                        bookAuthor = author,
                        description = review,
                        customImageUrl = imageUrl,
                        createdAt = if (isEditMode)
                            existingCreatedAt
                        else
                            System.currentTimeMillis()
                    )

                    saveToFirestore(post)
                },
                onFailure = {
                    handleError(it)
                }
            )
        } else {
            val post = Post(
                id = finalPostId,
                ownerUid = currentUser.uid,
                ownerName = currentUser.displayName ?: "User",
                ownerProfileImageUrl = currentUser.photoUrl?.toString(),
                bookTitle = title,
                bookAuthor = author,
                description = review,
                customImageUrl = existingImageUrl,
                createdAt = if (isEditMode) existingCreatedAt else System.currentTimeMillis()
            )

            saveToFirestore(post)
        }
    }

    private fun saveToFirestore(post: Post) {
        postRemoteDataSource.savePost(
            post = post,
            onSuccess = {
                Toast.makeText(
                    requireContext(),
                    if (isEditMode) "Post updated successfully" else "Post created successfully",
                    Toast.LENGTH_SHORT
                ).show()

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
            requireContext(), exception.message ?: "Failed", Toast.LENGTH_LONG
        ).show()
    }

    private fun loadPostForEdit(postId: String) {
        postRemoteDataSource.getPostById(postId = postId, onSuccess = { post ->
            titleEditText.setText(post.bookTitle)
            authorEditText.setText(post.bookAuthor)
            reviewEditText.setText(post.description)

            existingImageUrl = post.customImageUrl
            existingCreatedAt = post.createdAt

            if (!post.customImageUrl.isNullOrEmpty()) {
                Picasso.get().load(post.customImageUrl).into(postImagePreview)
            }
        }, onFailure = {
            handleError(it)
        })
    }

    private fun searchBooks(query: String) {

        Toast.makeText(
            requireContext(),
            "Key length: ${BuildConfig.GOOGLE_BOOKS_API_KEY.length}",
            Toast.LENGTH_SHORT
        ).show()

        RetrofitInstance.api.searchBooks(
            query,
            BuildConfig.GOOGLE_BOOKS_API_KEY
        )
            .enqueue(object : Callback<GoogleBooksResponse> {

                override fun onResponse(
                    call: Call<GoogleBooksResponse>,
                    response: Response<GoogleBooksResponse>
                ) {

                    if (response.isSuccessful) {

                        val books = response.body()?.items?.mapNotNull { item ->

                            val volumeInfo = item.volumeInfo ?: return@mapNotNull null

                            BookSearchItem(
                                googleBookId = item.id ?: "",
                                title = volumeInfo.title ?: "Unknown title",
                                authors = volumeInfo.authors?.joinToString(", ")
                                    ?: "Unknown author",
                                thumbnailUrl = volumeInfo.imageLinks?.thumbnail,
                                description = volumeInfo.description
                            )
                        } ?: emptyList()

                        booksRecyclerView.visibility = View.VISIBLE
                        booksAdapter.updateBooks(books)
                    }
                }

                override fun onFailure(call: Call<GoogleBooksResponse>, t: Throwable) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}