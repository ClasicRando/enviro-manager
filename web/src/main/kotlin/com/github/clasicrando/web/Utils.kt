package com.github.clasicrando.web

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.regions.data.RegionsDao
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.component.BasePage
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.workflows.data.WorkflowsDao
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.p
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

const val MAIN_CONTENT_ID = "main"
const val MAIN_CONTENT_TARGET = "#$MAIN_CONTENT_ID"
const val NO_DISPLAY_ELEMENT_TARGET = "#noDisplay"

enum class GridTier(
    val cssName: String,
) {
    ExtraSmall(""),
    Small("sm"),
    Medium("md"),
    Large("lg"),
    ExtraLarge("xl"),
    ExtraExtraLarge("xxl"),
}

/**
 * Extension property of route handlers that resolves a [UsersDao] using the [closestDI] method.
 * If you need to use this property multiple times in a route handler, store the result of this
 * property in a local variable to avoid having to resolve the dependency multiple times.
 */
val PipelineContext<Unit, ApplicationCall>.usersDao
    get(): UsersDao {
        val dao: UsersDao by closestDI().instance()
        return dao
    }

/**
 * Extension property of route handlers that resolves a [WorkflowsDao] using the [closestDI] method.
 * If you need to use this property multiple times in a route handler, store the result of this
 * property in a local variable to avoid having to resolve the dependency multiple times.
 */
val PipelineContext<Unit, ApplicationCall>.workflowsDao
    get(): WorkflowsDao {
        val dao: WorkflowsDao by closestDI().instance()
        return dao
    }

/**
 * Extension property of route handlers that resolves a [WorkflowsDao] using the [closestDI] method.
 * If you need to use this property multiple times in a route handler, store the result of this
 * property in a local variable to avoid having to resolve the dependency multiple times.
 */
val PipelineContext<Unit, ApplicationCall>.regionsDao
    get(): RegionsDao {
        val dao: RegionsDao by closestDI().instance()
        return dao
    }

/**
 * Extension property of route handlers that resolves a [DataSourcesDao] using the [closestDI]
 * method. If you need to use this property multiple times in a route handler, store the result of
 * this property in a local variable to avoid having to resolve the dependency multiple times.
 */
val PipelineContext<Unit, ApplicationCall>.dataSourcesDao
    get(): DataSourcesDao {
        val dao: DataSourcesDao by closestDI().instance()
        return dao
    }

/**
 * Extension property of route handlers that resolves a [DataSourceContactsDao] using the
 * [closestDI] method. If you need to use this property multiple times in a route handler, store
 * the result of this property in a local variable to avoid having to resolve the dependency
 * multiple times.
 */
val PipelineContext<Unit, ApplicationCall>.dataSourceContactsDao
    get(): DataSourceContactsDao {
        val dao: DataSourceContactsDao by closestDI().instance()
        return dao
    }

/**
 * Extension property of route handlers that resolves a [RecordWarehouseTypesDao] using the
 * [closestDI] method. If you need to use this property multiple times in a route handler, store
 * the result of this property in a local variable to avoid having to resolve the dependency
 * multiple times.
 */
val PipelineContext<Unit, ApplicationCall>.recordWarehouseTypesDao
    get(): RecordWarehouseTypesDao {
        val dao: RecordWarehouseTypesDao by closestDI().instance()
        return dao
    }

suspend fun PipelineContext<Unit, ApplicationCall>.userSessionOrRedirect(): UserSession? =
    call.userSessionOrRedirect()

suspend fun PipelineContext<Unit, ApplicationCall>.userOrRedirect(): User? =
    call.userOrRedirect(usersDao)

suspend fun PipelineContext<Unit, ApplicationCall>.userWithRoleOrRespond(role: Role): User? =
    call.userWithRoleOrRespond(dao = usersDao, role = role)

suspend fun PipelineContext<Unit, ApplicationCall>.adminUserOrRespondMaybeHtmxError(): User? =
    call.adminUserOrRespondMaybeHtmxError(usersDao)

suspend fun ApplicationCall.userSessionOrRedirect(): UserSession? {
    val userSession = sessions.get<UserSession>()
    if (userSession == null) {
        respondRedirect("/login")
        return null
    }
    return userSession
}

suspend fun ApplicationCall.userOrRedirect(dao: UsersDao): User? {
    val userSession = userSessionOrRedirect() ?: return null
    val user = dao.getById(userId = userSession.userId)
    if (user == null) {
        respondRedirect("/login")
        return null
    }
    return user
}

suspend fun ApplicationCall.userWithRoleOrRespond(
    dao: UsersDao,
    role: Role,
): User? {
    val user = userOrRedirect(dao) ?: return null
    if (!user.hasRole(role)) {
        if (shouldRespondHtmx) {
            respondHtmx {
                addCreateToastEvent("User missing role: $role")
            }
        } else {
            respondHtml {
                BasePage(user = user, pageTitle = "Missing Role") {
                    p {
                        +"You are missing the role, $role, that is required for accessing this page"
                    }
                }
            }
        }
        return null
    }
    return user
}

suspend fun ApplicationCall.adminUserOrRespondMaybeHtmxError(dao: UsersDao): User? =
    userWithRoleOrRespond(dao = dao, role = Role.Admin)

val ApplicationRequest.isHtmx: Boolean get() = this.headers.contains("HX-Request")

val ApplicationRequest.isBoost: Boolean get() = this.headers.contains("HX-Boosted")

val ApplicationCall.shouldRespondHtmx: Boolean get() = request.isHtmx && !request.isBoost
