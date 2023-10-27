package com.github.clasicrando.users

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId

interface UsersDao {
    suspend fun getById(userId: UserId): User?
    suspend fun validateUser(loginRequest: LoginRequest): UserId?
}
