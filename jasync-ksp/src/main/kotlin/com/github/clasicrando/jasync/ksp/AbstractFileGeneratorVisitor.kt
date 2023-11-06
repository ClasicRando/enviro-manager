package com.github.clasicrando.jasync.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSVisitorVoid

abstract class AbstractFileGeneratorVisitor : KSVisitorVoid() {
    private val imports = mutableSetOf<String>()

    protected fun addImport(import: String) {
        if (import.startsWith("kotlin.")) return
        imports += import
    }

    protected fun addImports(vararg imports: String) {
        for (import in imports) {
            addImport(import)
        }
    }

    abstract fun generateFile(codeGenerator: CodeGenerator)

    protected val importsSorted: List<String> get() = imports.sorted()
}
