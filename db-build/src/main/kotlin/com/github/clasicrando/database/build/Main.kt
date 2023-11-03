package com.github.clasicrando.database.build

import com.github.clasicrando.di.bindDatabaseComponents
import com.github.clasicrando.logging.logger
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import java.sql.Connection
import javax.sql.DataSource

val log: KLogger by logger()

val di =
    DI {
        bindDatabaseComponents()
        bindProvider<Connection> {
            val dataSource: DataSource by di.instance()
            dataSource.connection
        }
    }

fun main() =
    runBlocking {
        val connection: Connection by di.instance()
        try {
            val builder = PgDatabaseBuilder(connection)
            builder.buildDatabase()
            log.atInfo {
                message = "Finished running database build"
            }
        } catch (ex: Throwable) {
            log.atError {
                message = "Expected error building database from source"
                cause = ex
            }
        } finally {
            try {
                connection.close()
            } catch (_: Throwable) {
            }
        }
    }
