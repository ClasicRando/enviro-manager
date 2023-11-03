package com.github.clasicrando.database.build

import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class DbBuild(val entries: MutableList<DbBuildEntry>) {
    suspend fun run(
        directory: Path,
        builder: DatabaseBuilder,
    ) {
        for (entry in orderedEntries()) {
            builder.runDbBuildEntry(directory = directory, dbBuildEntry = entry)
        }
    }

    private fun orderedEntries(): List<DbBuildEntry> =
        buildList {
            val completed = mutableSetOf<String>()

            if (entries.isEmpty()) {
                return@buildList
            }
            while (true) {
                val schemaEntry =
                    entries.withIndex()
                        .firstOrNull { (_, entry) -> entry.name.startsWith("schemas/") }
                        ?: break
                val entry = entries.removeAt(schemaEntry.index)
                completed.add(entry.name)
                add(entry)
            }
            while (true) {
                val findOutput =
                    entries.withIndex()
                        .firstOrNull { (_, entry) -> entry.dependenciesMet(completed) }
                        ?: if (entries.isEmpty()) {
                            break
                        } else {
                            val unresolved =
                                entries.joinToString {
                                    "${it.name} (missing ${it.dependenciesNotMet(completed)})"
                                }
                            error(
                                """
                                Found unresolved dependency in build.json file.
                                Unresolved
                                $unresolved
                                """.trimIndent(),
                            )
                        }
                val entry = entries.removeAt(findOutput.index)
                completed.add(entry.name)
                add(entry)
            }
        }
}
