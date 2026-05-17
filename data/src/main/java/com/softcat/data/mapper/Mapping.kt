package com.softcat.data.mapper

import com.softcat.database.facade.DatabaseFacade
import com.softcat.database.models.IngredientDbModel
import com.softcat.database.models.RecipeDbModel
import com.softcat.database.models.ScoreDbModel
import com.softcat.database.models.UserDbModel
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.NutritionData
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.entities.Score
import com.softcat.domain.entities.User
import java.util.Calendar
import javax.inject.Inject
import kotlin.text.split

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

class RecipeMapper @Inject constructor(
    private val database: DatabaseFacade
) {

    suspend fun toEntities(recipeModels: List<RecipeDbModel>): List<Recipe> {
        val ingredientMap = database
            .getIngredients(limit = 100000)
            .associateBy { ingredient -> ingredient.id }
        val tagMap = database
            .getTags(limit = 100000)
            .associateBy { tag -> tag.id }

        return recipeModels.map { model ->
            val ingredients = model.ingredients
                .split(",")
                .mapNotNull { id -> ingredientMap[id.toInt()] }
                .map { it.toEntity() }

            val tags = model.tags
                .split(",")
                .mapNotNull { id -> tagMap[id.toInt()] }
                .map { RecipeTag(it.name) }

            Recipe(
                id = model.id,
                name = model.name,
                description = model.description,
                languageTag = "EN",
                isCooked = model.isCooked,
                ingredients = ingredients,
                tags = tags,
                steps = model.steps.split("|"),
                minutes = model.minutes,
                nutrition = NutritionData(
                    calories = model.calories,
                    fat = model.fat,
                    sugar = model.sugar,
                    sodium = model.sodium,
                    protein = model.protein,
                    saturatedFat = model.saturatedFat,
                    carbohydrates = model.carbohydrates
                ),
            )
        }
    }
}
