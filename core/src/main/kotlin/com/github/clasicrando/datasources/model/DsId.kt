package com.github.clasicrando.datasources.model

import kotlinx.serialization.Serializable
import org.snappy.decode.Decoder
import org.snappy.encode.Encode
import org.snappy.rowparse.SnappyRow
import java.sql.PreparedStatement

@Serializable
@JvmInline
value class DsId(val value: Long) : Encode {
    override fun encode(
        preparedStatement: PreparedStatement,
        parameterIndex: Int,
    ) {
        preparedStatement.setLong(parameterIndex, value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object : Decoder<DsId> {
        override fun decodeNullable(
            row: SnappyRow,
            fieldName: String,
        ): DsId? {
            return row.getLongNullable(fieldName)?.let { DsId(it) }
        }
    }
}
