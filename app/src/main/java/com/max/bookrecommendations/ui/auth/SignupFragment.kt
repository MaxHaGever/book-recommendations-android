package com.max.bookrecommendations.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.max.bookrecommendations.R

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var signupButton: MaterialButton

    private val viewModel: SignupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInputLayout = view.findViewById(R.id.nameInputLayout)
        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout)
        signupButton = view.findViewById(R.id.signupButton)

        val goToLoginButton: View = view.findViewById(R.id.goToLoginButton)

        observeViewModel()

        signupButton.setOnClickListener {
            if (validateSignupForm()) {
                signupUser()
            }
        }

        goToLoginButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            signupButton.isEnabled = !isLoading
            signupButton.text = if (isLoading) "Creating account..." else "Sign Up"
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.signupSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                navigateToFeedAndClearAuth()
            }
        }
    }

    private fun navigateToFeedAndClearAuth() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()

        findNavController().navigate(
            R.id.feedFragment,
            null,
            navOptions
        )
    }

    private fun signupUser() {
        val name = nameInputLayout.editText?.text.toString().trim()
        val email = emailInputLayout.editText?.text.toString().trim()
        val password = passwordInputLayout.editText?.text.toString().trim()

        viewModel.signup(
            name = name,
            email = email,
            password = password
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
