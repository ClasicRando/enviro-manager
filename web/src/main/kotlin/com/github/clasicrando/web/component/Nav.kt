package com.github.clasicrando.web.component

import com.github.clasicrando.models.User
import com.github.clasicrando.web.htmx.hxBoost
import kotlinx.html.*

private fun UL.themeSelector() {
    li(classes = "nav-item dropdown") {
        button(
            classes = "btn btn-link nav-link py-2 px-0 px-lg-2 dropdown-toggle align-items-center",
            type = ButtonType.button,
        ) {
            id = "bd-theme"
            attributes["aria-expanded"] = "false"
            attributes["data-bs-toggle"] = "dropdown"
            attributes["data-bs-display"] = "static"
            attributes["aria-label"] = "Toggle theme (dark)"
            i(classes = "fa-solid fa-moon me-1") {
                id = "theme-selector"
            }
            span(classes = "d-lg-none ms-2") {
                id = "bd-theme-text"
                +"Toggle Theme"
            }
        }
        ul(classes = "dropdown-menu dropdown-menu-end") {
            attributes["aria-lablledby"] = "bd-theme-text"
            li {
                button(
                    classes = "dropdown-item d-flex align-items-center",
                    type = ButtonType.button,
                ) {
                    attributes["aria-pressed"] = "false"
                    attributes["data-bs-theme-value"] = "light"
                    i(classes = "fa-solid fa-sun me-1")
                    +"Light"
                }
            }
            li {
                button(
                    classes = "dropdown-item d-flex align-items-center active",
                    type = ButtonType.button,
                ) {
                    attributes["aria-pressed"] = "true"
                    attributes["data-bs-theme-value"] = "dark"
                    i(classes = "fa-solid fa-moon me-1")
                    +"Dark"
                }
            }
            li {
                button(
                    classes = "dropdown-item d-flex align-items-center active",
                    type = ButtonType.button,
                ) {
                    attributes["aria-pressed"] = "false"
                    attributes["data-bs-theme-value"] = "auto"
                    i(classes = "fa-solid fa-circle-half-stroke me-1")
                    +"Auto"
                }
            }
        }
    }
}

private fun UL.userContext(userFullName: String?) {
    if (userFullName == null) {
        li(classes = "nav-item") {
            a(classes = "nav-link disabled", href = "#") {
                +"Login"
            }
        }
        return
    }
    li(classes = "nav-link dropdown") {
        a(classes = "nav-link dropdown-toggle", href = "#") {
            role = "button"
            attributes["aria-expanded"] = "false"
            +userFullName
        }
        ul(classes = "dropdown-menu") {
            li {
               a(classes = "dropdown-item", href = "/logout") {
                   hxBoost = true
                   +"Logout"
               }
            }
        }
    }
}

@HtmlTagMarker
fun DIV.mainNav(user: User? = null) {
    nav(classes = "navbar navbar-expand-lg bg-body-tertiary") {
        id = "mainNavBar"
        div(classes = "container-fluid") {
            a(classes = "navbar-brand", href = "#") {
                img(
                    src = "/assets/favicon.ico",
                    alt = "logo",
                    classes = "d-inline-block align-text-top me-2",
                ) {
                    height = "30"
                    +"EnviroManager"
                }
            }
            ul(classes = "navbar-nav me-auto my-2 my-lg-0 navbar-nav-scroll") {
                style = "--bs-scroll-height: 100px"
                li(classes = "nav-item") {
                    a(classes = "nav-link", href = "/") {
                        hxBoost = true
                        +"Home"
                    }
                }
            }
            ul(classes = "navbar-nav ms-auto my-2 my-lg-0 navbar-nav-scroll") {
                style = "--bs-scroll-height: 100px;"
                userContext(userFullName =  user?.fullName)
                themeSelector()
            }
        }
    }
}
