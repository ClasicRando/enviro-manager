package com.github.clasicrando.worker.ksp

import com.netflix.conductor.client.worker.Worker

interface GeneratedWorker : Worker {
    val threadCount: Int

    fun taskThreadCountEntry() = taskDefName to threadCount
}
