package com.github.clasicrando.datasources.model

import com.github.clasicrando.jasync.literal.compositeLiteralBuilder
import com.github.clasicrando.jasync.literal.parseComposite
import com.github.clasicrando.jasync.type.TypeParser
import kotlin.reflect.KClass

object DataSourceContactTypeParser : TypeParser<DataSourceContact> {
    override val type: KClass<DataSourceContact> = DataSourceContact::class
    override val typeName: String = "em.data_source_contacts"
    override val hasArrayType: Boolean = true

    override fun decodeFromString(value: String): DataSourceContact? {
        return parseComposite(value) {
            val contactId =
                readLong()?.let { ContactId(it) }
                    ?: error("Parameter 'contactId' cannot be null")
            val dsId =
                readLong()?.let { DsId(it) }
                    ?: error("Parameter 'dsId' cannot be null")
            val name = readString() ?: error("Parameter 'name' cannot be null")
            val email = readString()
            val website = readString()
            val type = readString()
            val notes = readString()
            DataSourceContact(
                contactId,
                dsId,
                name,
                email,
                website,
                type,
                notes,
            )
        }
    }

    override fun DataSourceContact.encodeToString(): String {
        return compositeLiteralBuilder {
            appendLong(this@encodeToString.contactId.value)
            appendLong(this@encodeToString.dsId.value)
            appendString(this@encodeToString.name)
            appendString(this@encodeToString.email)
            appendString(this@encodeToString.website)
            appendString(this@encodeToString.type)
            appendString(this@encodeToString.notes)
        }
    }
}
