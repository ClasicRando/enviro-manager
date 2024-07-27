package com.github.clasicrando.pipelines.data.postgres

import com.github.clasicrando.pipelines.data.PipelineStateDao
import com.github.clasicrando.pipelines.model.PipelineState
import com.github.clasicrando.pipelines.model.RunId
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.intellij.lang.annotations.Language
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgPipelineStateDao(
    override val di: DI,
) : DIAware,
    PipelineStateDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getAll(): List<PipelineState> =
        pool.useConnection { conn ->
            conn.createPreparedQuery(GET_PIPELINE_STATES).fetchAll(PipelineState)
        }

    override suspend fun getPipelineStates(runId: RunId): List<PipelineState> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_PIPELINE_RUN_PIPELINE_STATES)
                .bind(runId.value)
                .fetchAll(PipelineState)
        }
}

@Language("POSTGRES-PSQL")
private val GET_PIPELINE_STATES =
    """
    SELECT code, name, href, workflow_order
    FROM pipeline.pipeline_states
    ORDER BY workflow_order
    """.trimIndent()

@Language("POSTGRES-PSQL")
private val GET_PIPELINE_RUN_PIPELINE_STATES =
    """
    SELECT ps.code, ps.name, ps.href, ps.workflow_order
    FROM pipeline.pipeline_states ps
    JOIN pipeline.pr_workflow_runs pr ON pr.pipeline_state = ps.code
    WHERE pr.run_id = $1
    ORDER BY ps.workflow_order
    """.trimIndent()
