package com.github.clasicrando.jasync.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class RowParserProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return RowParserProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}
