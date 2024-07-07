package com.github.clasicrando.database.build

import com.github.clasicrando.di.bindDatabaseComponents
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.kodein.di.DI
import org.kodein.di.instance

val logger: KLogger = KotlinLogging.logger {}

val di =
    DI {
        bindDatabaseComponents()
    }

suspend fun main() {
    val pool: PgAsyncConnectionPool by di.instance()
    pool.useConnection {
        try {
            val builder = PgDatabaseBuilder(it)
            builder.buildDatabase()
            logger.atInfo {
                message = "Finished running database build"
            }
        } catch (ex: Exception) {
            logger.atError {
                message = "Expected error building database from source"
                cause = ex
            }
        }
    }
}
