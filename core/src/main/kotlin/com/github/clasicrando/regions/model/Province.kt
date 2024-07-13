package com.github.clasicrando.regions.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.serialization.Serializable

@Serializable
data class Province(
    val provCode: String,
    val countryCode: String,
    val name: String,
) {
    companion object : RowParser<Province> {
        override fun fromRow(row: DataRow): Province =
            Province(
                provCode = row.getAsNonNull("prov_code"),
                countryCode = row.getAsNonNull("country_code"),
                name = row.getAsNonNull("name"),
            )
    }
}
