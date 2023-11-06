package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.model.ContactId
import com.github.clasicrando.datasources.model.DataSourceContact
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.jasync.query.sqlCommand
import com.github.clasicrando.requests.ModifyDataSourceContactRequest
import com.github.jasync.sql.db.Connection
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class PgDataSourceContactsDao(override val di: DI) : DIAware, DataSourceContactsDao {
    private val connection: Connection by di.instance()

    override suspend fun create(
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    ) {
        sqlCommand("call em.create_data_source_contact(?, ?, ?, ?, ?, ?)")
            .bind(dsId)
            .bind(request.name)
            .bind(request.email.takeIf { it.isNotBlank() })
            .bind(request.website.takeIf { it.isNotBlank() })
            .bind(request.type.takeIf { it.isNotBlank() })
            .bind(request.notes.takeIf { it.isNotBlank() })
            .execute(connection)
    }

    override suspend fun delete(
        contactId: ContactId,
        dsId: DsId,
    ) {
        sqlCommand("call em.delete_data_source_contact(?, ?)")
            .bind(contactId)
            .bind(dsId)
            .execute(connection)
    }

    override suspend fun getByDsId(dsId: DsId): List<DataSourceContact> {
        return sqlCommand(
            """
            select
                dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type,
                dsc.notes
            from em.v_data_source_contacts dsc
            where dsc.ds_id = ?
            """.trimIndent(),
        )
            .bind(dsId)
            .query<DataSourceContact>(connection)
    }

    override suspend fun getById(contactId: ContactId): DataSourceContact? {
        return sqlCommand(
            """
            select
                dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type,
                dsc.notes
            from em.v_data_source_contacts dsc
            where dsc.contact_id = ?
            """.trimIndent(),
        )
            .bind(contactId)
            .querySingleOrNull(connection)
    }

    override suspend fun update(
        contactId: ContactId,
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    ) {
        sqlCommand("call em.update_data_source_contact(?, ?, ?, ?, ?, ?, ?)")
            .bind(contactId)
            .bind(dsId)
            .bind(request.name)
            .bind(request.email.takeIf { it.isNotBlank() })
            .bind(request.website.takeIf { it.isNotBlank() })
            .bind(request.type.takeIf { it.isNotBlank() })
            .bind(request.notes.takeIf { it.isNotBlank() })
            .execute(connection)
    }
}
