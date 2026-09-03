package com.softcat.foody.screens.fridge

import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.IngredientCategory
import com.softcat.foody.R
import com.softcat.foody.ui.theme.BaseOrange
import com.softcat.foody.ui.theme.Pink
import com.softcat.foody.ui.theme.Purple
import com.softcat.foody.ui.theme.SoftBlue
import com.softcat.foody.ui.theme.Turquoise
import com.softcat.foody.ui.theme.Vinous
import kotlin.collections.forEach

class IngredientCategoryToIngredientCardMapper {
    private fun getTitleResId(category: IngredientCategory) = when (category) {
        IngredientCategory.Crops -> R.string.fridge_category_crops
        IngredientCategory.Dairy -> R.string.fridge_category_dairy
        IngredientCategory.MeatAndFish -> R.string.fridge_category_meat_fish
        IngredientCategory.Sweet -> R.string.fridge_category_sweet
        IngredientCategory.SpiceAndSauce -> R.string.fridge_category_spice_sauce
        IngredientCategory.FruitAndVegetables -> R.string.fridge_category_fruit_vegetables
        IngredientCategory.Other -> R.string.fridge_category_other
    }

    private fun getIconResId(category: IngredientCategory) = when (category) {
        IngredientCategory.Crops -> R.drawable.crops
        IngredientCategory.Dairy -> R.drawable.milk
        IngredientCategory.MeatAndFish -> R.drawable.meat
        IngredientCategory.Sweet -> R.drawable.icecream
        IngredientCategory.SpiceAndSauce -> R.drawable.spice
        IngredientCategory.FruitAndVegetables -> R.drawable.vegetables
        IngredientCategory.Other -> R.drawable.blue_octopus
    }

    private fun getColor(category: IngredientCategory) = when (category) {
        IngredientCategory.Crops -> BaseOrange
        IngredientCategory.Dairy -> Purple
        IngredientCategory.MeatAndFish -> Pink
        IngredientCategory.Sweet -> Turquoise
        IngredientCategory.SpiceAndSauce -> Vinous
        IngredientCategory.FruitAndVegetables -> BaseOrange
        IngredientCategory.Other -> SoftBlue
    }

    fun mapIngredientsToCategories(ingredients: List<Ingredient>): List<FridgeStore.State.IngredientCategoryCard> {
        val categories = mutableMapOf<IngredientCategory, MutableList<Ingredient>>()
        ingredients.forEach { elem ->
            if (elem.category !in categories)
                categories[elem.category] = mutableListOf()
            categories[elem.category]?.add(elem)
        }
        val result = mutableListOf<FridgeStore.State.IngredientCategoryCard>()
        categories.forEach { (key, value) ->
            val card = FridgeStore.State.IngredientCategoryCard(
                titleResId = getTitleResId(key),
                iconResId = getIconResId(key),
                color = getColor(key),
                names = value.map { it.name }
            )
            result.add(card)
        }
        return result
    }
}