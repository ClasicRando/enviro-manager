plugins {
    kotlin("jvm")
}

val conductorVersion: String by project
val kspVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation("com.netflix.conductor:conductor-client:$conductorVersion")
    implementation("com.netflix.conductor:conductor-common:$conductorVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}
