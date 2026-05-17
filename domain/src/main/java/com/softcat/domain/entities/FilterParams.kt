package com.softcat.domain.entities

data class FilterParams(
    val reqTags: List<String> = emptyList(),
    val reqIngredients: List<String> = emptyList(),
    val maxAbsentCount: Int = Int.MAX_VALUE,
    val duration: ClosedFloatingPointRange<Float> = 0f..MAX_DURATION,
    val calories: ClosedFloatingPointRange<Float> = 0f..MAX_CALORIES,
    val minScore: Int = 1,
    val isCooked: TripleChoice = TripleChoice.NotImportant
) {
    enum class TripleChoice {
        Yes,
        NotImportant,
        No
    }

    companion object {
        const val MAX_CALORIES = 10000f
        const val MAX_DURATION = 180f
    }
}