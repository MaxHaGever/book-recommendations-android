package com.max.bookrecommendations.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.UserProfileChangeRequest
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.max.bookrecommendations.data.remote.StorageRemoteDataSource

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var saveButton: MaterialButton
    private lateinit var profileImagePreview: ImageView
    private var selectedImageUri: Uri? = null
    private val authRemoteDataSource = AuthRemoteDataSource()
    private val storageRemoteDataSource = StorageRemoteDataSource()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profileImagePreview.imageTintList = null
                profileImagePreview.setImageURI(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInputLayout = view.findViewById(R.id.nameInputLayout)
        saveButton = view.findViewById(R.id.saveButton)
        profileImagePreview = view.findViewById(R.id.profileImagePreview)

        val changeImageButton: MaterialButton = view.findViewById(R.id.changeProfileImageButton)

        val currentUser = authRemoteDataSource.getCurrentUser()

        if (currentUser == null) {
            findNavController().popBackStack()
            return
        }

        nameInputLayout.editText?.setText(currentUser.displayName ?: "")

        changeImageButton.setOnClickListener {
            openGallery()
        }

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

        if (currentUser == null) return

        saveButton.isEnabled = false
        saveButton.text = "Saving..."

        if (selectedImageUri != null) {

            storageRemoteDataSource.uploadProfileImage(
                currentUser.uid,
                selectedImageUri!!,
                onSuccess = { imageUrl ->

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(Uri.parse(imageUrl))
                        .build()

                    currentUser.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener { exception ->
                            handleError(exception)
                        }

                },
                onFailure = { exception ->
                    handleError(exception)
                }
            )

        } else {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            currentUser.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .addOnFailureListener { exception ->
                    handleError(exception)
                }
        }
    }

    private fun handleError(exception: Exception) {
        saveButton.isEnabled = true
        saveButton.text = "Save Changes"

        Toast.makeText(
            requireContext(),
            exception.message ?: "Update failed",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
}