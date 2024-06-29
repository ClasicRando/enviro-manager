plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val kdbcVersion: String by project

repositories {
    mavenLocal()
}

dependencies {
    // https://mvnrepository.com/artifact/io.github.clasicrando/kdbc-postgresql
    implementation("io.github.clasicrando:kdbc-postgresql:$kdbcVersion")
}
