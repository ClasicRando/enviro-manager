package com.github.clasicrando.di

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.data.DataSourcesDao
import com.github.clasicrando.datasources.data.RecordWarehouseTypesDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourceContactsDao
import com.github.clasicrando.datasources.data.postgres.PgDataSourcesDao
import com.github.clasicrando.datasources.data.postgres.PgRecordWarehouseTypeDao
import com.github.clasicrando.pipelines.data.PipelineRunDao
import com.github.clasicrando.pipelines.data.PipelineStateDao
import com.github.clasicrando.pipelines.data.postgres.PgPipelineRunDao
import com.github.clasicrando.pipelines.data.postgres.PgPipelineStateDao
import com.github.clasicrando.pipelines.model.MergeType
import com.github.clasicrando.regions.data.PgRegionsDao
import com.github.clasicrando.regions.data.RegionsDao
import com.github.clasicrando.users.data.PgUsersDao
import com.github.clasicrando.users.data.UsersDao
import com.github.clasicrando.workflows.data.TasksDao
import com.github.clasicrando.workflows.data.WorkflowTasksDao
import com.github.clasicrando.workflows.data.WorkflowsDao
import com.github.clasicrando.workflows.data.postgres.PgTasksDao
import com.github.clasicrando.workflows.data.postgres.PgWorkflowTasksDao
import com.github.clasicrando.workflows.data.postgres.PgWorkflowsDao
import com.github.clasicrando.workflows.model.ScheduleEntry
import com.github.clasicrando.workflows.model.TaskRule
import com.github.clasicrando.workflows.model.WorkflowRunStatus
import com.github.clasicrando.workflows.model.WorkflowTaskComposite
import io.github.clasicrando.kdbc.core.pool.PoolOptions
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.postgresql.connection.PgConnectOptions
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.kodein.di.DI
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindProvider
import org.kodein.di.instance
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
            applicationName = "EnviroManager Web",
            connectionTimeout = 5.toDuration(DurationUnit.SECONDS),
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
    bindProvider<RegionsDao> {
        PgRegionsDao(di)
    }
    bindProvider<PipelineStateDao> {
        PgPipelineStateDao(di)
    }
    bindProvider<PipelineRunDao> {
        PgPipelineRunDao(di)
    }
    bindProvider<TasksDao> {
        PgTasksDao(di)
    }
    bindProvider<WorkflowTasksDao> {
        PgWorkflowTasksDao(di)
    }
}

suspend fun DI.cleanUpResources() {
    val connectionPool by this.instance<PgAsyncConnectionPool>()
    connectionPool.close()
}

suspend fun DI.registerTypes() {
    val connectionPool by di.instance<PgAsyncConnectionPool>()
    connectionPool.useConnection {
        it.registerEnumType<MergeType>("pipeline.merge_type")
//        it.registerEnumType<TaskStatus>("workflow_engine.task_status")
        it.registerEnumType<WorkflowRunStatus>("workflow_engine.workflow_run_status")
        it.registerCompositeType<ScheduleEntry>("workflow_engine.schedule_entry")
        it.registerCompositeType<TaskRule>("workflow_engine.task_rule")
        it.registerCompositeType<WorkflowTaskComposite>("workflow_engine.workflow_tasks")
    }
}
