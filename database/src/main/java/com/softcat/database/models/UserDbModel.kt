package com.softcat.database.models

data class UserDbModel(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val registerDate: Long = 0L
)