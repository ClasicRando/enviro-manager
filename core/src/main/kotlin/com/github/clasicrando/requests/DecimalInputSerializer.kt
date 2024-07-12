package com.github.clasicrando.requests

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class DecimalInputSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            serialName = "DecimalInputSerializer",
            kind = PrimitiveKind.STRING,
        )

    override fun deserialize(decoder: Decoder): Double =
        decoder
            .decodeString()
            .takeIf {
                it.isNotBlank()
            }?.toDoubleOrNull() ?: 0.0

    override fun serialize(
        encoder: Encoder,
        value: Double,
    ) {
        encoder.encodeString(value.toString())
    }
}
