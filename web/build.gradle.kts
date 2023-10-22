plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    application
    id("com.google.devtools.ksp")
}

val postgresqlJdbcVersion: String by project
val ktorVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("com.github.snappy:snappy:0.1")
    ksp("com.github.snappy:snappy:0.1")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:$postgresqlJdbcVersion")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName.set("enviro-manager-all.jar")
    }
}
