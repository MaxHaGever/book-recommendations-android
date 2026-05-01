package com.max.bookrecommendations.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.max.bookrecommendations.R
import androidx.navigation.fragment.findNavController

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)

        val loginButton: View = view.findViewById(R.id.loginButton)

        val goToSignupButton: View = view.findViewById(R.id.goToSignupButton)

        goToSignupButton.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_signupFragment)
        }

        loginButton.setOnClickListener {
            validateLoginForm()
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
}