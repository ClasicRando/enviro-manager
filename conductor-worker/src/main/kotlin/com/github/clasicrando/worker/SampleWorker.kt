package com.github.clasicrando.worker

import com.github.clasicrando.worker.ksp.Worker
import com.netflix.conductor.common.metadata.tasks.Task
import com.netflix.conductor.common.metadata.tasks.TaskResult

@Worker(taskDefName = "task_1", threadCount = 1)
fun task1(task: Task): TaskResult {
    val result = TaskResult(task)
    result.status = TaskResult.Status.COMPLETED

    result.outputData["outputKey1"] = "value"
    result.outputData["oddEven"] = 1
    result.outputData["mod"] = 4

    return result
}
