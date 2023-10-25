package com.github.clasicrando.models

import org.snappy.decode.Decoder
import org.snappy.encode.Encode
import org.snappy.rowparse.SnappyRow
import java.sql.PreparedStatement

enum class Role(val dbValue: String, val description: String) : Encode {
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

    override fun encode(
        preparedStatement: PreparedStatement,
        parameterIndex: Int,
    ) {
        preparedStatement.setString(parameterIndex, this.dbValue)
    }

    companion object : Decoder<Role> {
        override fun decodeNullable(
            row: SnappyRow,
            fieldName: String,
        ): Role? {
            val roleName = row.getStringNullable(fieldName) ?: return null
            return Role.entries.firstOrNull { it.dbValue == roleName }
        }
    }
}
