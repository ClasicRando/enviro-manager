plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val kspVersion: String by project
val jasyncPostgresqlVersion: String by project

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    api("com.github.jasync-sql:jasync-postgresql:$jasyncPostgresqlVersion")
}
