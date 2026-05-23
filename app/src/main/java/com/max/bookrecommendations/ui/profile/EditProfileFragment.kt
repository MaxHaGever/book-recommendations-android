package com.max.bookrecommendations.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.max.bookrecommendations.R

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var saveButton: MaterialButton
    private lateinit var profileImagePreview: ImageView

    private var selectedImageUri: Uri? = null

    private val viewModel: EditProfileViewModel by viewModels()

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

        val changeImageButton: MaterialButton =
            view.findViewById(R.id.changeProfileImageButton)

        nameInputLayout.editText?.setText(viewModel.getCurrentName())

        observeViewModel()

        changeImageButton.setOnClickListener {
            openGallery()
        }

        saveButton.setOnClickListener {
            if (validateForm()) {
                updateProfile()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isSaving.observe(viewLifecycleOwner) { isSaving ->
            saveButton.isEnabled = !isSaving
            saveButton.text = if (isSaving) "Saving..." else "Save Changes"
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
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

    private fun updateProfile() {
        val name = nameInputLayout.editText?.text.toString().trim()

        viewModel.updateProfile(
            name = name,
            imageUri = selectedImageUri
        )
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
}