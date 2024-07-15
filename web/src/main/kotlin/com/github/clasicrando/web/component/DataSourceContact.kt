package com.github.clasicrando.web.component

import com.github.clasicrando.datasources.model.DataSourceContact
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.web.NO_DISPLAY_ELEMENT_TARGET
import com.github.clasicrando.web.api.apiV1Url
import com.github.clasicrando.web.element.Column
import com.github.clasicrando.web.element.Row
import io.ktor.http.HttpMethod
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.tr
import kotlinx.serialization.json.JsonPrimitive

private const val NAME_FIELD = "name"
private const val EMAIL_FIELD = "email"
private const val WEBSITE_FIELD = "website"
private const val TYPE_FIELD = "type"
private const val NOTES_FIELD = "notes"

@Component
fun TBODY.DataSourceContact(contact: DataSourceContact) {
    tr {
        DataCell(contact.contactId)
        DataCell(contact.name)
        DataCell(contact.email)
        DataCell(contact.website)
        DataCell(contact.type)
        DataCell(contact.notes)
        td {
            RowAction(
                title = "Edit",
                url = apiV1Url("/data-sources/${contact.dsId}/contacts/${contact.contactId}/edit"),
                icon = "fa-edit",
                httpMethod = HttpMethod.Get,
                target = ADD_MODAL_TARGET,
            )
            RowAction(
                title = "Delete",
                url = apiV1Url("/data-sources/${contact.dsId}/contacts/${contact.contactId}"),
                icon = "fa-trash",
                httpMethod = HttpMethod.Delete,
                confirmMessage =
                    "Are you sure you want to delete this data source contact?",
                target = NO_DISPLAY_ELEMENT_TARGET,
            )
        }
    }
}

@Component
fun <T, C : TagConsumer<T>> C.CreateOrUpdateDataSourceContactModal(
    dsId: DsId,
    contact: DataSourceContact?,
) {
    val extraValues =
        contact?.contactId?.let {
            mapOf("contactId" to JsonPrimitive(it.value))
        } ?: mapOf()
    CreateOrUpdateModal(
        id = "createOrUpdateDataSourceContact",
        title = "${if (contact == null) "Create New" else "Update"} Data Source Contact",
        putUrl = apiV1Url("/data-sources/$dsId/contacts"),
        extraValues = extraValues,
    ) {
        Row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = NAME_FIELD
                +"Name"
            }
            Column(size = 9) {
                input(classes = "form-control") {
                    id = NAME_FIELD
                    name = NAME_FIELD
                    contact?.name?.let { value = it }
                }
            }
        }
        Row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = EMAIL_FIELD
                +"Email"
            }
            Column(size = 9) {
                input(classes = "form-control") {
                    id = EMAIL_FIELD
                    name = EMAIL_FIELD
                    contact?.email?.let { value = it }
                }
            }
        }
        Row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = WEBSITE_FIELD
                +"Website"
            }
            Column(size = 9) {
                input(classes = "form-control") {
                    id = WEBSITE_FIELD
                    name = WEBSITE_FIELD
                    contact?.website?.let { value = it }
                }
            }
        }
        Row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = TYPE_FIELD
                +"Type"
            }
            Column(size = 9) {
                input(classes = "form-control") {
                    id = TYPE_FIELD
                    name = TYPE_FIELD
                    contact?.type?.let { value = it }
                }
            }
        }
        Row(classes = "mb-3") {
            label(classes = "col-sm-3 col-form-label") {
                htmlFor = NOTES_FIELD
                +"Notes"
            }
            Column(size = 9) {
                textArea(classes = "form-control") {
                    id = NOTES_FIELD
                    name = NOTES_FIELD
                    contact?.notes?.let { text(it) }
                }
            }
        }
    }
}
