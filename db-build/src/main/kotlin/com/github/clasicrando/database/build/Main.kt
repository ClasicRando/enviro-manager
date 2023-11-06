package com.github.clasicrando.database.build

import com.github.clasicrando.di.bindDatabaseComponents
import com.github.jasync.sql.db.Connection
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.instance

val logger: KLogger = KotlinLogging.logger {}

val di =
    DI {
        bindDatabaseComponents()
    }

fun main() =
    runBlocking {
        val connection: Connection by di.instance()
        try {
            val builder = PgDatabaseBuilder(connection)
            builder.buildDatabase()
            logger.atInfo {
                message = "Finished running database build"
            }
        } catch (ex: Throwable) {
            logger.atError {
                message = "Expected error building database from source"
                cause = ex
            }
        } finally {
            try {
                connection.disconnect().await()
            } catch (_: Throwable) {
            }
        }
    }
