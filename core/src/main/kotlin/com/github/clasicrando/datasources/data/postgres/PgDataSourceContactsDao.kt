package com.github.clasicrando.datasources.data.postgres

import com.github.clasicrando.datasources.data.DataSourceContactsDao
import com.github.clasicrando.datasources.model.ContactId
import com.github.clasicrando.datasources.model.DataSourceContact
import com.github.clasicrando.datasources.model.DsId
import com.github.clasicrando.requests.ModifyDataSourceContactRequest
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.snappy.command.sqlCommand
import org.snappy.extensions.useConnection

class PgDataSourceContactsDao(override val di: DI) : DIAware, DataSourceContactsDao {
    private val dataSource: javax.sql.DataSource by di.instance()

    override suspend fun create(
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    ) {
        dataSource.useConnection {
            sqlCommand("call em.create_data_source_contact(?, ?, ?, ?, ?, ?)")
                .bind(dsId)
                .bind(request.name)
                .bind(request.email.takeIf { it.isNotBlank() })
                .bind(request.website.takeIf { it.isNotBlank() })
                .bind(request.type.takeIf { it.isNotBlank() })
                .bind(request.notes.takeIf { it.isNotBlank() })
                .executeSuspend(this)
        }
    }

    override suspend fun delete(contactId: ContactId, dsId: DsId) {
        dataSource.useConnection {
            sqlCommand("call em.delete_data_source_contact(?, ?)")
                .bind(contactId)
                .bind(dsId)
                .executeSuspend(this)
        }
    }

    override suspend fun getByDsId(dsId: DsId): List<DataSourceContact> {
        return dataSource.useConnection {
            sqlCommand(
                """
                    select
                        dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type,
                        dsc.notes
                    from em.v_data_source_contacts dsc
                    where dsc.ds_id = ?
                """.trimIndent()
            )
                .bind(dsId)
                .querySuspend<DataSourceContact>(this)
                .toList()
        }
    }

    override suspend fun getById(contactId: ContactId): DataSourceContact? {
        return dataSource.useConnection {
            sqlCommand(
                """
                    select
                        dsc.contact_id, dsc.ds_id, dsc.name, dsc.email, dsc.website, dsc.type,
                        dsc.notes
                    from em.v_data_source_contacts dsc
                    where dsc.contact_id = ?
                """.trimIndent()
            )
                .bind(contactId)
                .querySingleOrNullSuspend(this)
        }
    }

    override suspend fun update(
        contactId: ContactId,
        dsId: DsId,
        request: ModifyDataSourceContactRequest,
    ) {
        dataSource.useConnection {
            sqlCommand("call em.update_data_source_contact(?, ?, ?, ?, ?, ?, ?)")
                .bind(contactId)
                .bind(dsId)
                .bind(request.name)
                .bind(request.email.takeIf { it.isNotBlank() })
                .bind(request.website.takeIf { it.isNotBlank() })
                .bind(request.type.takeIf { it.isNotBlank() })
                .bind(request.notes.takeIf { it.isNotBlank() })
                .executeSuspend(this)
        }
    }
}
