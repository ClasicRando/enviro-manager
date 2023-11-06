package com.github.clasicrando.di

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourceContactsDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourcesDao
import com.github.clasicrando.datasources.data.postgres.PgRecordWarehouseTypeDao
import com.github.clasicrando.datasources.model.DataSourceContactTypeParser
import com.github.clasicrando.jasync.json.PgJsonTypeParser
import com.github.clasicrando.jasync.type.registerType
import com.github.clasicrando.users.data.PgUsersDao
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.workflows.data.PgWorkflowsDao
import com.github.clasicrando.workflows.data.WorkflowsDao
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun DI.MainBuilder.bindDatabaseComponents() {
    bindEagerSingleton<ConnectionPool<PostgreSQLConnection>> {
        PostgreSQLConnectionBuilder.createConnectionPool {
            host = System.getenv("EM_DB_HOST") ?: error("Missing EM_DB_HOST env parameter")
            port = System.getenv("EM_DB_PORT")?.toInt()
                ?: error("Missing EM_DB_PORT env parameter")
            database = System.getenv("EM_DB") ?: error("Missing EM_DB env parameter")
            username = System.getenv("EM_DB_USER") ?: error("Missing EM_DB_USER env parameter")
            password = System.getenv("EM_DB_PASSWORD")
                ?: error("Missing EM_DB_PASSWORD env parameter")
        }
    }
    bindProvider<Connection> {
        val pool: ConnectionPool<PostgreSQLConnection> by di.instance()
        pool
    }
}

fun DI.MainBuilder.bindDaoComponents() {
    bindDatabaseComponents()
    bindProvider<DataSourcesDao> {
        PgDataSourcesDao(di)
    }
    bindProvider<UsersDao> {
        PgUsersDao(di)
    }
    bindProvider<RecordWarehouseTypesDao> {
        PgRecordWarehouseTypeDao(di)
    }
    bindProvider<WorkflowsDao> {
        PgWorkflowsDao(di)
    }
    bindProvider<DataSourceContactsDao> {
        PgDataSourceContactsDao(di)
    }
}

suspend fun DI.registerTypes() {
    val connection: Connection by di.instance()
    val executor = connection.asSuspending.connect()
    PgJsonTypeParser.registerType(executor)
    DataSourceContactTypeParser.registerType(executor)
}
