package com.github.clasicrando.worker

import com.netflix.conductor.common.metadata.tasks.Task
import com.netflix.conductor.common.metadata.tasks.TaskResult
import com.netflix.conductor.sdk.workflow.task.WorkerTask

object SampleWorker {
    @WorkerTask(value = "task_1", threadCount = 1)
    fun task1(task: Task): TaskResult {
        val result = TaskResult(task)
        result.status = TaskResult.Status.COMPLETED

        result.outputData["outputKey1"] = "value"
        result.outputData["oddEven"] = 1
        result.outputData["mod"] = 4

        return result
    }
}
