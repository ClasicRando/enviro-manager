package com.github.clasicrando.web

import com.github.clasicrando.users.model.UserId
import io.ktor.server.auth.Principal

data class UserSession(val userId: UserId) : Principal
