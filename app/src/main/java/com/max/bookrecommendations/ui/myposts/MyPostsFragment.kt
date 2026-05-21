package com.max.bookrecommendations.ui.myposts

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.local.DatabaseProvider
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.repository.PostRepository
import com.max.bookrecommendations.ui.feed.PostAdapter

class MyPostsFragment : Fragment(R.layout.fragment_my_posts) {

    private lateinit var myPostsViewModel: MyPostsViewModel
    private lateinit var postAdapter: PostAdapter

    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPostsRecyclerView: RecyclerView = view.findViewById(R.id.myPostsRecyclerView)
        val myPostsProgressBar: ProgressBar = view.findViewById(R.id.myPostsProgressBar)
        val emptyMyPostsTextView: TextView = view.findViewById(R.id.emptyMyPostsTextView)

        postAdapter = PostAdapter(mutableListOf()) { post ->
            val action =
                MyPostsFragmentDirections.actionMyPostsFragmentToPostDetailsFragment(post.id)

            findNavController().navigate(action)
        }

        myPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myPostsRecyclerView.adapter = postAdapter

        val database = DatabaseProvider.getDatabase(requireContext())
        val postRepository = PostRepository(database.postDao())
        val factory = MyPostsViewModelFactory(postRepository)

        myPostsViewModel = ViewModelProvider(this, factory)[MyPostsViewModel::class.java]

        myPostsViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitPosts(posts)

            emptyMyPostsTextView.visibility =
                if (posts.isEmpty()) View.VISIBLE else View.GONE
        }

        myPostsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            myPostsProgressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        myPostsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        val currentUserId = authRemoteDataSource.getCurrentUserId()

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        myPostsViewModel.loadMyPosts(currentUserId)
    }

    override fun onResume() {
        super.onResume()

        val currentUserId = authRemoteDataSource.getCurrentUserId()
        if (::myPostsViewModel.isInitialized && currentUserId != null) {
            myPostsViewModel.loadMyPosts(currentUserId)
        }
    }
}
