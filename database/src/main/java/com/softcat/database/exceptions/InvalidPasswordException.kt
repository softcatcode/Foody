package com.softcat.database.exceptions

class InvalidPasswordException(
    email: String,
    password: String
): Exception("Password '$password' is invalid for user with email = '$email'") {
}