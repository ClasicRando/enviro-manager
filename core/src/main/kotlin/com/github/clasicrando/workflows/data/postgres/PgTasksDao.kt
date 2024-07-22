package com.github.clasicrando.workflows.data.postgres

import com.github.clasicrando.workflows.data.TasksDao
import com.github.clasicrando.workflows.model.Task
import com.github.clasicrando.workflows.model.TaskId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
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

    override suspend fun createTask(
        name: String,
        description: String,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(INSERT_TASK)
                .bind(name)
                .bind(description)
                .executeClosing()
        }
    }

    override suspend fun updateTask(
        taskId: TaskId,
        name: String,
        description: String,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(UPDATE_TASK)
                .bind(name)
                .bind(description)
                .bind(taskId.value)
                .executeClosing()
        }
    }
}

@Language("POSTGRES-PSQL")
private val GET_TASKS =
    """
    SELECT task_id, name, description
    FROM workflow_engine.tasks
    ORDER BY task_id
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_TASK =
    """
    SELECT task_id, name, description
    FROM workflow_engine.tasks
    WHERE task_id = $1
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val INSERT_TASK =
    """
    INSERT INTO workflow_engine.tasks(name, description)
    VALUES($1, $2)
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val UPDATE_TASK =
    """
    UPDATE workflow_engine.tasks
    SET
        name = $1,
        description = $2
    WHERE task_id = $3
    """.trimIndent()
