package com.github.clasicrando

/**
 * Clean the text parameter value by converting blank strings into null and trimming any non-blank
 * values. This calls [takeIf], checking if the string [isNotBlank] and calls [trim] if the string
 * passes the test
 */
fun String.cleanNullableNonEmptyTextParameter() = this.takeIf { it.isNotBlank() }?.trim()
