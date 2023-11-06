plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":jasync-ksp"))
    ksp(project(":jasync-ksp"))
}
