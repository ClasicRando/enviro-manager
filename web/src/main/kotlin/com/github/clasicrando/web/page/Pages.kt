package com.github.clasicrando.web.page

import com.github.clasicrando.datasources.model.toDsId
import com.github.clasicrando.pipelines.model.toRunId
import com.github.clasicrando.users.model.Role
import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.UserSession
import com.github.clasicrando.web.adminUserOrRespondMaybeHtmxError
import com.github.clasicrando.web.component.AdminDashboard
import com.github.clasicrando.web.component.BasePage
import com.github.clasicrando.web.component.DataSourceView
import com.github.clasicrando.web.component.DataSourcesTable
import com.github.clasicrando.web.component.LoginForm
import com.github.clasicrando.web.component.PipelineRunView
import com.github.clasicrando.web.component.PipelineRunsTable
import com.github.clasicrando.web.component.TasksTable
import com.github.clasicrando.web.component.WorkflowsTable
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.shouldRespondHtmx
import com.github.clasicrando.web.userOrRedirect
import com.github.clasicrando.web.userWithRoleOrRespond
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.util.getOrFail
import kotlinx.html.TagConsumer
import kotlinx.html.p

suspend inline fun ApplicationCall.respondMaybeHtmxPage(
    user: User? = null,
    stylesheetHref: String? = null,
    pageTitle: String? = null,
    crossinline html: TagConsumer<*>.() -> Unit,
) {
    if (this.shouldRespondHtmx) {
        this.respondHtmx {
            pushCurrentUrl(this@respondMaybeHtmxPage.request)
            addHtml(html)
        }
        return
    }
    this.respondHtml {
        BasePage(
            user = user,
            stylesheetHref = stylesheetHref,
            pageTitle = pageTitle,
            innerContent = html,
        )
    }
}

fun Route.unauthenticatedPages() {
    get("/login") {
        val userSession = call.sessions.get<UserSession>()
        if (userSession != null) {
            call.respondRedirect("/")
            return@get
        }
        call.respondMaybeHtmxPage(pageTitle = "Login") {
            LoginForm()
        }
    }
    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondRedirect(url = "/login")
    }
}

fun Route.authenticatedPages() {
    index()
    dataSources()
    dataSource()
    workflows()
    tasks()
    pipelineRuns()
    pipelineRun()
    adminDashboard()
}

private fun Route.index() =
    get("/") {
        val user = userOrRedirect() ?: return@get
        call.respondMaybeHtmxPage(user = user, pageTitle = "Home") {
            p { +"Welcome to EnviroManager" }
        }
    }

private fun Route.dataSources() =
    get("/data-sources") {
        val user = userOrRedirect() ?: return@get
        call.respondMaybeHtmxPage(user = user, pageTitle = "Data Sources") {
            DataSourcesTable(user)
        }
    }

private fun Route.dataSource() =
    get("/data-sources/{dsId}") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val user = userOrRedirect() ?: return@get
        call.respondMaybeHtmxPage(user = user, pageTitle = "Data Source") {
            DataSourceView(dsId = dsId, user = user)
        }
    }

private fun Route.workflows() =
    get("/workflows") {
        val user = userWithRoleOrRespond(Role.Developer) ?: return@get
        call.respondMaybeHtmxPage(user = user, pageTitle = "Workflows") {
            WorkflowsTable()
        }
    }

private fun Route.tasks() =
    get("/tasks") {
        val user = userWithRoleOrRespond(Role.Developer) ?: return@get
        call.respondMaybeHtmxPage(user = user, pageTitle = "Workflows") {
            TasksTable()
        }
    }

private val pipelineRunRoles =
    arrayOf(
        Role.PipelineCollection,
        Role.PipelineLoad,
        Role.PipelineCheck,
        Role.PipelineQA,
    )

private fun Route.pipelineRuns() =
    get("/pipeline-runs") {
        val user = userWithRoleOrRespond(roles = pipelineRunRoles)
        call.respondMaybeHtmxPage(user = user, pageTitle = "Pipeline Runs") {
            PipelineRunsTable()
        }
    }

private fun Route.pipelineRun() =
    get("/pipeline-runs/{pipelineRunId}") {
        val pipelineRunId = call.parameters.getOrFail<Long>("pipelineRunId").toRunId()
        val user = userWithRoleOrRespond(roles = pipelineRunRoles)
        call.respondMaybeHtmxPage(user = user, pageTitle = "Pipeline Run") {
            PipelineRunView(pipelineRunId)
        }
    }

private fun Route.adminDashboard() =
    get("/admin-dashboard") {
        val user = adminUserOrRespondMaybeHtmxError() ?: return@get
        call.respondMaybeHtmxPage(user = user, pageTitle = "Admin Dashboard") {
            AdminDashboard()
        }
    }
