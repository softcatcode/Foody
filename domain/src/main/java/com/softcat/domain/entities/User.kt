package com.softcat.domain.entities

import com.softcat.domain.serializers.CalendarSerializer
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    @Serializable(with = CalendarSerializer::class)
    val registerDate: Calendar
)