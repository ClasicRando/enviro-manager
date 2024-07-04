package com.github.clasicrando.web.page

import com.github.clasicrando.datasources.model.toDsId
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.web.adminUserOrRespondHtmxError
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.component.AdminDashboard
import com.github.clasicrando.web.component.BasePage
import com.github.clasicrando.web.component.DataSourceTableRefresh
import com.github.clasicrando.web.component.DataSourceView
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.isBoost
import com.github.clasicrando.web.isHtmx
import com.github.clasicrando.web.respondBasePage
import com.github.clasicrando.web.userOrRedirect
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.util.getOrFail
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

val ApplicationCall.shouldRespondHtmx: Boolean get() = request.isHtmx && !request.isBoost

fun Route.pages() {
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
        call.respondBasePage(
            contentUrl = apiV1Url("/home"),
            user = user,
        )
    }

private fun Route.dataSources() =
    get("/data-sources") {
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushUrl = "/data-sources"
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
                pushUrl = "/data-sources/$dsId"
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
        val dsId = call.parameters.getOrFail<Long>("dsId")
        val dao: UsersDao by closestDI().instance()
        val user = call.userOrRedirect(dao = dao) ?: return@get
        call.respondBasePage(
            contentUrl = apiV1Url("/data-sources/$dsId/contacts/create"),
            user = user,
        )
    }

private fun Route.adminDashboard() =
    get("/admin-dashboard") {
        val usersDao: UsersDao by closestDI().instance()
        val user = call.adminUserOrRespondHtmxError(dao = usersDao) ?: return@get
        if (call.shouldRespondHtmx) {
            call.respondHtmx {
                pushUrl = "/admin-dashboard"
                addHtml {
                    AdminDashboard()
                }
            }
            return@get
        }
        call.respondHtml {
            BasePage(user = user) {
                AdminDashboard()
            }
        }
    }
