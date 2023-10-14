plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    application
}

group = "com.github.clasicrando"
version = "0.1"

repositories {
    mavenCentral()
}

val ktorVersion: String by project
val kotlinTestVersion: String by project
val kotlinLoggingVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation(kotlin("test", version = kotlinTestVersion))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName.set("enviro-manager-all.jar")
    }
}

kotlin {
    jvmToolchain(11)
}
