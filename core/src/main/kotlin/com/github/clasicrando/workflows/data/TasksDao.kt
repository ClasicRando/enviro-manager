package com.github.clasicrando.workflows.data

import com.github.clasicrando.workflows.model.Task
import com.github.clasicrando.workflows.model.TaskId

interface TasksDao {
    suspend fun getTasks(): List<Task>

    suspend fun getTaskById(taskId: TaskId): Task?

    suspend fun createTask(
        name: String,
        description: String,
    )

    suspend fun updateTask(
        taskId: TaskId,
        name: String,
        description: String,
    )
}
