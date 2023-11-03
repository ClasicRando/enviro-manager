package com.github.clasicrando.di

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourceContactsDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourcesDao
import com.github.clasicrando.datasources.data.postgres.PgRecordWarehouseTypeDao
import com.github.clasicrando.users.data.PgUsersDao
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.workflows.data.PgWorkflowsDao
import com.github.clasicrando.workflows.data.WorkflowsDao
import org.apache.commons.dbcp2.BasicDataSource
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.postgresql.PGConnection
import javax.sql.DataSource

fun DI.MainBuilder.bindDatabaseComponents() {
    bindEagerSingleton<DataSource> {
        BasicDataSource().apply {
            defaultAutoCommit = true
            url = System.getenv("EM_DB_URL")
        }
    }
    bindProvider<PGConnection> {
        val dataSource: DataSource by di.instance()
        dataSource.connection.unwrap(PGConnection::class.java)
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
