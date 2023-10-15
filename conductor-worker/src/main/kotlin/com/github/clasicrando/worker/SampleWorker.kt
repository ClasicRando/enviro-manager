package com.github.clasicrando.worker

import com.netflix.conductor.client.worker.Worker
import com.netflix.conductor.common.metadata.tasks.Task
import com.netflix.conductor.common.metadata.tasks.TaskResult

class SampleWorker(private val taskDefName: String) : Worker {
    override fun getTaskDefName(): String {
        return taskDefName
    }

    override fun execute(task: Task?): TaskResult {
        requireNotNull(task)
        val result = TaskResult(task)
        result.status = TaskResult.Status.COMPLETED

        result.outputData["outputKey1"] = "value"
        result.outputData["oddEven"] = 1
        result.outputData["mod"] = 4

        return result
    }
}