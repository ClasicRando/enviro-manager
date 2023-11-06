package com.github.clasicrando.users.model

enum class Role(val dbValue: String, val description: String) {
    Admin(dbValue = "admin", description = "All privileges granted"),
    CreateDataSource(
        dbValue = "create-data-source",
        description = "Enables a user to create a new data source",
    ),
    CreatePipelineRun(
        dbValue = "create-pipeline-run",
        description = "Enables a user to create a new data load pipeline run",
    ),
    PipelineCheck(
        dbValue = "pipeline-check",
        description = "Enables a user to execute a check of data pipeline loads",
    ),
    PipelineCollection(
        dbValue = "pipeline-collection",
        description = "Enables a user to collect data for and modify existing data sources",
    ),
    PipelineLoad(
        dbValue = "pipeline-load",
        description = "Enables a user to execute a load of a data pipeline",
    ),
    PipelineQA(
        dbValue = "pipeline-qa",
        description = "Enables a user to execute a quality assurance check on data pipeline loads",
    ), ;

    override fun toString(): String {
        return dbValue
    }

    companion object {
        fun fromString(value: String): Role {
            return Role.entries.firstOrNull { it.dbValue == value }
                ?: error("Could not find a role for value = '$value'")
        }
    }
}
