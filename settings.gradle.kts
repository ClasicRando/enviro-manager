pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    val ktorVersion: String by settings
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val shadowJarVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
        id("com.google.devtools.ksp") version kspVersion
        id("com.github.johnrengelman.shadow") version shadowJarVersion
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
