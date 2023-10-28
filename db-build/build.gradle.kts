plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

val postgresqlJdbcVersion: String by project

dependencies {
    implementation("com.github.snappy:snappy:0.1")
    implementation(project(":core"))
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:$postgresqlJdbcVersion")
}

application {
    mainClass.set("com.github.clasicrando.database.build.MainKt")
}
