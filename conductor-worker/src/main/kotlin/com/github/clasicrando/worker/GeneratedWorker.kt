package com.github.clasicrando.worker

import com.netflix.conductor.client.worker.Worker

interface GeneratedWorker : Worker {
    val threadCount: Int
}
