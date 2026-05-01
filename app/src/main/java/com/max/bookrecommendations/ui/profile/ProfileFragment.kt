package com.max.bookrecommendations.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var editProfileButton: MaterialButton
    private lateinit var logoutButton: MaterialButton

    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        logoutButton = view.findViewById(R.id.logoutButton)

        showCurrentUser()

        logoutButton.setOnClickListener {
            authRemoteDataSource.logout()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(R.id.authFragment, false)
        }

        editProfileButton.setOnClickListener {
            Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show()
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
    }
}