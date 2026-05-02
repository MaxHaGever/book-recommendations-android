package com.max.bookrecommendations.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.UserProfileChangeRequest
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var saveButton: MaterialButton

    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInputLayout = view.findViewById(R.id.nameInputLayout)
        saveButton = view.findViewById(R.id.saveButton)

        val currentUser = authRemoteDataSource.getCurrentUser()

        if (currentUser == null) {
            findNavController().popBackStack()
            return
        }

        nameInputLayout.editText?.setText(currentUser.displayName ?: "")

        saveButton.setOnClickListener {
            if (validateForm()) {
                updateProfileName()
            }
        }
    }

    private fun validateForm(): Boolean {
        val name = nameInputLayout.editText?.text.toString().trim()

        return if (name.isEmpty()) {
            nameInputLayout.error = "Name is required"
            false
        } else {
            nameInputLayout.error = null
            true
        }
    }

    private fun updateProfileName() {
        val name = nameInputLayout.editText?.text.toString().trim()
        val currentUser = authRemoteDataSource.getCurrentUser()

        saveButton.isEnabled = false
        saveButton.text = "Saving..."

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        currentUser?.updateProfile(profileUpdates)
            ?.addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            ?.addOnFailureListener { exception ->
                saveButton.isEnabled = true
                saveButton.text = "Save Changes"

                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Update failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}