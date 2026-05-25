package com.max.bookrecommendations.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var editProfileButton: MaterialButton
    private lateinit var myPostsButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var profileImageView: ImageView
    private lateinit var createPostButton: MaterialButton
    private lateinit var feedButton: MaterialButton

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        myPostsButton = view.findViewById(R.id.myPostsButton)
        logoutButton = view.findViewById(R.id.logoutButton)
        profileImageView = view.findViewById(R.id.profileImageView)
        createPostButton = view.findViewById(R.id.createPostButton)
        feedButton = view.findViewById(R.id.feedButton)

        observeViewModel()
        setupClickListeners()

        viewModel.loadProfile()
    }

    private fun observeViewModel() {
        viewModel.profileState.observe(viewLifecycleOwner) { state ->

            if (!state.isLoggedIn) {
                navigateToAuthAndClearBackStack()
                return@observe
            }

            nameTextView.text = state.name
            emailTextView.text = state.email

            if (!state.photoUrl.isNullOrEmpty()) {
                profileImageView.imageTintList = null

                Picasso.get()
                    .load(state.photoUrl)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_account_circle_24)
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.outline_account_circle_24)
            }

            if (!state.errorMessage.isNullOrBlank()) {
                Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.logoutSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                navigateToAuthAndClearBackStack()
            }
        }
    }

    private fun navigateToAuthAndClearBackStack() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()

        findNavController().navigate(
            R.id.authFragment,
            null,
            navOptions
        )
    }

    private fun setupClickListeners() {
        logoutButton.setOnClickListener {
            viewModel.logout()
        }

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        myPostsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myPostsFragment)
        }

        createPostButton.setOnClickListener {
            findNavController().navigate(R.id.createEditPostFragment)
        }

        feedButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_feedFragment)
        }
    }
}
