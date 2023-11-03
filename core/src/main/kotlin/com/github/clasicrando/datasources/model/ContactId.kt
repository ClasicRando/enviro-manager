package com.github.clasicrando.datasources.model

import org.snappy.decode.Decoder
import org.snappy.encode.Encode
import org.snappy.rowparse.SnappyRow
import java.sql.PreparedStatement

@JvmInline
value class ContactId(val value: Long) : Encode {
    override fun encode(
        preparedStatement: PreparedStatement,
        parameterIndex: Int,
    ) {
        preparedStatement.setLong(parameterIndex, value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object : Decoder<ContactId> {
        override fun decodeNullable(
            row: SnappyRow,
            fieldName: String,
        ): ContactId? {
            return row.getLongNullable(fieldName)?.let { ContactId(it) }
        }
    }
}
