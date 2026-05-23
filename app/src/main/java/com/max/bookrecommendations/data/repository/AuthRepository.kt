package com.max.bookrecommendations.data.repository

import com.google.firebase.auth.FirebaseUser
import com.max.bookrecommendations.data.remote.AuthRemoteDataSource

class AuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) {

    fun login(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        authRemoteDataSource.login(
            email = email,
            password = password,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun signup(
        email: String,
        password: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        authRemoteDataSource.signup(
            email = email,
            password = password,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun logout() {
        authRemoteDataSource.logout()
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRemoteDataSource.getCurrentUser()
    }

    fun getCurrentUserId(): String? {
        return authRemoteDataSource.getCurrentUserId()
    }

    fun isUserLoggedIn(): Boolean {
        return authRemoteDataSource.isUserLoggedIn()
    }
}