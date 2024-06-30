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
import io.github.clasicrando.kdbc.core.pool.PoolOptions
import io.github.clasicrando.kdbc.core.use
import io.github.clasicrando.kdbc.postgresql.connection.PgConnectOptions
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
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
    bindEagerSingleton<PgAsyncConnectionPool> {
        val connectOptions: PgConnectOptions by di.instance()
        val poolOptions = PoolOptions()
        PgAsyncConnectionPool(
            connectOptions = connectOptions,
            poolOptions = poolOptions,
        )
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
    val pool: PgAsyncConnectionPool by di.instance()
    pool.acquire().use { conn ->
        conn.registerCompositeType<DataSourceContact>("em.data_source_contacts")
    }
}
