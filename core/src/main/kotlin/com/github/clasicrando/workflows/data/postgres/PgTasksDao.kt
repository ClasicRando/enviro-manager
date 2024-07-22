package com.github.clasicrando.workflows.data.postgres

import com.github.clasicrando.workflows.data.TasksDao
import com.github.clasicrando.workflows.model.Task
import com.github.clasicrando.workflows.model.TaskId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.intellij.lang.annotations.Language
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgTasksDao(
    override val di: DI,
) : DIAware,
    TasksDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getTasks(): List<Task> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_TASKS)
                .fetchAll(Task)
        }

    override suspend fun getTaskById(taskId: TaskId): Task? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_TASK)
                .bind(taskId.value)
                .fetchFirst(Task)
        }
}

@Language("POSTGRES-PSQL")
private val GET_TASKS =
    """
    SELECT task_id, name, description
    FROM workflow_engine.tasks
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_TASK =
    """
    SELECT task_id, name, description
    FROM workflow_engine.tasks
    WHERE task_id = $1
    """.trimIndent()
