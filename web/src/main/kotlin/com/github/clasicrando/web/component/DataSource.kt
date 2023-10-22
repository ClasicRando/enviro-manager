package com.github.clasicrando.web.component

import com.github.clasicrando.models.DataSource
import com.github.clasicrando.web.htmx.HtmxContentCollector
import kotlinx.html.i
import kotlinx.html.td
import kotlinx.html.tr

fun HtmxContentCollector.dataSource(dataSource: DataSource) {
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
    }
}
