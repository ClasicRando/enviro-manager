plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    application
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

val postgresqlJdbcVersion: String by project
val ktorVersion: String by project
val kodeinVersion: String by project
val kredsVersion: String by project
val apacheCommonsDbcpVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.github.crackthecodeabhi:kreds:$kredsVersion")
    implementation("com.github.snappy:snappy:0.1")
    ksp("com.github.snappy:snappy:0.1")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:$postgresqlJdbcVersion")
    // https://mvnrepository.com/artifact/org.kodein.di/kodein-di-jvm
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2
    implementation("org.apache.commons:commons-dbcp2:$apacheCommonsDbcpVersion")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName.set("enviro-manager-all.jar")
    }
}
