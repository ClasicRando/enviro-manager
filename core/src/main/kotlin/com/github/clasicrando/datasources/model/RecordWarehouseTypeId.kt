package com.github.clasicrando.datasources.model

import kotlinx.serialization.Serializable
import org.snappy.decode.Decoder
import org.snappy.encode.Encode
import org.snappy.rowparse.SnappyRow
import java.sql.PreparedStatement

@Serializable
@JvmInline
value class RecordWarehouseTypeId(val value: Short) : Encode {
    override fun encode(
        preparedStatement: PreparedStatement,
        parameterIndex: Int,
    ) {
        preparedStatement.setShort(parameterIndex, value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object : Decoder<RecordWarehouseTypeId> {
        override fun decodeNullable(
            row: SnappyRow,
            fieldName: String,
        ): RecordWarehouseTypeId? {
            return row.getShortNullable(fieldName)?.let { RecordWarehouseTypeId(it) }
        }
    }
}
