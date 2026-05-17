package com.softcat.database.exceptions

class UserNotFoundException(
    email: String
): Exception("User with email = '$email' is not found")