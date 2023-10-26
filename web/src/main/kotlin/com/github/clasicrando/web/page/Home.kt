package com.github.clasicrando.web.page

import com.github.clasicrando.models.User
import kotlinx.html.FlowContent
import kotlinx.html.p

class Home(user: User) : BasePage(user = user, pageTitle = "Data Sources") {
    override fun FlowContent.innerContent() {
        p { +"Welcome to EnviroManager" }
    }
}
