package com.softcat.foody.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RecipeGrid(
    modifier: Modifier = Modifier,
    onRecipeClick: (Int) -> Unit,
    changeFavouriteStatus: (Int) -> Unit,
    recipes: List<RecipeModel>,
    topPadding: Dp = 0.dp
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp)
    ) {
        item {
            Spacer(Modifier.height(topPadding))
        }
        item {
            Spacer(Modifier.height(topPadding))
        }
        itemsIndexed(items = recipes, key = { _, item -> item.id }) { index, recipe ->
            RecipeCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .padding(4.dp),
                recipe = recipe,
                index = index,
                onFavouriteClick = { changeFavouriteStatus(recipe.id) },
                onClick = onRecipeClick
            )
        }
    }
}