package com.github.clasicrando.regions.model

import io.github.clasicrando.kdbc.core.query.RowParser
import io.github.clasicrando.kdbc.core.result.DataRow
import io.github.clasicrando.kdbc.core.result.getAsNonNull
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val countryCode: String,
    val name: String,
) {
    companion object : RowParser<Country> {
        override fun fromRow(row: DataRow): Country =
            Country(
                countryCode = row.getAsNonNull("country_code"),
                name = row.getAsNonNull("name"),
            )
    }
}
