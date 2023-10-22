package com.github.clasicrando.web.page

import com.github.clasicrando.models.User
import com.github.clasicrando.web.component.dataTable
import kotlinx.html.th
import kotlinx.html.tr

class Home(user: User) : Base(user) {
    init {
        innerContent {
            dataTable(
                id = "dataSources",
                caption = "Data Sources",
                dataSource = "/api/data-sources",
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
                }
            }
        }
    }
}
