package com.max.bookrecommendations.ui.feed

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.local.DatabaseProvider
import com.max.bookrecommendations.data.repository.PostRepository
import com.google.android.material.button.MaterialButton

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var feedViewModel: FeedViewModel
    private lateinit var postAdapter: PostAdapter



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileButton: MaterialButton = view.findViewById(R.id.profileButton)
        val feedRecyclerView: RecyclerView = view.findViewById(R.id.feedRecyclerView)
        val feedProgressBar: ProgressBar = view.findViewById(R.id.feedProgressBar)
        val emptyFeedTextView: TextView = view.findViewById(R.id.emptyFeedTextView)
        val createPostFab: FloatingActionButton = view.findViewById(R.id.createPostFab)

        postAdapter = PostAdapter(mutableListOf()) { post ->
            val bundle = Bundle().apply {
                putString("postId", post.id)
            }

            findNavController().navigate(
                R.id.action_feedFragment_to_postDetailsFragment,
                bundle
            )
        }

        feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedRecyclerView.adapter = postAdapter

        val database = DatabaseProvider.getDatabase(requireContext())
        val postRepository = PostRepository(database.postDao())
        val factory = FeedViewModelFactory(postRepository)

        feedViewModel = ViewModelProvider(this, factory)[FeedViewModel::class.java]

        feedViewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitPosts(posts)

            emptyFeedTextView.visibility =
                if (posts.isEmpty()) View.VISIBLE else View.GONE
        }

        feedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            feedProgressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        feedViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        createPostFab.setOnClickListener {
            findNavController().navigate(R.id.createEditPostFragment)
        }

        profileButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_profileFragment)
        }


        feedViewModel.loadFeed()
    }
}
