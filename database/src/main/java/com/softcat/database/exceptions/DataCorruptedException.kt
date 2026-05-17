package com.softcat.database.exceptions

class DataCorruptedException(
    msg: String
): Exception("Data is corrupted: $msg")