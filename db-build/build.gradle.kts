plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

dependencies {
    implementation("com.github.snappy:snappy:0.1")
    implementation(project(":core"))
}

application {
    mainClass.set("com.github.clasicrando.database.build.MainKt")
}
