plugins {
    kotlin("jvm")
}

allprojects {
    repositories {
        mavenCentral()
    }
    group = "com.github.clasicrando"
    version = "0.1"
}

subprojects {
    apply(plugin = "kotlin")

    val kotlinTestVersion: String by project
    val kotlinLoggingVersion: String by project
    val logbackVersion: String by project

    dependencies {
        implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
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
