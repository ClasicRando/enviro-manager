package com.github.clasicrando.web.page

import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.component.dataTable
import kotlinx.html.FlowContent
import kotlinx.html.th
import kotlinx.html.tr

class DataSources(user: User) : BasePage(user = user, pageTitle = "Data Sources") {
    override fun FlowContent.innerContent() {
        dataTable(
            id = "dataSources",
            caption = "Data Sources",
            dataSource = apiV1Url("data-sources"),
        ) {
            tr {
                th { +"Id" }
                th { +"Code" }
                th { +"Province" }
                th { +"Country" }
                th { +"Prov Level" }
                th { +"Reporting Type" }
                th { +"Assigned User" }
                th { +"Created By" }
                th { +"Created" }
                th { +"Updated By" }
                th { +"Last Updated" }
                th { +"Actions" }
            }
        }
    }
}
