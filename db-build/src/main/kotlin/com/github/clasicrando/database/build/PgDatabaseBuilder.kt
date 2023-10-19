package com.github.clasicrando.database.build

import com.github.clasicrando.logging.logger
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.serialization.json.Json
import org.snappy.command.sqlCommand
import java.nio.file.Path
import java.sql.Connection
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.notExists
import kotlin.io.path.readText

class PgDatabaseBuilder(private val connection: Connection) : DatabaseBuilder {
    private val log: KLogger by logger()
    private suspend fun refreshDatabase() {
        val query = """
            select string_agg(schema_name,', ')
            from information_schema.schemata
            where schema_owner = current_user
        """.trimIndent()
        val schemaNames = sqlCommand(query).queryScalarOrNullSuspend<String>(connection)
            ?: return
        sqlCommand("drop schema if exists $schemaNames cascade").executeSuspend(connection)
    }

    private fun processTypeDefinition(block: String): String {
        val newBlock = typeRegex.replace(
            block,
            """
                if not exists (
                    select 1
                    from pg_namespace n
                    join pg_type t on n.oid = t.typnamespace
                    where
                        n.nspname = '$1'
                        and t.typname = '$2'
                ) then
                    create type $1.$2 as $3;
                end if;
            """.trimIndent(),
        )
        return "do \$body\$\n$newBlock\n\$body\$;"
    }

    private fun formatAnonymousBlock(block: String): String {
        val firstWord = block.split(Regex("\\s+"))
            .firstOrNull()
            ?: error("Cannot format empty block")
        return when (firstWord) {
            "do" -> block
            "begin", "declare" -> "do \$body\$\n$block\n\$body\$;"
            else -> if (typeRegex.matches(block)) {
                processTypeDefinition(block)
            } else {
                "do \$body\$\nbegin\n$block\nend;\n\$body\$;"
            }
        }
    }

    private suspend fun executeAnonymousBlock(block: String) {
        val query = formatAnonymousBlock(block)
        sqlCommand(query).executeSuspend(connection)
    }

    override suspend fun buildDatabase() {
        val databaseTarget = sqlCommand("select current_database()")
            .queryScalarSuspend<String>(connection)
        log.atInfo {
            message = "Target specified as $databaseTarget to rebuild"
        }

        val databaseRefresh = System.getenv("EM_DB_REFRESH") == "true"
        if (databaseRefresh) {
            log.atInfo {
                message = "Refresh specified for $databaseTarget database"
            }
            try {
                refreshDatabase()
            } catch (ex: Throwable) {
                log.atError {
                    message = "Error during database refresh"
                    cause = ex
                }
                return
            }
        }

        val databaseDirectory = Path(System.getenv("EM_DB_BUILD_DIR"))
        if (databaseDirectory.notExists()) {
            log.atError {
                message = "Could not find database build directory"
                payload = mapOf("directory" to databaseDirectory)
            }
            return
        }

        val jsonFile = databaseDirectory.resolve("build.json")
            .bufferedReader()
            .readText()
        val dbBuild = Json.decodeFromString<DbBuild>(jsonFile)
        try {
            dbBuild.run(databaseDirectory, this)
        } catch (ex: Throwable) {
            log.atError {
                message = "Error running database build"
                cause = ex
            }
            return
        }


    }

    override suspend fun runDbBuildEntry(directory: Path, dbBuildEntry: DbBuildEntry) {
        val fileName = dbBuildEntry.name
            .takeIf { it.endsWith(".pgsql") }
            ?: "${dbBuildEntry.name}.pgsql"
        val path = directory.resolve(fileName)
        val block = path.readText()
        try {
            executeAnonymousBlock(block)
        } catch (ex: Throwable) {
            log.atError {
                message = "Error running file"
                payload = mapOf("entry_name" to dbBuildEntry.name)
            }
            throw ex
        }
    }

    companion object {
        private val typeRegex = Regex("^create\\s+type\\s+([^.]+)\\.([^.]+)\\s+as([^;]+);")
    }
}
