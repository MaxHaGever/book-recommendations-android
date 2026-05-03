package com.max.bookrecommendations.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource
import com.google.firebase.auth.UserProfileChangeRequest

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var signupButton: MaterialButton

    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInputLayout = view.findViewById(R.id.nameInputLayout)
        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout)
        signupButton = view.findViewById(R.id.signupButton)

        val goToLoginButton: View = view.findViewById(R.id.goToLoginButton)

        signupButton.setOnClickListener {
            if (validateSignupForm()) {
                signupUser()
            }
        }

        goToLoginButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun signupUser() {
        val email = emailInputLayout.editText?.text.toString().trim()
        val password = passwordInputLayout.editText?.text.toString().trim()

        signupButton.isEnabled = false
        signupButton.text = "Creating account..."

        authRemoteDataSource.signup(
            email = email,
            password = password,
            onSuccess = { user ->
                val name = nameInputLayout.editText?.text.toString().trim()

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    ?.addOnFailureListener { exception ->
                        signupButton.isEnabled = true
                        signupButton.text = "Sign Up"

                        Toast.makeText(
                            requireContext(),
                            exception.message ?: "Profile update failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            },
            onFailure = { exception ->
                signupButton.isEnabled = true
                signupButton.text = "Sign Up"

                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Signup failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun validateSignupForm(): Boolean {
        val name = nameInputLayout.editText?.text.toString().trim()
        val email = emailInputLayout.editText?.text.toString().trim()
        val password = passwordInputLayout.editText?.text.toString().trim()
        val confirmPassword = confirmPasswordInputLayout.editText?.text.toString().trim()

        var isValid = true

        if (name.isEmpty()) {
            nameInputLayout.error = "Name is required"
            isValid = false
        } else {
            nameInputLayout.error = null
        }

        if (email.isEmpty()) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Please enter a valid email"
            isValid = false
        } else {
            emailInputLayout.error = null
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = "Please confirm your password"
            isValid = false
        } else if (confirmPassword != password) {
            confirmPasswordInputLayout.error = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordInputLayout.error = null
        }

        return isValid
    }
}