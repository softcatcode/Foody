package com.softcat.database.models

import androidx.room.TypeConverter
import java.nio.ByteBuffer

class VectorConverters {
    @TypeConverter
    fun fromVector(vector: List<Float>): ByteArray {
        val buffer = ByteBuffer.allocate(vector.size * 4)
        vector.forEach { buffer.putFloat(it) }
        return buffer.array()
    }

    @TypeConverter
    fun toVector(data: ByteArray): List<Float> {
        val buffer = ByteBuffer.wrap(data)
        return List(data.size / 4) { buffer.float }
    }
}