plugins {
    kotlin("jvm")
    application
    id("com.google.devtools.ksp")
    id("com.github.johnrengelman.shadow")
}

val conductorVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation(project(":conductor-worker-ksp"))
    ksp(project(":conductor-worker-ksp"))
    implementation("com.netflix.conductor:conductor-client:$conductorVersion")
    implementation("com.netflix.conductor:conductor-common:$conductorVersion")
}

application {
    mainClass.set("com.github.clasicrando.worker.MainKt")
}
