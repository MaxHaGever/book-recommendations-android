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
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.AppContainer
import com.max.bookrecommendations.ui.common.PostImageLoader
import com.max.bookrecommendations.ui.feed.PostAdapter

class MyPostsFragment : Fragment(R.layout.fragment_my_posts) {

    private lateinit var myPostsViewModel: MyPostsViewModel
    private lateinit var postAdapter: PostAdapter
    private lateinit var postImageLoader: PostImageLoader

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPostsRecyclerView: RecyclerView = view.findViewById(R.id.myPostsRecyclerView)
        val myPostsProgressBar: ProgressBar = view.findViewById(R.id.myPostsProgressBar)
        val emptyMyPostsTextView: TextView = view.findViewById(R.id.emptyMyPostsTextView)
        val profileButton: MaterialButton = view.findViewById(R.id.myPostsProfileButton)

        profileButton.setOnClickListener {
            findNavController().popBackStack()
        }

        postImageLoader = AppContainer.postImageLoader(requireContext())

        postAdapter = PostAdapter(
            posts = mutableListOf(),
            onPostClick = { post ->
                val action =
                    MyPostsFragmentDirections.actionMyPostsFragmentToPostDetailsFragment(post.id)

                findNavController().navigate(action)
            },
            onLoadPostImage = { post, imageView ->
                postImageLoader.load(viewLifecycleOwner, post, imageView)
            }
        )

        myPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myPostsRecyclerView.adapter = postAdapter

        val factory = MyPostsViewModelFactory(requireContext().applicationContext)
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

        val currentUserId = myPostsViewModel.getCurrentUserId()

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        myPostsViewModel.loadMyPosts(currentUserId)
    }

    override fun onResume() {
        super.onResume()

        if (!::myPostsViewModel.isInitialized) {
            return
        }

        val currentUserId = myPostsViewModel.getCurrentUserId()
        if (currentUserId != null) {
            myPostsViewModel.loadMyPosts(currentUserId)
        }
    }
}
