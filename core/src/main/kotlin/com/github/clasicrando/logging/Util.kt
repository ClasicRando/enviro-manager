package com.github.clasicrando.logging

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

fun Any.logger(): Lazy<KLogger> {
    return lazy { KotlinLogging.logger {} }
}

fun logger(): Lazy<KLogger> {
    return lazy { KotlinLogging.logger {} }
}
