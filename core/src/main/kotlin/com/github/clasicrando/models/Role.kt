package com.github.clasicrando.models

enum class Role(val description: String) {
    Admin("All privileges granted"),
    CreateDataSource("Enables a user to create a new data source"),
    CreatePipelineRun("Enables a user to create a new data load pipeline run"),
    PipelineCheck("Enables a user to execute a check of data pipeline loads"),
    PipelineCollection("Enables a user to collect data for and modify existing data sources"),
    PipelineLoad("Enables a user to execute a load of a data pipeline"),
    PipelineQA("Enables a user to execute a quality assurance check on data pipeline loads"),
}
