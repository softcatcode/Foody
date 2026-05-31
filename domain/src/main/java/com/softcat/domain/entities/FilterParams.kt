package com.softcat.domain.entities

data class FilterParams(
    val ingredients: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
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