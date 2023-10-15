package com.github.clasicrando.worker.ksp

@Target(AnnotationTarget.FUNCTION)
annotation class Worker(val taskDefName: String, val threadCount: Int)
