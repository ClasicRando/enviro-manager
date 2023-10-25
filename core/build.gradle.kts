plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

val postgresqlJdbcVersion: String by project
val apacheCommonsDbcpVersion: String by project

dependencies {
    implementation("com.github.snappy:snappy:0.1")
    ksp("com.github.snappy:snappy:0.1")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:$postgresqlJdbcVersion")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2
    implementation("org.apache.commons:commons-dbcp2:$apacheCommonsDbcpVersion")
}
