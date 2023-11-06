package com.github.clasicrando.jasync.literal

/** Exception thrown when a literal parser cannot decode a value to the expected type */
class LiteralParseError
    @PublishedApi
    internal constructor(
        expectedType: String,
        value: Any?,
        reason: String? = null,
    ) : Throwable(
            "Error parsing composite value. " +
                "Expected type $expectedType but got '$value'.${reason ?: ""}",
        )

/**
 * Exception thrown when a literal parser has exhausted all values but a parser calls read one more
 * time.
 */
class ExhaustedBuffer internal constructor() :
    Throwable("Action called on exhausted literal buffer")
