package com.github.clasicrando.worker

import com.github.clasicrando.worker.ksp.GeneratedWorker
import com.netflix.conductor.client.automator.TaskRunnerConfigurer
import com.netflix.conductor.client.http.TaskClient
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val taskClient = TaskClient()
    taskClient.setRootURI("http://localhost:8080/api/")

    val workers =
        listOf<GeneratedWorker>(
            Task1Worker,
        )
    val workerThreadCount = workers.associate { it.taskThreadCountEntry() }
    var configurer: TaskRunnerConfigurer? = null
    try {
        configurer =
            TaskRunnerConfigurer.Builder(taskClient, workers)
                .withTaskThreadCount(workerThreadCount)
                .build()
        configurer.init()
    } catch (ex: Throwable) {
        try {
            configurer?.shutdown()
        } catch (ex: Throwable) {
            logger.atError {
                message =
                    "Exception during TaskRunnerConfigurer shutdown. Ignored since it is not fatal"
                cause = ex
            }
        }
        logger.atError {
            message = "Top level error running TaskRunnerConfigurer"
            cause = ex
        }
    }
}
