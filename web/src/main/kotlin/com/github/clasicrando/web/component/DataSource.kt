package com.github.clasicrando.web.component

import com.github.clasicrando.models.DataSource
import com.github.clasicrando.models.DataSourceWithContacts
import com.github.clasicrando.web.element.row
import com.github.clasicrando.web.htmx.HtmxContentCollector
import io.ktor.http.HttpMethod
import kotlinx.html.fieldSet
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr

fun HtmxContentCollector.dataSourceRow(dataSource: DataSource) {
    tr {
        dataCell(dataSource.dsId)
        dataCell(dataSource.code)
        dataCell(dataSource.province)
        dataCell(dataSource.country)
        td {
            i(classes = "fa-solid ${if (dataSource.provLevel) "fa-check" else "fa-x"}")
        }
        dataCell(dataSource.reportingType)
        dataCell(dataSource.assignedUser)
        dataCell(dataSource.createdBy)
        dataCell(dataSource.created)
        dataCell(dataSource.updatedBy)
        dataCell(dataSource.lastUpdated)
        td {
            rowAction(
                title = "View Data Source",
                url = "/data-source/${dataSource.dsId}",
                icon = "fa-right-to-bracket",
                httpMethod = HttpMethod.Get,
            )
        }
    }
}

fun HtmxContentCollector.dataSourceDisplay(data: DataSourceWithContacts) {
    fieldSet {
        row {
            dataField(fieldId = "dsId", label = "ID", columnWidth = 2, data = data.dataSource.dsId)
        }
    }
    dataDisplayTable(
        caption = "Contacts",
        header = {
            tr {
                th { +"Contact ID" }
                th { +"Name" }
                th { +"Email" }
                th { +"Website" }
                th { +"Type" }
                th { +"Notes" }
            }
        },
        items = data.contacts,
        rowBuilder = { contact ->
            tr {
                dataCell(contact.contactId)
                dataCell(contact.name)
                dataCell(contact.email)
                dataCell(contact.website)
                dataCell(contact.type)
                dataCell(contact.notes)
            }
        },
    )
}
