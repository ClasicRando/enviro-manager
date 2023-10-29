package com.github.clasicrando.web.component

import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.web.api.DATA_SOURCE_API_BASE_URL
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.column
import com.github.clasicrando.web.element.row
import kotlinx.html.TagConsumer
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.textArea

private const val NAME_FIELD = "name"
private const val EMAIL_FIELD = "email"
private const val WEBSITE_FIELD = "website"
private const val TYPE_FIELD = "type"
private const val NOTES_FIELD = "notes"

fun <T, C : TagConsumer<T>> C.createDataSourceContactForm(dsId: DsId) {
    createForm(
        title = "Create New Data Source Contact",
        postUrl = apiV1Url("$DATA_SOURCE_API_BASE_URL/$dsId/contacts"),
        cancelUrl = apiV1Url("$DATA_SOURCE_API_BASE_URL/$dsId"),
    ) {
        row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = NAME_FIELD
                +"Name"
            }
            column(size = 9) {
                input(classes = "form-control") {
                    id = NAME_FIELD
                    name = NAME_FIELD
                }
            }
        }
        row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = EMAIL_FIELD
                +"Email"
            }
            column(size = 9) {
                input(classes = "form-control") {
                    id = EMAIL_FIELD
                    name = EMAIL_FIELD
                }
            }
        }
        row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = WEBSITE_FIELD
                +"Website"
            }
            column(size = 9) {
                input(classes = "form-control") {
                    id = WEBSITE_FIELD
                    name = WEBSITE_FIELD
                }
            }
        }
        row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = TYPE_FIELD
                +"Type"
            }
            column(size = 9) {
                input(classes = "form-control") {
                    id = TYPE_FIELD
                    name = TYPE_FIELD
                }
            }
        }
        row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = NOTES_FIELD
                +"Notes"
            }
            column(size = 9) {
                textArea(classes = "form-control") {
                    id = NOTES_FIELD
                    name = NOTES_FIELD
                }
            }
        }
    }
}
