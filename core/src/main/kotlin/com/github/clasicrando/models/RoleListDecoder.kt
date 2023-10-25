package com.github.clasicrando.models

import org.snappy.decode.Decoder
import org.snappy.postgresql.array.toList
import org.snappy.rowparse.SnappyRow

class RoleListDecoder : Decoder<List<Role>> {
    override fun decodeNullable(
        row: SnappyRow,
        fieldName: String,
    ): List<Role>? {
        return row.getArray(fieldName)
            .toList<String>()
            ?.map { roleName ->
                Role.entries.firstOrNull { it.dbValue == roleName }
                    ?: error("Could not decode '$roleName' into Role")
            }
    }
}
