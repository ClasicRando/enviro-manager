package com.github.clasicrando.web.page

import com.github.clasicrando.users.model.User
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.component.dataDisplay
import kotlinx.html.FlowContent

class DataSource(
    user: User,
    private val dsId: Long,
) : BasePage(user = user, pageTitle = "Data Source") {
    override fun FlowContent.innerContent() {
        dataDisplay(
            displayId = dsId.toString(),
            title = "Data Source Details",
            dataUrl = apiV1Url("/data-sources/$dsId"),
        )
    }
}
