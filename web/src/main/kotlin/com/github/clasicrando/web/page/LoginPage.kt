package com.github.clasicrando.web.page

import com.github.clasicrando.web.component.loginForm

class LoginPage : BasePage(pageTitle = "Login") {
    init {
        innerContent {
            loginForm()
        }
    }
}
