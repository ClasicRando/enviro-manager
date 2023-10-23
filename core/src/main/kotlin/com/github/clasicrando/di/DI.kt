package com.github.clasicrando.di

import org.apache.commons.dbcp2.BasicDataSource
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.postgresql.PGConnection
import java.sql.Connection
import javax.sql.DataSource

val diContainer =
    DI {
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
        bindProvider<Connection> {
            val dataSource: DataSource by di.instance()
            dataSource.connection
        }
    }
