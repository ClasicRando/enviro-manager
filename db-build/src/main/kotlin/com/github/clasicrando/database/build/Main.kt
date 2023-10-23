package com.github.clasicrando.database.build

import com.github.clasicrando.di.diContainer
import com.github.clasicrando.logging.logger
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.runBlocking
import org.kodein.di.instance
import java.sql.Connection

val log: KLogger by logger()

fun main() =
    runBlocking {
        val connection: Connection by diContainer.instance()
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
