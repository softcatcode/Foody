package com.softcat.database.exceptions

class ScoreIsAbsentException(
    userId: String,
    recipeId: Int
): Exception("No scores found for user=$userId and recipe=$recipeId")

class ScoresNodeIsAbsentException(
    userId: String,
): Exception("No scores found for user=$userId")