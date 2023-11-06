package com.github.clasicrando.jasync.query

import com.github.clasicrando.jasync.EmptyResult
import com.github.clasicrando.jasync.TooManyRows
import com.github.clasicrando.jasync.cache.RowParserCache
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.asSuspending
import kotlinx.coroutines.future.await

fun sqlCommand(query: String): QueryBuilder = QueryBuilder(query)

class QueryBuilder internal constructor(
    private val query: String,
) {
    private val parameters = mutableListOf<Any?>()

    fun bind(vararg parameters: Any?): QueryBuilder {
        for (parameter in parameters) {
            this.parameters.add(parameter)
        }
        return this
    }

    @PublishedApi
    internal suspend fun getQueryResult(executor: Connection): QueryResult {
        val connection = executor.asSuspending.connect()
        val future =
            connection.sendPreparedStatement(
                query = query,
                values = parameters,
            )
        return future.await()
    }

    suspend fun execute(executor: Connection): QueryResult {
        return getQueryResult(executor)
    }

    suspend inline fun <reified T : Any> query(executor: Connection): List<T> {
        val rowParser = RowParserCache.getOrThrow<T>()
        val queryResult = getQueryResult(executor)
        return queryResult.rows.map(transform = rowParser::parseRow)
    }

    suspend inline fun <reified T : Any> querySingleOrNull(executor: Connection): T? {
        val rowParser = RowParserCache.getOrThrow<T>()
        val queryResult = getQueryResult(executor)
        val rows = queryResult.rows
        if (rows.size > 1) {
            throw TooManyRows()
        }
        return rows.map(transform = rowParser::parseRow).firstOrNull()
    }

    suspend inline fun <reified T : Any> querySingle(executor: Connection): T {
        return querySingleOrNull<T>(executor) ?: throw EmptyResult()
    }

    suspend inline fun <reified T : Any> queryFirstOrNull(executor: Connection): T? {
        val rowParser = RowParserCache.getOrThrow<T>()
        val queryResult = getQueryResult(executor)
        return queryResult.rows
            .firstOrNull()
            ?.let {
                rowParser.parseRow(it)
            }
    }

    suspend inline fun <reified T : Any> queryFirst(executor: Connection): T {
        return queryFirstOrNull<T>(executor) ?: throw EmptyResult()
    }

    suspend fun <T> queryScalarOrNull(executor: Connection): T? {
        val queryResult = getQueryResult(executor)
        return queryResult.rows
            .firstOrNull()
            ?.getAs(0)
    }

    suspend fun <T> queryScalar(executor: Connection): T {
        return queryScalarOrNull<T>(executor) ?: throw EmptyResult()
    }
}
