plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

dependencies {
    implementation(project(":core"))
}

application {
    mainClass.set("com.github.clasicrando.database.build.MainKt")
}
