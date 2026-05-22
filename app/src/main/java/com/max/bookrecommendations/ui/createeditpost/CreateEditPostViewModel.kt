package com.max.bookrecommendations.ui.createeditpost

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.max.bookrecommendations.BuildConfig
import com.max.bookrecommendations.data.model.BookSearchItem
import com.max.bookrecommendations.data.model.Post
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource
import com.max.bookrecommendations.data.remote.api.GoogleBooksResponse
import com.max.bookrecommendations.data.remote.api.RetrofitInstance
import com.max.bookrecommendations.data.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class CreateEditPostViewModel(
    private val postRepository: PostRepository,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val storageRemoteDataSource: StorageRemoteDataSource
) : ViewModel() {

    private val _postForEdit = MutableLiveData<Post>()
    val postForEdit: LiveData<Post> = _postForEdit

    private val _bookResults = MutableLiveData<List<BookSearchItem>>()
    val bookResults: LiveData<List<BookSearchItem>> = _bookResults

    private val _isLoadingPost = MutableLiveData<Boolean>()
    val isLoadingPost: LiveData<Boolean> = _isLoadingPost

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching

    private val _isSaving = MutableLiveData<Boolean>()
    val isSaving: LiveData<Boolean> = _isSaving

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private var existingPost: Post? = null

    fun loadPostForEdit(postId: String) {
        _isLoadingPost.value = true

        postRepository.getPostByIdFromRemote(
            postId = postId,
            onSuccess = { post ->
                existingPost = post
                _postForEdit.value = post
                _isLoadingPost.value = false

                viewModelScope.launch {
                    postRepository.savePost(post)
                }
            },
            onFailure = { exception ->
                _isLoadingPost.value = false
                _errorMessage.value = exception.message ?: "Failed to load post"
            }
        )
    }

    fun searchBooks(query: String) {
        _isSearching.value = true

        RetrofitInstance.api.searchBooks(
            query = query,
            apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
        ).enqueue(object : Callback<GoogleBooksResponse> {

            override fun onResponse(
                call: Call<GoogleBooksResponse>,
                response: Response<GoogleBooksResponse>
            ) {
                _isSearching.value = false

                if (!response.isSuccessful) {
                    _errorMessage.value = "Book search failed"
                    return
                }

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

                _bookResults.value = books
            }

            override fun onFailure(call: Call<GoogleBooksResponse>, t: Throwable) {
                _isSearching.value = false
                _errorMessage.value = t.message ?: "Book search failed"
            }
        })
    }

    fun savePost(
        postId: String?,
        title: String,
        author: String,
        review: String,
        selectedImageUri: Uri?,
        selectedApiImageUrl: String?
    ) {
        val currentUser = authRemoteDataSource.getCurrentUser()

        if (currentUser == null) {
            _errorMessage.value = "User not logged in"
            return
        }

        val isEditMode = !postId.isNullOrEmpty()

        val finalPostId = if (isEditMode) {
            postId!!
        } else {
            UUID.randomUUID().toString()
        }

        val updatedAt = System.currentTimeMillis()

        _isSaving.value = true

        when {
            selectedImageUri != null -> {
                storageRemoteDataSource.uploadPostImage(
                    postId = finalPostId,
                    imageUri = selectedImageUri,
                    onSuccess = { imageUrl ->
                        createAndSavePost(
                            postId = finalPostId,
                            title = title,
                            author = author,
                            review = review,
                            imageUrl = imageUrl,
                            updatedAt = updatedAt
                        )
                    },
                    onFailure = { exception ->
                        handleSaveFailure(exception)
                    }
                )
            }

            selectedApiImageUrl != null -> {
                storageRemoteDataSource.uploadPostImageFromUrl(
                    postId = finalPostId,
                    imageUrl = selectedApiImageUrl,
                    onSuccess = { imageUrl ->
                        createAndSavePost(
                            postId = finalPostId,
                            title = title,
                            author = author,
                            review = review,
                            imageUrl = imageUrl,
                            updatedAt = updatedAt
                        )
                    },
                    onFailure = { exception ->
                        handleSaveFailure(exception)
                    }
                )
            }

            else -> {
                createAndSavePost(
                    postId = finalPostId,
                    title = title,
                    author = author,
                    review = review,
                    imageUrl = existingPost?.customImageUrl,
                    updatedAt = updatedAt
                )
            }
        }
    }

    private fun createAndSavePost(
        postId: String,
        title: String,
        author: String,
        review: String,
        imageUrl: String?,
        updatedAt: Long
    ) {
        val currentUser = authRemoteDataSource.getCurrentUser()

        if (currentUser == null) {
            _isSaving.postValue(false)
            _errorMessage.postValue("User not logged in")
            return
        }

        val post = Post(
            id = postId,
            ownerUid = currentUser.uid,
            ownerName = currentUser.displayName ?: "User",
            ownerProfileImageUrl = currentUser.photoUrl?.toString(),
            bookTitle = title,
            bookAuthor = author,
            description = review,
            customImageUrl = imageUrl,
            createdAt = existingPost?.createdAt ?: updatedAt,
            lastUpdated = updatedAt
        )

        postRepository.savePostToRemote(
            post = post,
            onSuccess = {
                viewModelScope.launch {
                    postRepository.savePost(post)

                    _isSaving.value = false
                    _saveSuccess.value = true
                }
            },
            onFailure = { exception ->
                handleSaveFailure(exception)
            }
        )
    }

    private fun handleSaveFailure(exception: Exception) {
        _isSaving.postValue(false)
        _errorMessage.postValue(exception.message ?: "Failed to save post")
    }
}
