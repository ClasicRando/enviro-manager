package com.github.clasicrando.users.data

import com.github.clasicrando.requests.LoginRequest
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.users.model.UserId

interface UsersDao {
    suspend fun getById(userId: UserId): User?

    suspend fun getByUsername(username: String): User?

    suspend fun validateUser(loginRequest: LoginRequest): UserId?

    suspend fun getWithRole(role: Role): List<User>

    suspend fun getAll(): List<User>

    suspend fun disableUser(userId: UserId)

    suspend fun enableUser(userId: UserId)

    suspend fun modifyRoles(
        userId: UserId,
        roles: List<Role>,
    )

    suspend fun updateUser(
        userId: UserId,
        username: String,
        fullName: String,
    )
}
