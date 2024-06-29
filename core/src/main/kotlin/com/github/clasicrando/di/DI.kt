package com.github.clasicrando.di

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourceContactsDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourcesDao
import com.github.clasicrando.datasources.data.postgres.PgRecordWarehouseTypeDao
import com.github.clasicrando.datasources.model.DataSourceContact
import com.github.clasicrando.users.data.PgUsersDao
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.workflows.data.PgWorkflowsDao
import com.github.clasicrando.workflows.data.WorkflowsDao
import io.github.clasicrando.kdbc.core.connection.AsyncConnection
import io.github.clasicrando.kdbc.postgresql.Postgres
import io.github.clasicrando.kdbc.postgresql.connection.PgAsyncConnection
import io.github.clasicrando.kdbc.postgresql.connection.PgConnectOptions
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance

fun DI.MainBuilder.bindDatabaseComponents() {
    bindEagerSingleton {
        PgConnectOptions(
            host = System.getenv("EM_DB_HOST") ?: error("Missing EM_DB_HOST env parameter"),
            port =
                System.getenv("EM_DB_PORT")?.toInt()
                    ?: error("Missing EM_DB_PORT env parameter"),
            database = System.getenv("EM_DB") ?: error("Missing EM_DB env parameter"),
            username = System.getenv("EM_DB_USER") ?: error("Missing EM_DB_USER env parameter"),
            password =
                System.getenv("EM_DB_PASSWORD")
                    ?: error("Missing EM_DB_PASSWORD env parameter"),
        )
    }
    bindProvider<AsyncConnection> {
        val connection: PgAsyncConnection by di.instance()
        connection
    }
    bindProvider<PgAsyncConnection> {
        val connectOptions: PgConnectOptions by di.instance()
        runBlocking {
            Postgres.asyncConnection(connectOptions)
        }
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
    val connection: PgAsyncConnection by di.instance()
    connection.registerCompositeType<DataSourceContact>("em.data_source_contacts")
}
