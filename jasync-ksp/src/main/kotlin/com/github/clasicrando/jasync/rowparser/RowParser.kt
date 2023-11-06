package com.github.clasicrando.jasync.rowparser

import com.github.jasync.sql.db.RowData

interface RowParser<out T> {
    fun parseRow(row: RowData): T
}
