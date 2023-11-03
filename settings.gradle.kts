pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    val kotlinVersion: String by settings
    val kotlinxSerializationPluginVersion: String by settings
    val ktorVersion: String by settings
    val kspVersion: String by settings
    val shadowJarVersion: String by settings
    val ktlintPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinxSerializationPluginVersion
        id("io.ktor.plugin") version ktorVersion
        id("com.google.devtools.ksp") version kspVersion
        id("com.github.johnrengelman.shadow") version shadowJarVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "enviro-manager"
include("web")
include("core")
include("conductor-worker")
include("conductor-worker-ksp")
include("db-build")
include("jasync-ksp")
