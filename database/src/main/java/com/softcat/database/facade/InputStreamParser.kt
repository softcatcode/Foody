package com.softcat.database.facade

import com.softcat.database.models.RecipeDbModel
import java.io.EOFException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

internal fun InputStream.readRecipe() = RecipeDbModel(
    id = readInt32LE(),
    name = readString(),
    description = readString(),
    steps = readString(),

    ingredients = List(readInt32LE()) {
        readInt32LE()
    }.joinToString(","),

    tags = List(readInt32LE()) {
        readInt32LE()
    }.joinToString(","),

    isCooked = false,
    minutes = readInt32LE(),
    calories = readFloat32LE(),
    fat = readFloat32LE(),
    sugar = readFloat32LE(),
    sodium = readFloat32LE(),
    protein = readFloat32LE(),
    saturatedFat = readFloat32LE(),
    carbohydrates = readFloat32LE()
)

internal fun InputStream.readInt32LE(): Int {
    val b0 = read()
    val b1 = read()
    val b2 = read()
    val b3 = read()
    return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
}

internal fun InputStream.readFloat32LE(): Float {
    val buffer = readFully(4)
    return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).float
}

internal fun InputStream.readString(): String {
    val length = readInt32LE()
    if (length <= 0) return ""
    val buffer = readFully(length)
    return String(buffer, StandardCharsets.UTF_8)
}

private fun InputStream.readFully(n: Int): ByteArray {
    val buffer = ByteArray(n)
    var totalRead = 0
    while (totalRead < n) {
        val bytesRead = read(buffer, totalRead, n - totalRead)
        if (bytesRead == -1) {
            throw EOFException("Unexpected end of stream: read $totalRead of $n bytes")
        }
        totalRead += bytesRead
    }
    return buffer
}