package com.github.clasicrando.worker

import com.github.clasicrando.logging.logger
import com.netflix.conductor.client.automator.TaskRunnerConfigurer
import com.netflix.conductor.client.http.TaskClient
import io.github.oshai.kotlinlogging.KLogger

val logger: KLogger by logger()

fun main(args: Array<String>) {
    val taskClient = TaskClient()
    taskClient.setRootURI("http://localhost:8080/api/")

    val workerNamePrefix = ""
    val task1Name = "task_1"
    val workerThreadCount = mapOf(
        task1Name to 1
    )
    val worker1 = SampleWorker(task1Name)

    val workers = listOf(worker1)
    var configurer: TaskRunnerConfigurer? = null
    try {
        configurer = TaskRunnerConfigurer.Builder(taskClient, workers)
            .withWorkerNamePrefix(workerNamePrefix)
            .withTaskThreadCount(workerThreadCount)
            .build()
        configurer.init()
    } catch (ex: Throwable) {
        try {
            configurer?.shutdown()
        } catch (ex: Throwable) {
            logger.atError {
                message = "Exception during TaskRunnerConfigurer shutdown. Ignored since it is not fatal"
                cause = ex
            }
        }
        logger.atError {
            message = "Top level error running TaskRunnerConfigurer"
            cause = ex
        }
    }
}
