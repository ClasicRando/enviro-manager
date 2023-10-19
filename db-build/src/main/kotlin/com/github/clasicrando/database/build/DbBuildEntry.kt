package com.github.clasicrando.database.build

import kotlinx.serialization.Serializable

@Serializable
data class DbBuildEntry(val name: String, val dependencies: List<String>) {
    fun dependenciesMet(completed: Set<String>): Boolean {
        return dependencies.isEmpty() || dependencies.all { completed.contains(it) }
    }

    fun dependenciesNotMet(completed: Set<String>): String {
        return dependencies.filter { !completed.contains(it) }.joinToString { it }
    }
}
