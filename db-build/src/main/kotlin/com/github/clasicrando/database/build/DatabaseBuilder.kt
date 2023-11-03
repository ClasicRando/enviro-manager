package com.github.clasicrando.database.build

import java.nio.file.Path

interface DatabaseBuilder {
    suspend fun buildDatabase()

    suspend fun runDbBuildEntry(
        directory: Path,
        dbBuildEntry: DbBuildEntry,
    )
}
