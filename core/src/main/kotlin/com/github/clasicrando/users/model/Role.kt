package com.github.clasicrando.users.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Role(
    val display: String,
    val dbValue: String,
    val description: String,
    val inheritedRoles: List<Role> = emptyList(),
) {
    data object Admin : Role(
        display = "Admin",
        dbValue = "admin",
        description = "All privileges granted",
    )

    data object Developer : Role(
        display = "Developer",
        dbValue = "developer",
        description = "Application developer with the extended ability to create workflows",
    )

    data object CreateDataSource : Role(
        display = "Create Data Source",
        dbValue = "create-data-source",
        description = "Enables a user to create a new data source",
        inheritedRoles = listOf(EditDataSource),
    )

    data object EditDataSource : Role(
        display = "Edit Data Source",
        dbValue = "edit-data-source",
        description = "Enables a user to edit existing data sources",
    )

    data object CreatePipelineRun : Role(
        display = "Create Pipeline Run",
        dbValue = "create-pipeline-run",
        description = "Enables a user to create a new data load pipeline run",
    )

    data object PipelineCollection : Role(
        display = "Pipeline Collection",
        dbValue = "pipeline-collection",
        description = "Enables a user to collect data for and modify existing data sources",
    )

    data object PipelineLoad : Role(
        display = "Pipeline Load",
        dbValue = "pipeline-load",
        description = "Enables a user to execute a load of a data pipeline",
    )

    data object PipelineCheck : Role(
        display = "Pipeline Check",
        dbValue = "pipeline-check",
        description = "Enables a user to execute a check of data pipeline loads",
    )

    data object PipelineQA : Role(
        display = "Pipeline QA",
        dbValue = "pipeline-qa",
        description = "Enables a user to execute a quality assurance check on data pipeline loads",
    )

    companion object {
        fun fromString(dbValue: String): Role =
            when (dbValue) {
                Admin.dbValue -> Admin
                Developer.dbValue -> Developer
                CreateDataSource.dbValue -> CreateDataSource
                EditDataSource.dbValue -> EditDataSource
                CreatePipelineRun.dbValue -> CreatePipelineRun
                PipelineCollection.dbValue -> PipelineCollection
                PipelineLoad.dbValue -> PipelineLoad
                PipelineCheck.dbValue -> PipelineCheck
                PipelineLoad.dbValue -> PipelineLoad
                PipelineQA.dbValue -> PipelineQA
                else -> error("Could not find a role for value = '$dbValue'")
            }

        val all: Array<Role> =
            arrayOf(
                Admin,
                Developer,
                CreateDataSource,
                EditDataSource,
                CreatePipelineRun,
                PipelineCollection,
                PipelineLoad,
                PipelineCheck,
                PipelineQA,
            )
    }
}
