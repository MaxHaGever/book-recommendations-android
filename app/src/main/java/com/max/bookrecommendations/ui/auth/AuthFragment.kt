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

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var loginButton: MaterialButton

    private val authRemoteDataSource = AuthRemoteDataSource()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (authRemoteDataSource.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_authFragment_to_feedFragment)
            return
        }

        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)
        loginButton = view.findViewById(R.id.loginButton)

        val goToSignupButton: View = view.findViewById(R.id.goToSignupButton)

        goToSignupButton.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_signupFragment)
        }

        loginButton.setOnClickListener {
            if (validateLoginForm()) {
                loginUser()
            }
        }
    }

    private fun validateLoginForm(): Boolean {
        val email = emailInputLayout.editText?.text.toString().trim()
        val password = passwordInputLayout.editText?.text.toString().trim()

        var isValid = true

        if (email.isEmpty()) {
            emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Invalid email"
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

        return isValid
    }

    private fun loginUser() {
        val email = emailInputLayout.editText?.text.toString().trim()
        val password = passwordInputLayout.editText?.text.toString().trim()

        loginButton.isEnabled = false
        loginButton.text = "Logging in..."

        authRemoteDataSource.login(
            email = email,
            password = password,
            onSuccess = {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                loginButton.isEnabled = true
                loginButton.text = "Login"

                findNavController().navigate(R.id.action_authFragment_to_feedFragment)
            },
            onFailure = { exception ->
                loginButton.isEnabled = true
                loginButton.text = "Login"

                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Login failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}
