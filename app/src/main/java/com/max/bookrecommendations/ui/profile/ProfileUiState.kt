package com.max.bookrecommendations.ui.profile

data class ProfileUiState(
    val name: String = "Book Lover",
    val email: String = "No email",
    val photoUrl: String? = null,
    val isLoggedIn: Boolean = true,
    val errorMessage: String? = null
)