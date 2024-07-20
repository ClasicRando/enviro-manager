package com.github.clasicrando.pipeline.data.postgres

import com.github.clasicrando.pipeline.data.PipelineRunDao
import com.github.clasicrando.pipeline.model.PipelineRun
import com.github.clasicrando.pipeline.model.PipelineRunMin
import com.github.clasicrando.pipeline.model.RunId
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
            conn
                .createPreparedQuery(GET_RUNS)
                .fetchAll(PipelineRunMin)
        }

    override suspend fun getRun(runId: RunId): PipelineRun? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(GET_RUN)
                .bind(runId)
                .fetchFirst(PipelineRun)
        }

    companion object {
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
                check_user, qa_user, current_pipeline_state, collection_workflow_id,
                load_workflow_id, check_workflow_id, qa_workflow_id, is_active, production_count,
                staging_count, match_count, new_count, plotting_stats, merge_type
            FROM pipeline.v_pipeline_runs
            WHERE run_id = $1
            """.trimIndent()
    }
}
