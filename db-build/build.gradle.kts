plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

val kdbcVersion: String by project

repositories {
    mavenLocal()
}

dependencies {
    implementation(project(":core"))
    // https://mvnrepository.com/artifact/io.github.clasicrando/kdbc-postgresql
    implementation("io.github.clasicrando:kdbc-postgresql:$kdbcVersion")
}

application {
    mainClass.set("com.github.clasicrando.database.build.MainKt")
}
