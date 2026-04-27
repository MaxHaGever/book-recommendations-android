package com.max.bookrecommendations.data.repository

import com.max.bookrecommendations.data.local.UserDao
import com.max.bookrecommendations.data.mapper.toEntity
import com.max.bookrecommendations.data.mapper.toUser
import com.max.bookrecommendations.data.model.User

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun getUserById(uid: String): User? {
        return userDao.getUserById(uid)?.toUser()
    }

    suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    suspend fun deleteUser(uid: String) {
        userDao.deleteUserById(uid)
    }
}