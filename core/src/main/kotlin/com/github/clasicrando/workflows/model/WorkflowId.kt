package com.github.clasicrando.workflows.model

import kotlinx.serialization.Serializable
import org.snappy.decode.Decoder
import org.snappy.encode.Encode
import org.snappy.rowparse.SnappyRow
import java.sql.PreparedStatement

@Serializable
@JvmInline
value class WorkflowId(val value: Long) : Encode {
    override fun encode(
        preparedStatement: PreparedStatement,
        parameterIndex: Int,
    ) {
        preparedStatement.setLong(parameterIndex, value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object : Decoder<WorkflowId> {
        override fun decodeNullable(
            row: SnappyRow,
            fieldName: String,
        ): WorkflowId? {
            return row.getLongNullable(fieldName)?.let { WorkflowId(it) }
        }
    }
}
