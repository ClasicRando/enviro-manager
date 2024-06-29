plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

val conductorVersion: String by project

dependencies {
    implementation(project(":core"))
    // https://mvnrepository.com/artifact/org.conductoross/conductor-client
    implementation("org.conductoross:conductor-client:$conductorVersion")
    // https://mvnrepository.com/artifact/org.conductoross/conductor-java-sdk
    implementation("org.conductoross:conductor-java-sdk:$conductorVersion")
    // https://mvnrepository.com/artifact/org.conductoross/conductor-common
    implementation("org.conductoross:conductor-common:$conductorVersion")
}

application {
    mainClass.set("com.github.clasicrando.worker.MainKt")
}
