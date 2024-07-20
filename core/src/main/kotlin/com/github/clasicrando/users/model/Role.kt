package com.github.clasicrando.users.model

import kotlinx.serialization.Serializable

private const val ADMIN = "admin"
private const val DEVELOPER = "developer"
private const val CREATE_DATA_SOURCE = "create-data-source"
private const val EDIT_DATA_SOURCE = "edit-data-source"
private const val CREATE_PIPELINE_RUN = "create-pipeline-run"
private const val PIPELINE_COLLECTION = "pipeline-collection"
private const val PIPELINE_LOAD = "pipeline-load"
private const val PIPELINE_CHECK = "pipeline-check"
private const val PIPELINE_QA = "pipeline-qa"

@Serializable
sealed class Role(
    val display: String,
    val dbValue: String,
    val description: String,
    private val inheritedRoleValues: List<String> = emptyList(),
) {
    data object Admin : Role(
        display = "Admin",
        dbValue = ADMIN,
        description = "All privileges granted",
        inheritedRoleValues = listOf(
            DEVELOPER,
            CREATE_DATA_SOURCE,
            EDIT_DATA_SOURCE,
            CREATE_PIPELINE_RUN,
            PIPELINE_COLLECTION,
            PIPELINE_LOAD,
            PIPELINE_CHECK,
            PIPELINE_QA,
        ),
    )

    data object Developer : Role(
        display = "Developer",
        dbValue = DEVELOPER,
        description = "Application developer with the extended ability to create workflows",
    )

    data object CreateDataSource : Role(
        display = "Create Data Source",
        dbValue = CREATE_DATA_SOURCE,
        description = "Enables a user to create a new data source",
        inheritedRoleValues = listOf(EDIT_DATA_SOURCE),
    )

    data object EditDataSource : Role(
        display = "Edit Data Source",
        dbValue = EDIT_DATA_SOURCE,
        description = "Enables a user to edit existing data sources",
    )

    data object CreatePipelineRun : Role(
        display = "Create Pipeline Run",
        dbValue = CREATE_PIPELINE_RUN,
        description = "Enables a user to create a new data load pipeline run",
    )

    data object PipelineCollection : Role(
        display = "Pipeline Collection",
        dbValue = PIPELINE_COLLECTION,
        description = "Enables a user to collect data for and modify existing data sources",
    )

    data object PipelineLoad : Role(
        display = "Pipeline Load",
        dbValue = PIPELINE_LOAD,
        description = "Enables a user to execute a load of a data pipeline",
    )

    data object PipelineCheck : Role(
        display = "Pipeline Check",
        dbValue = PIPELINE_CHECK,
        description = "Enables a user to execute a check of data pipeline loads",
    )

    data object PipelineQA : Role(
        display = "Pipeline QA",
        dbValue = PIPELINE_QA,
        description = "Enables a user to execute a quality assurance check on data pipeline loads",
    )

    val inheritedRoles: List<Role> by lazy {
        inheritedRoleValues.map { fromString(it) }
    }

    companion object {
        fun fromString(dbValue: String): Role =
            when (dbValue) {
                ADMIN -> Admin
                DEVELOPER -> Developer
                CREATE_DATA_SOURCE -> CreateDataSource
                EDIT_DATA_SOURCE -> EditDataSource
                CREATE_PIPELINE_RUN -> CreatePipelineRun
                PIPELINE_COLLECTION -> PipelineCollection
                PIPELINE_LOAD -> PipelineLoad
                PIPELINE_CHECK -> PipelineCheck
                PIPELINE_QA -> PipelineQA
                else -> error("Could not find a role for value = '$dbValue'")
            }

        val all by lazy {
            sequenceOf(
                DEVELOPER,
                CREATE_DATA_SOURCE,
                EDIT_DATA_SOURCE,
                CREATE_PIPELINE_RUN,
                PIPELINE_COLLECTION,
                PIPELINE_LOAD,
                PIPELINE_CHECK,
                PIPELINE_QA,
            ).map { fromString(it) }
                .toList()
        }
    }
}
