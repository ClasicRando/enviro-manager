package com.github.clasicrando.web.page

import com.github.clasicrando.models.User
import kotlinx.html.HTML

fun HTML.home(user: User) {
    with(Base(user = user)) {
        innerContent {
            +"Hello, World!"
        }
        apply()
    }
}
