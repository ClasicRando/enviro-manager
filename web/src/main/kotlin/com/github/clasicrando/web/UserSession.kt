package com.github.clasicrando.web

import io.ktor.server.auth.Principal
import java.util.UUID

data class UserSession(val userId: UUID) : Principal
