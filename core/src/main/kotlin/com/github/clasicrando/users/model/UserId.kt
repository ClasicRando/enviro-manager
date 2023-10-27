package com.github.clasicrando.users.model

import org.snappy.decode.Decoder
import org.snappy.encode.Encode
import org.snappy.rowparse.SnappyRow
import org.snappy.rowparse.getObjectNullable
import java.sql.PreparedStatement
import java.util.UUID

@JvmInline
value class UserId(val value: UUID) : Encode {
    override fun encode(
        preparedStatement: PreparedStatement,
        parameterIndex: Int,
    ) {
        preparedStatement.setObject(parameterIndex, value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object : Decoder<UserId> {
        override fun decodeNullable(
            row: SnappyRow,
            fieldName: String,
        ): UserId? {
            return row.getObjectNullable<UUID>(fieldName)?.let { UserId(it) }
        }
    }
}
