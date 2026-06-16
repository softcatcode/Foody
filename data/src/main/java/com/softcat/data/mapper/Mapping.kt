package com.softcat.data.mapper

import com.softcat.database.models.IngredientDbModel
import com.softcat.database.models.ScoreDbModel
import com.softcat.database.models.UserDbModel
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.Score
import com.softcat.domain.entities.User
import java.util.Calendar

fun ScoreDbModel.toEntity() = Score(
    recipeId = recipeId,
    value = value,
    date = Calendar.getInstance().apply { timeInMillis = date * 1000L }
)

fun List<ScoreDbModel>.toListEntity() = map { it.toEntity() }

fun Score.toDbModel() = ScoreDbModel(
    recipeId = recipeId,
    value = value,
    date = date.timeInMillis / 1000L
)

fun UserDbModel.toEntity() = User(
    id = id,
    email = email,
    name = name,
    registerDate = Calendar.getInstance().apply { timeInMillis = registerDate * 1000L }
)

fun User.toDbModel() = UserDbModel(
    id = id,
    email = email,
    name = name,
    registerDate = registerDate.timeInMillis / 1000L
)

fun IngredientDbModel.toEntity() = Ingredient(
    id = id,
    name = name
)
