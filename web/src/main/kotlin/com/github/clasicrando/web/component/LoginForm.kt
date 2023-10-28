package com.github.clasicrando.web.component

import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.htmx.HtmxContentCollector
import com.github.clasicrando.web.htmx.SwapType
import com.github.clasicrando.web.htmx.htmxJsonEncoding
import com.github.clasicrando.web.htmx.hxPost
import com.github.clasicrando.web.htmx.hxSwap
import com.github.clasicrando.web.htmx.hxTarget
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label

fun HtmxContentCollector.loginForm() {
    h3(classes = "login-form mx-auto") {
        +"Login to EnviroManager"
    }
    form(classes = "login-form mx-auto") {
        id = "loginForm"
        hxPost = apiV1Url("/users/login")
        hxTarget = "#errorMessage"
        hxSwap(SwapType.InnerHtml)
        htmxJsonEncoding = true
        div(classes = "form-group") {
            label {
                htmlFor = "username"
                +"Username"
            }
            input(classes = "form-control", type = InputType.text, name = "username") {
                id = "username"
                required = true
            }
        }
        div(classes = "form-group") {
            label {
                htmlFor = "password"
                +"Password"
            }
            input(classes = "form-control", type = InputType.password, name = "password") {
                id = "password"
                required = true
            }
        }
        div { id = "errorMessage" }
        input(classes = "btn btn-primary", type = InputType.submit) {
            value = "Login"
        }
    }
}
