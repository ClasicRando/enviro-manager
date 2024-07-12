package com.github.clasicrando.requests

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class BooleanSwitchSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            serialName = "BooleanSwitchSerializer",
            kind = PrimitiveKind.STRING,
        )

    override fun deserialize(decoder: Decoder): Boolean =
        decoder.decodeString().equals(other = "on", ignoreCase = true)

    override fun serialize(
        encoder: Encoder,
        value: Boolean,
    ) {
        encoder.encodeString(if (value) "on" else "")
    }
}
