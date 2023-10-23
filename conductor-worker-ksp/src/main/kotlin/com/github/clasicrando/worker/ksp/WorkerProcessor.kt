package com.github.clasicrando.worker.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import java.io.OutputStream

class WorkerProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Worker::class.java.name)
        val result = symbols.filter { !it.validate() }.toList()
        symbols.filter { it is KSFunctionDeclaration && it.validate() }
            .forEach { it.accept(Visitor(), Unit) }
        return result
    }

    inner class Visitor : KSVisitorVoid() {
        override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit) {
            val parameterType =
                valueParameter.type
                    .resolve()
                    .declaration
            val simpleName = parameterType.simpleName.asString()
            val packageName = parameterType.packageName.asString()
            check(simpleName == "Task" && packageName == "com.netflix.conductor.common.metadata.tasks") {
                "Parameter of worker function must be a Task"
            }
        }

        override fun visitTypeReference(typeReference: KSTypeReference, data: Unit) {
            val type = typeReference.resolve().declaration
            val simpleName = type.simpleName.asString()
            val packageName = type.packageName.asString()
            check(simpleName == "TaskResult" && packageName == "com.netflix.conductor.common.metadata.tasks") {
                "Return value of worker function must be a TaskResult"
            }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            check(function.parameters.size == 1)
            function.parameters[0].accept(this, data)
            function.returnType?.accept(this, data)
                ?: error("Worker function must return a value")

            val functionContainingFile = function.containingFile!!
            val parentClassPackage = functionContainingFile.packageName.asString()

            val annotationValues = function.annotations.first {
                it.shortName.asString() == Worker::class.simpleName
            }.arguments
            val taskDefName = annotationValues[0].value as String
            val threadCount = annotationValues[1].value as Int

            val functionName = function.simpleName.asString()
            val titleCaseName = "${functionName.first().uppercase()}${functionName.substring(1)}"
            val workerClassName = "${titleCaseName}Worker"

            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(true, function.containingFile!!),
                packageName = parentClassPackage,
                fileName = "${titleCaseName}Worker",
            )
            file.appendText(
                """
                @file:Suppress("ktlint")
                package $parentClassPackage

                import com.github.clasicrando.worker.ksp.GeneratedWorker
                import com.netflix.conductor.common.metadata.tasks.Task
                import com.netflix.conductor.common.metadata.tasks.TaskResult

                object $workerClassName : GeneratedWorker {
                    override val threadCount = $threadCount

                    override fun getTaskDefName(): String {
                        return "${taskDefName.replace("\"", "\"\"")}"
                    }

                    override fun execute(task: Task?): TaskResult {
                        requireNotNull(task) { "Worker received a null task" }
                        return $functionName(task)
                    }
                }""".trimIndent()
            )
            file.appendText("\n")
        }

        private fun OutputStream.appendText(str: String) {
            this.write(str.toByteArray())
        }
    }
}
