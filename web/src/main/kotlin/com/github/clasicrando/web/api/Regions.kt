package com.github.clasicrando.web.api

import com.github.clasicrando.web.component.SimpleOption
import com.github.clasicrando.web.htmx.respondHtmx
import com.github.clasicrando.web.regionsDao
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.regions() {
    route("/regions") {
        countries()
        provinces()
    }
}

private fun Route.countries() =
    get("/countries") {
        val countries = regionsDao.getCountryCodes()

        call.respondHtmx {
            addTrigger("refresh-provinces")
            addHtml {
                for ((i, country) in countries.withIndex()) {
                    SimpleOption(
                        value = country.countryCode,
                        text = country.name,
                        selected = i == 0,
                    )
                }
            }
        }
    }

private fun Route.provinces() =
    get("/provinces") {
        val regionsDao = regionsDao
        val countryCode =
            call.parameters["country"]
                ?.takeIf { it.isNotBlank() }
                ?: regionsDao.getCountryCodes().first().countryCode
        val provinces = regionsDao.getProvinceCodes(countryCode)

        call.respondHtmx {
            addHtml {
                SimpleOption(value = "", selected = true)
                for (province in provinces) {
                    SimpleOption(value = province.provCode, text = province.name)
                }
            }
        }
    }
