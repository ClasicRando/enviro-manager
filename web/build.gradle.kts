plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    application
}

val ktorVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName.set("enviro-manager-all.jar")
    }
}
