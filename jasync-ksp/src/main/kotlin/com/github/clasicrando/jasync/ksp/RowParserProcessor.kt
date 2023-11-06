package com.github.clasicrando.jasync.ksp

import com.github.clasicrando.jasync.symbol.ResultRow
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSEmptyVisitor

class RowParserProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private val rowParsers = mutableMapOf<String, String>()
    private var isDone = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(resultRowAnnotationName)
        val result = symbols.filter { !it.validate() }.toList()
        for (symbol in symbols) {
            if (symbol !is KSClassDeclaration) {
                val name = symbol.containingFile?.fileName ?: symbol
                logger.warn("Found ResultRow symbol that is not a class, '$name'")
                continue
            }
            if (symbol.validate()) {
                val visitor = RowParserVisitor(resolver)
                symbol.accept(visitor, Unit)
                rowParsers[visitor.qualifiedName] = visitor.rowParserClass
            }
        }

        collectRowParsers(resolver)
        return result
    }

    data class DataClassParameterDetails(
        val fieldName: String,
        val typeName: String,
        val isInline: Boolean,
        val subType: String?,
    ) {
        val simpleTypeName = typeName.split(".").last()
        val simpleSubTypeName = subType?.split(".")?.last()
    }

    inner class DataClassParameterVisitor : KSEmptyVisitor<Unit, DataClassParameterDetails>() {
        override fun visitValueParameter(
            valueParameter: KSValueParameter,
            data: Unit,
        ): DataClassParameterDetails {
            var subType: String? = null
            val paramType = valueParameter.type.resolve()
            val paramDeclaration = paramType.declaration as KSClassDeclaration
            val paramDeclarationName = paramDeclaration.simpleName.asString()
            if (paramDeclarationName != "List") {
                require(paramDeclaration.typeParameters.isEmpty()) {
                    "Row parser fields must not have generic types"
                }
            } else {
                subType =
                    paramType.arguments
                        .first()
                        .type!!
                        .resolve()
                        .declaration
                        .qualifiedName!!
                        .asString()
            }

            val isInline = paramDeclaration.modifiers.contains(Modifier.VALUE)
            if (isInline) {
                val parameterName =
                    paramDeclaration.primaryConstructor!!
                        .parameters
                        .first()
                        .name!!
                        .asString()
                val innerValueProperty =
                    paramDeclaration.getDeclaredProperties()
                        .first { it.simpleName.asString() == parameterName }
                require(!innerValueProperty.modifiers.contains(Modifier.PRIVATE)) {
                    "Value type parameter cannot be private for row parser generation"
                }
                require(!innerValueProperty.modifiers.contains(Modifier.PROTECTED)) {
                    "Value type parameter cannot be protected for row parser generation"
                }
            }

            return DataClassParameterDetails(
                fieldName = valueParameter.name!!.asString(),
                typeName = paramDeclaration.qualifiedName!!.asString(),
                isInline = isInline,
                subType = subType,
            )
        }

        override fun defaultHandler(
            node: KSNode,
            data: Unit,
        ): DataClassParameterDetails {
            return DataClassParameterDetails(
                fieldName = "",
                typeName = "",
                isInline = false,
                subType = null,
            )
        }
    }

    data class PropertyAnnotationValues(
        val rename: String?,
        val flatten: Boolean,
        val fromString: String?,
    )

    inner class DataClassPropertyVisitor : KSEmptyVisitor<Unit, PropertyAnnotation>() {
        override fun visitPropertyDeclaration(
            property: KSPropertyDeclaration,
            data: Unit,
        ): PropertyAnnotation {
            val propertyName = property.simpleName.asString()
            val flattenAnnotation =
                property.annotations
                    .any { it.shortName.asString() == "Flatten" }
            val renameAnnotation =
                property.annotations
                    .firstOrNull { it.shortName.asString() == "Rename" }
                    ?.arguments
                    ?.get(0)
                    ?.value as? String?
            val fromStringAnnotation =
                property.annotations
                    .firstOrNull { it.shortName.asString() == "FromString" }
                    ?.arguments
                    ?.get(0)
                    ?.value as? String?
            return propertyName to
                PropertyAnnotationValues(
                    rename = renameAnnotation,
                    flatten = flattenAnnotation,
                    fromString = fromStringAnnotation,
                )
        }

        override fun defaultHandler(
            node: KSNode,
            data: Unit,
        ): PropertyAnnotation {
            return "" to
                PropertyAnnotationValues(
                    rename = null,
                    flatten = false,
                    fromString = null,
                )
        }
    }

    inner class RowParserVisitor(private val resolver: Resolver) : AbstractFileGeneratorVisitor() {
        private val dataClassParameterVisitor = DataClassParameterVisitor()
        private val dataClassPropertyVisitor = DataClassPropertyVisitor()
        private lateinit var classDeclaration: KSClassDeclaration
        private lateinit var simpleName: String
        private lateinit var classPackage: String
        val qualifiedName: String get() = "$classPackage.$simpleName"
        private lateinit var propertyAnnotations: Map<String, PropertyAnnotationValues>
        private lateinit var parameters: List<DataClassParameterDetails>
        var rowParserClass: String = ""
            private set

        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {
            this.classDeclaration = classDeclaration
            this.simpleName = classDeclaration.simpleName.asString()
            this.classPackage = classDeclaration.packageName.asString()
            addImport("com.github.jasync.sql.db.RowData")
            addImport("$classPackage.$simpleName")
            rowParserClass =
                classDeclaration.annotations
                    .first { it.shortName.asString() == resultRowAnnotationShortName }
                    .arguments[0]
                    .value as String
            if (rowParserClass.isNotBlank()) {
                val declaration =
                    resolver.getClassDeclarationByName(
                        object : KSName {
                            override fun asString(): String = rowParserClass

                            override fun getQualifier(): String {
                                return rowParserClass.split(".").dropLast(1).joinToString(".")
                            }

                            override fun getShortName(): String {
                                return rowParserClass.split(".").last()
                            }
                        },
                    )
                requireNotNull(declaration) {
                    "RowParser class name cannot be found '$rowParserClass'. " +
                        "Must be fully qualified name"
                }
                val superTypes = declaration.getAllSuperTypes()
                require(superTypes.any { it.declaration.simpleName.asString() == "RowParser" }) {
                    "RowParser class name must implement the RowParser interface"
                }
                return
            }

            require(classDeclaration.modifiers.contains(Modifier.DATA)) {
                "Only data classes can be result rows, '$simpleName'"
            }
            require(classDeclaration.typeParameters.none()) {
                "Cannot generate row parser for class with generic parameters, '$simpleName'"
            }

            propertyAnnotations =
                classDeclaration.getDeclaredProperties()
                    .map { it.accept(dataClassPropertyVisitor, Unit) }
                    .filter { it.first.isNotBlank() }
                    .associate { it }
            classDeclaration.primaryConstructor!!.accept(this, Unit)
            generateFile(codeGenerator)
        }

        override fun visitFunctionDeclaration(
            function: KSFunctionDeclaration,
            data: Unit,
        ) {
            parameters = function.parameters.map { it.accept(dataClassParameterVisitor, Unit) }
        }

        private fun parameterToConstructorParam(details: DataClassParameterDetails): String {
            val annotations = propertyAnnotations[details.fieldName]
            val columnName = annotations?.rename ?: details.fieldName
            val assignedValue =
                when {
                    annotations?.flatten == true -> {
                        addImport(details.typeName)
                        addImport("com.github.clasicrando.jasync.cache.RowParserCache")
                        val simpleTypeName = details.typeName.split(".").last()
                        "RowParserCache.getOrThrow<$simpleTypeName>().parseRow(row)"
                    }
                    !annotations?.fromString.isNullOrBlank() -> {
                        val simpleTypeName = details.simpleTypeName
                        var fromStringMethod = annotations!!.fromString!!

                        if (fromStringMethod.contains(".")) {
                            addImport(fromStringMethod)
                        } else {
                            addImport(details.typeName)
                            fromStringMethod = "${details.simpleSubTypeName}.$fromStringMethod"
                        }

                        if (simpleTypeName == "List") {
                            details.subType?.let { addImport(it) }
                            "row.getAs<List<String>>(\"$columnName\").map { $fromStringMethod(it) }"
                        } else {
                            "$fromStringMethod(row.getString(\"$columnName\"))"
                        }
                    }
                    details.isInline -> {
                        addImport(details.typeName)
                        "${details.simpleTypeName}(row.getAs(\"$columnName\"))"
                    }
                    else -> "row.getAs(\"$columnName\")"
                }
            return "${details.fieldName} = $assignedValue,"
        }

        override fun generateFile(codeGenerator: CodeGenerator) {
            val outputClassName = "${simpleName}RowParser"
            rowParserClass = outputClassName
            val fields =
                parameters.joinToString(
                    separator = "\n                                ",
                ) {
                    parameterToConstructorParam(it)
                }
            val imports =
                importsSorted.joinToString(separator = "\n                    ") {
                    "import $it"
                }
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = true, classDeclaration.containingFile!!),
                packageName = DESTINATION_PACKAGE,
                fileName = outputClassName,
            ).use {
                it.appendText(
                    """
                    package com.github.clasicrando.jasync

                    import com.github.clasicrando.jasync.rowparser.RowParser
                    $imports

                    class $outputClassName : RowParser<$simpleName> {
                        override fun parseRow(row: RowData): $simpleName {
                            return $simpleName(
                                $fields
                            )
                        }
                    }

                    """.trimIndent(),
                )
            }
        }
    }

    private fun collectRowParsers(resolver: Resolver) {
        if (isDone) {
            return
        }

        val pairs =
            rowParsers.entries.joinToString(
                separator = "\n                        ",
            ) { (className, parserName) ->
                "typeOf<$className>() to $parserName(),"
            }
        codeGenerator.createNewFile(
            dependencies =
                Dependencies(
                    aggregating = true,
                    *resolver.getAllFiles().toList().toTypedArray(),
                ),
            packageName = DESTINATION_PACKAGE,
            fileName = ROW_PARSERS_OBJECT_NAME,
        ).use {
            it.appendText(
                """
                package com.github.clasicrando.jasync

                import com.github.clasicrando.jasync.rowparser.RowParser
                import kotlin.reflect.typeOf
                import kotlin.reflect.KType

                object $ROW_PARSERS_OBJECT_NAME {
                    val items: Map<KType, RowParser<*>> = mapOf(
                        $pairs
                    )
                }

                """.trimIndent(),
            )
        }
        isDone = true
    }

    companion object {
        private val resultRowAnnotationName = ResultRow::class.qualifiedName!!
        private val resultRowAnnotationShortName = ResultRow::class.simpleName!!
        private const val DESTINATION_PACKAGE = "com.github.clasicrando.jasync"
        const val ROW_PARSERS_OBJECT_NAME = "RowParsers"
        const val ROW_PARSERS_OBJECT_NAME_FULL = "$DESTINATION_PACKAGE.$ROW_PARSERS_OBJECT_NAME"
    }
}

typealias PropertyAnnotation = Pair<String, RowParserProcessor.PropertyAnnotationValues>
