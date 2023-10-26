package com.github.clasicrando.web.page

import com.github.clasicrando.web.component.loginForm
import kotlinx.html.FlowContent

class LoginPage : BasePage(pageTitle = "Login") {
    override fun FlowContent.innerContent() {
        loginForm()
    }
}
