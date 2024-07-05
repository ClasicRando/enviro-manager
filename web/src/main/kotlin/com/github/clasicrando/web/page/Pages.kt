package com.github.clasicrando.web.page

import com.github.clasicrando.datasources.model.toDsId
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.web.UserSession
import com.github.clasicrando.web.adminUserOrRespondHtmxError
import com.github.clasicrando.web.component.AdminDashboard
import com.github.clasicrando.web.component.BasePage
import com.github.clasicrando.web.component.CreateDataSourceContactForm
import com.github.clasicrando.web.component.DataSourceTableRefresh
import com.github.clasicrando.web.component.DataSourceView
import com.github.clasicrando.web.component.loginForm
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.isBoost
import com.github.clasicrando.web.isHtmx
import com.github.clasicrando.web.userOrRedirect
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
import kotlinx.html.p
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

val ApplicationCall.shouldRespondHtmx: Boolean get() = request.isHtmx && !request.isBoost

fun Route.unauthenticatedPages() {
    get("/login") {
        val userSession = call.sessions.get<UserSession>()
        if (userSession != null) {
            call.respondRedirect("/")
            return@get
        }
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushCurrentUrl(call.request)
                addHtml {
                    loginForm()
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(pageTitle = "Login") {
                loginForm()
            }
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
    createDataSourceContact()
    adminDashboard()
}

private fun Route.index() =
    get("/") {
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushCurrentUrl(call.request)
                addHtml {
                    p { +"Welcome to EnviroManager" }
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(user = user, pageTitle = "Home") {
                p { +"Welcome to EnviroManager" }
            }
        }
    }

private fun Route.dataSources() =
    get("/data-sources") {
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushCurrentUrl(call.request)
                addHtml {
                    DataSourceTableRefresh()
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(user = user, pageTitle = "Data Sources") {
                DataSourceTableRefresh()
            }
        }
    }

private fun Route.dataSource() =
    get("/data-sources/{dsId}") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushCurrentUrl(call.request)
                addHtml {
                    DataSourceView(dsId)
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(user = user, pageTitle = "Data Source") {
                DataSourceView(dsId)
            }
        }
    }

private fun Route.createDataSourceContact() =
    get("/data-sources/{dsId}/contacts/create") {
        val dsId = call.parameters.getOrFail<Long>("dsId").toDsId()
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushCurrentUrl(call.request)
                addHtml {
                    CreateDataSourceContactForm(dsId)
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(user = user, pageTitle = "Create Contact") {
                CreateDataSourceContactForm(dsId)
            }
        }
    }

private fun Route.adminDashboard() =
    get("/admin-dashboard") {
        val usersDao: UsersDao by closestDI().instance()
        val user = call.adminUserOrRespondHtmxError(dao = usersDao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushCurrentUrl(call.request)
                addHtml {
                    AdminDashboard()
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(user = user, pageTitle = "Admin Dashboard") {
                AdminDashboard()
            }
        }
    }
