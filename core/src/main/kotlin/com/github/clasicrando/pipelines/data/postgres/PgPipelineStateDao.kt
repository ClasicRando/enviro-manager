package com.github.clasicrando.pipelines.data.postgres

import com.github.clasicrando.pipelines.data.PipelineStateDao
import com.github.clasicrando.pipelines.model.PipelineState
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
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
            conn
                .createPreparedQuery(
                    """
                    SELECT code, name, href, workflow_order
                    FROM pipeline.pipeline_states
                    ORDER BY workflow_order
                    """.trimIndent(),
                ).fetchAll(PipelineState)
        }
}
