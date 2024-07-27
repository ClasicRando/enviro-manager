package com.github.clasicrando.pipelines.data.postgres

import com.github.clasicrando.pipelines.data.PipelineRunDao
import com.github.clasicrando.pipelines.model.PipelineRun
import com.github.clasicrando.pipelines.model.PipelineRunMin
import com.github.clasicrando.pipelines.model.RunId
import com.github.clasicrando.pipelines.model.TaskQueueItem
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.intellij.lang.annotations.Language
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgPipelineRunDao(
    override val di: DI,
) : DIAware,
    PipelineRunDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun getRuns(): List<PipelineRunMin> =
        pool.useConnection { conn ->
            conn.createPreparedQuery(GET_RUNS).fetchAll(PipelineRunMin)
        }

    override suspend fun getRun(runId: RunId): PipelineRun? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_RUN)
                .bind(runId.value)
                .fetchFirst(PipelineRun)
        }

    override suspend fun getWorkflowRunData(
        runId: RunId,
        pipelineState: String,
    ): List<TaskQueueItem> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_RUN_WORKFLOW_RUN_DATA)
                .bind(runId.value)
                .bind(pipelineState)
                .fetchAll(TaskQueueItem)
        }
}

@Language("POSTGRES-SQL")
private val GET_RUNS =
    """
    SELECT
        run_id, data_source_code, record_date, collection_user, load_user, check_user,
        qa_user, current_pipeline_state, is_active
    FROM pipeline.v_pipeline_runs
    """.trimIndent()

@Language("POSTGRES-SQL")
private val GET_RUN =
    """
    SELECT
        run_id, ds_id, data_source_code, record_date, collection_user, load_user,
        check_user, qa_user, current_pipeline_state, is_active, production_count,
        staging_count, match_count, new_count, plotting_stats, merge_type
    FROM pipeline.v_pipeline_runs
    WHERE run_id = $1
    """.trimIndent()

@Language("POSTGRES-SQL")
private val GET_RUN_WORKFLOW_RUN_DATA =
    """
    SELECT
        tq.task_order, t.name as task_name, tq.status, tq.input_parameters, tq.output_parameters,
        tq.output, tq.rules, tq.task_start, tq.task_end, tq.progress
    FROM pipeline.pr_workflow_runs pr
    JOIN workflow_engine.task_queue tq ON pr.workflow_run_id = tq.workflow_run_id
    JOIN workflow_engine.tasks t ON tq.task_id = t.task_id
    WHERE
        pr.run_id = $1
        AND pr.pipeline_state = $2
    ORDER by tq.task_order
    """.trimIndent()
