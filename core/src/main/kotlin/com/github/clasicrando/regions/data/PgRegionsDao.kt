package com.github.clasicrando.regions.data

import com.github.clasicrando.regions.model.Country
import com.github.clasicrando.regions.model.Province
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgRegionsDao(
    override val di: DI,
) : DIAware,
    RegionsDao {
    val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getCountryCodes(): List<Country> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    SELECT c.country_code, c.name
                    FROM em.countries c
                    """.trimIndent(),
                ).fetchAll(Country)
        }

    override suspend fun getProvinceCodes(countryCode: String): List<Province> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    SELECT p.prov_code, p.country_code, p.name
                    FROM em.provinces p
                    WHERE p.country_code = $1
                    """.trimIndent(),
                ).bind(countryCode)
                .fetchAll(Province)
        }
}
