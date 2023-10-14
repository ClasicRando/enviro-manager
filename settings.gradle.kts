pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    val ktorVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "enviro-manager"