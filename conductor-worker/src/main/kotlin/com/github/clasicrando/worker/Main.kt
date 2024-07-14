package com.github.clasicrando.worker

import com.google.common.reflect.ClassPath
import com.netflix.conductor.client.automator.TaskRunnerConfigurer
import com.netflix.conductor.client.http.TaskClient
import com.netflix.conductor.common.metadata.tasks.Task
import com.netflix.conductor.common.metadata.tasks.TaskResult
import com.netflix.conductor.sdk.workflow.task.WorkerTask
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.typeOf

val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val taskClient = TaskClient()
    taskClient.setRootURI("http://localhost:8080/api/")

    val workers = collectMethodWorkers("com.github.clasicrando.worker")
    if (workers.isEmpty()) {
        logger.warn { "No workers found. Exiting task client" }
        return
    }
    val workerThreadCount = workers.associate { it.taskDefName to it.threadCount }
    var configurer: TaskRunnerConfigurer? = null
    try {
        configurer =
            TaskRunnerConfigurer
                .Builder(taskClient, workers)
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

private fun collectMethodWorkers(vararg packages: String): List<GeneratedWorker> {
    return ClassPath
        .from(GeneratedWorker::class.java.classLoader)
        .allClasses
        .flatMap { classInfo ->
            if (!checkPackages(packages, classInfo.name)) {
                return@flatMap emptyList()
            }

            val cls = classInfo.load().kotlin
            if (cls.objectInstance == null) {
                return@flatMap emptyList()
            }

            val objectRef = cls.objectInstance!!
            cls.memberFunctions
                .asSequence()
                .mapNotNull(::processMethod)
                .map { (method, annotation) ->
                    createGeneratedWorker(objectRef, method, annotation)
                }.toList()
        }
}

private fun checkPackages(
    packages: Array<out String>,
    fullName: String,
): Boolean = packages.any { fullName.startsWith(it) }

private fun processMethod(method: KFunction<*>): Pair<KFunction<TaskResult>, WorkerTask>? {
    val annotation = method.findAnnotation<WorkerTask>() ?: return null
    if (!verifyMethod(method)) {
        return null
    }
    @Suppress("UNCHECKED_CAST")
    return method as KFunction<TaskResult> to annotation
}

private fun verifyMethod(method: KFunction<*>): Boolean {
    if (method.returnType != typeOf<TaskResult>()) {
        logger.atInfo {
            message = "Found WorkerTask where the return value is not TaskResult"
        }
        return false
    }
    return true
}

private fun createGeneratedWorker(
    objectReference: Any,
    method: KFunction<TaskResult>,
    annotation: WorkerTask,
): GeneratedWorker =
    object : GeneratedWorker {
        override val threadCount: Int = annotation.threadCount

        override fun getTaskDefName(): String = annotation.value

        override fun execute(task: Task): TaskResult = method.call(objectReference, task)
    }
