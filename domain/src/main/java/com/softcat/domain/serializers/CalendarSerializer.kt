package com.softcat.domain.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Calendar

object CalendarSerializer : KSerializer<Calendar> {
    override val descriptor = PrimitiveSerialDescriptor("Calendar", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Calendar) {
        encoder.encodeLong(value.timeInMillis)
    }

    override fun deserialize(decoder: Decoder): Calendar {
        val millis = decoder.decodeLong()
        return Calendar.getInstance().apply { timeInMillis = millis }
    }
}