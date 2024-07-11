package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.model.ContactId
import com.github.clasicrando.datasources.model.DataSourceContact
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.ModifyDataSourceContactRequest
import io.github.clasicrando.kdbc.core.pool.useConnection
import io.github.clasicrando.kdbc.core.query.bind
import io.github.clasicrando.kdbc.core.query.executeClosing
import io.github.clasicrando.kdbc.core.query.fetchAll
import io.github.clasicrando.kdbc.core.query.fetchFirst
import io.github.clasicrando.kdbc.postgresql.pool.PgAsyncConnectionPool
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgDataSourceContactsDao(
    override val di: DI,
) : DIAware,
    DataSourceContactsDao {
    private val pool: PgAsyncConnectionPool by di.instance()

    override suspend fun create(
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    insert into em.data_source_contacts (ds_id, name, email, website, type, notes)
                    values ($1, $2, $3, $4, $5, $6)
                    """.trimIndent(),
                ).bind(dsId.value)
                .bind(request.name.trim())
                .bind(request.email.trim().takeIf { it.isNotBlank() })
                .bind(request.website.trim().takeIf { it.isNotBlank() })
                .bind(request.type.trim().takeIf { it.isNotBlank() })
                .bind(request.notes.trim().takeIf { it.isNotBlank() })
                .executeClosing()
        }
    }

    override suspend fun delete(
        contactId: ContactId,
        dsId: DsId,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    delete from em.data_source_contacts
                    where
                        contact_id = $1
                        and ds_id = $2
                    """.trimIndent(),
                ).bind(contactId.value)
                .bind(dsId.value)
                .execute()
        }
    }

    override suspend fun getByDsId(dsId: DsId): List<DataSourceContact> =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select
                        dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type,
                        dsc.notes
                    from em.v_data_source_contacts dsc
                    where dsc.ds_id = $1
                    """.trimIndent(),
                ).bind(dsId.value)
                .fetchAll(DataSourceContact)
        }

    override suspend fun getById(contactId: ContactId): DataSourceContact? =
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    select
                        dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type,
                        dsc.notes
                    from em.v_data_source_contacts dsc
                    where dsc.contact_id = $1
                    """.trimIndent(),
                ).bind(contactId.value)
                .fetchFirst(DataSourceContact)
        }

    override suspend fun update(
        contactId: ContactId,
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    ) {
        pool.useConnection { conn ->
            conn
                .createPreparedQuery(
                    """
                    update em.data_source_contacts
                    set
                        name = $3,
                        email = $4,
                        website = $5,
                        type = $6,
                        notes = $7
                    where
                        contact_id = $1
                        and ds_id = $2
                    """.trimIndent(),
                ).bind(contactId.value)
                .bind(dsId.value)
                .bind(request.name.trim())
                .bind(request.email.trim().takeIf { it.isNotBlank() })
                .bind(request.website.trim().takeIf { it.isNotBlank() })
                .bind(request.type.trim().takeIf { it.isNotBlank() })
                .bind(request.notes.trim().takeIf { it.isNotBlank() })
                .executeClosing()
        }
    }
}
