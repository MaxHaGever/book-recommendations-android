package com.max.bookrecommendations.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var editProfileButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var profileImageView: ImageView
    private lateinit var createPostButton: MaterialButton

    private lateinit var myPostsButton: MaterialButton


    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        logoutButton = view.findViewById(R.id.logoutButton)
        profileImageView = view.findViewById(R.id.profileImageView)
        createPostButton = view.findViewById(R.id.createPostButton)
        myPostsButton = view.findViewById(R.id.myPostsButton)


        showCurrentUser()

        logoutButton.setOnClickListener {
            authRemoteDataSource.logout()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(R.id.authFragment, false)
        }

        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        createPostButton.setOnClickListener {
            findNavController().navigate(R.id.createEditPostFragment)
        }

        myPostsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myPostsFragment)
        }

    }

    private fun showCurrentUser() {
        val currentUser = authRemoteDataSource.getCurrentUser()

        if (currentUser == null) {
            findNavController().popBackStack(R.id.authFragment, false)
            return
        }

        nameTextView.text = currentUser.displayName ?: "Book Lover"
        emailTextView.text = currentUser.email ?: "No email"

        val photoUrl = currentUser.photoUrl

        if (photoUrl != null) {
            profileImageView.imageTintList = null
            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.outline_account_circle_24)
                .error(R.drawable.outline_account_circle_24)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.outline_account_circle_24)
        }
    }
}