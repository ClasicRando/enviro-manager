package com.github.clasicrando.requests

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@Serializable
@JvmInline
value class UnknownNumberOfItems<T>(
    @Serializable(with = UnknownNumberOfItemsSerialize::class)
    private val items: List<T>,
) : Iterable<T> {
    val size: Int get() = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    operator fun get(index: Int): T = items[index]

    override fun iterator(): Iterator<T> = items.iterator()
}

@Suppress("UNCHECKED_CAST")
class UnknownNumberOfItemsSerialize<T>(
    private val dataSerializer: KSerializer<T>,
) : JsonContentPolymorphicSerializer<List<T>>(List::class as KClass<List<T>>) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<List<T>> =
        if (element is JsonArray) {
            ListSerializer(dataSerializer)
        } else {
            SingleItemAsList(dataSerializer)
        }

    class SingleItemAsList<T>(
        private val dataSerializer: KSerializer<T>,
    ) : KSerializer<List<T>> {
        override val descriptor: SerialDescriptor = dataSerializer.descriptor

        override fun deserialize(decoder: Decoder): List<T> {
            val temp = listOf(dataSerializer.deserialize(decoder))
            return temp
        }

        override fun serialize(
            encoder: Encoder,
            value: List<T>,
        ) {
            error("Should not be used")
        }
    }
}
