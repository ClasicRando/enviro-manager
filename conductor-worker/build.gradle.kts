plugins {
    kotlin("jvm")
    application
}

val conductorVersion: String by project

dependencies {
    implementation("com.netflix.conductor:conductor-client:$conductorVersion")
    implementation("com.netflix.conductor:conductor-common:$conductorVersion")
}

application {
    mainClass.set("com.github.clasicrando.worker.MainKt")
}
