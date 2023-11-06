package com.github.clasicrando.jasync.type

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.column.ColumnEncoderDecoder
import com.github.jasync.sql.db.postgresql.column.ArrayDecoder
import com.github.jasync.sql.db.postgresql.column.PostgreSQLColumnDecoderRegistry
import com.github.jasync.sql.db.postgresql.column.PostgreSQLColumnEncoderRegistry
import kotlinx.coroutines.future.await
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
interface TypeParser<T : Any> : ColumnEncoderDecoder {
    val type: KClass<T>
    val typeName: String
    val hasArrayType: Boolean

    fun T.encodeToString(): String

    fun decodeFromString(value: String): T?

    override fun encode(value: Any): String {
        require(type.isInstance(value))
        return (value as T).encodeToString()
    }

    override fun decode(value: String): Any? = decodeFromString(value)
}

private val typeQuery =
    """
    select t.oid
    from pg_type t
    join pg_namespace n on t.typnamespace = n.oid
    where
        t.typname = ?
        and n.nspname = coalesce(nullif(?,''), 'pg_catalog')
    """.trimIndent()

private val arrayTypeQuery =
    """
    select t.oid
    from pg_type t
    join pg_namespace n on t.typnamespace = n.oid
    where
        t.typname = '_' || ?
        and n.nspname = coalesce(nullif(?,''), 'pg_catalog')
    """.trimIndent()

suspend fun <T : Any> TypeParser<T>.registerType(executor: Connection) {
    val connection = executor.asSuspending.connect()
    val simpleTypeName = typeName.split(".").last()
    val schemaName = typeName.split(".").dropLast(1).joinToString(separator = ".")

    val queryResult =
        connection.sendPreparedStatement(
            query = typeQuery,
            values = listOf(simpleTypeName, schemaName),
        ).await()
    require(queryResult.rows.size == 1) {
        "Found multiple types for pg type name '$typeName'"
    }
    val typeOid = queryResult.rows[0].getAs<Int>(0)

    PostgreSQLColumnEncoderRegistry.Instance
        .registerEncoder(
            clazz = type.java,
            type = typeOid,
            encoder = this,
        )
    PostgreSQLColumnDecoderRegistry.Instance
        .registerDecoder(
            type = typeOid,
            decoder = this,
        )

    if (hasArrayType) {
        val arrayQueryResult =
            connection.sendPreparedStatement(
                query = arrayTypeQuery,
                values = listOf(simpleTypeName, schemaName),
            ).await()
        require(arrayQueryResult.rows.size == 1) {
            "Found multiple types for array pg type name '_$typeName'"
        }
        val arrayTypeOid = arrayQueryResult.rows[0].getAs<Int>(0)

        PostgreSQLColumnDecoderRegistry.Instance
            .registerDecoder(
                type = arrayTypeOid,
                decoder = ArrayDecoder(decoder = this),
            )
    }
}
