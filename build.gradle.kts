plugins {
    kotlin("jvm")
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    group = "com.github.clasicrando"
    version = "0.1"
}

subprojects {
    apply(plugin = "kotlin")

    val kotlinLoggingVersion: String by project
    val slfj4Version: String by project
    val logbackVersion: String by project
    val kotlinxCoroutineVersion: String by project
    val kotlinxSerializationVersion: String by project
    val kodeinVersion: String by project
    val kotlinTestVersion: String by project

    dependencies {
        implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
        implementation("org.slf4j:slf4j-api:$slfj4Version")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxCoroutineVersion")
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-core-jvm
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$kotlinxSerializationVersion")
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json-jvm
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$kotlinxSerializationVersion")
        // https://mvnrepository.com/artifact/org.kodein.di/kodein-di-jvm
        implementation("org.kodein.di:kodein-di-jvm:$kodeinVersion")
        testImplementation(kotlin("test", version = kotlinTestVersion))
    }

    kotlin {
        jvmToolchain(11)
    }

    tasks.test {
        testLogging {
            setExceptionFormat("full")
            events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            )
            showStandardStreams = true
            afterSuite(KotlinClosure2<TestDescriptor,TestResult,Unit>({ descriptor, result ->
                if (descriptor.parent == null) {
                    println("\nTest Result: ${result.resultType}")
                    println("""
                    Test summary: ${result.testCount} tests, 
                    ${result.successfulTestCount} succeeded, 
                    ${result.failedTestCount} failed, 
                    ${result.skippedTestCount} skipped
                """.trimIndent().replace("\n", ""))
                }
            }))
        }
        useJUnitPlatform()
    }
}
