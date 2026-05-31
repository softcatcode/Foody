package com.softcat.foody.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.foody.R
import com.softcat.foody.ui.theme.FoodyTheme

@Composable
private fun IngredientCard(
    modifier: Modifier = Modifier,
    gradient: Brush,
    label: String
) {
    Card(
        modifier = modifier
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .background(gradient)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = White
        )
    }
}

@Composable
private fun RecipeData(
    modifier: Modifier = Modifier,
    recipe: RecipeModel,
    ingredientGradient: Brush
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(bottom = 2.dp)
            .then(modifier)
    ) {
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = recipe.name,
                style = MaterialTheme.typography.labelLarge,
                color = White,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            if (recipe.scoreVisible) {
                RecipeScore(
                    modifier = Modifier,
                    value = recipe.score
                )
            }
        }
        Text(
            modifier = Modifier.heightIn(max = 128.dp),
            text = recipe.description,
            style = MaterialTheme.typography.bodyMedium,
            color = White,
            maxLines = 4
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            recipe.ingredients.forEach {
                IngredientCard(
                    label = it,
                    gradient = ingredientGradient,
                )
            }
        }
    }
}

@Composable
private fun RecipeScore(
    modifier: Modifier = Modifier,
    value: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = White
        )
        Spacer(Modifier.width(4.dp))
        Image(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.star_filled),
            contentDescription = null,
        )
    }
}

@Composable
fun RecipeCard(
    modifier: Modifier = Modifier,
    recipe: RecipeModel,
    index: Int,

    onFavouriteClick: () -> Unit,
    onClick: (Int) -> Unit
) {
    val colors = RecipeCardGradients.getColors(index)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(10),
        onClick = { onClick(recipe.id) }
    ) {
        Box(
            modifier = Modifier
                .background(colors.mainGradient)
                .padding(16.dp)
        ) {
            RecipeData(
                modifier = Modifier.align(Alignment.TopStart).padding(bottom = 32.dp),
                recipe = recipe,
                ingredientGradient = colors.ingredientGradient,
            )
            if (recipe.favouriteButtonVisible) {
                AddToFavouritesButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp),
                    isFavourite = recipe.isFavourite,
                    onClick = onFavouriteClick
                )
            }
        }
    }
}

@Composable
fun SimpleStringValueCard(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    label: String,
    color: Color,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(if (isActive) 2.dp else 0.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) color else White
        ),
        border = BorderStroke(2.dp, color),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) White else color,
            maxLines = 1
        )
    }
}

@Composable
fun SimpleIngredientCard(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    name: String,
    onClick: () -> Unit
) {
    SimpleStringValueCard(
        modifier = modifier,
        isActive = isActive,
        label = name,
        color = MaterialTheme.colorScheme.primary,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun SimpleTagCard(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    name: String = "30-minutes-or-less",
    onClick: () -> Unit = {}
) {
    SimpleStringValueCard(
        modifier = modifier,
        isActive = isActive,
        label = name,
        color = MaterialTheme.colorScheme.secondary,
        onClick = onClick
    )
}

@Composable
@Preview
fun RecipeCard_Preview() {
    FoodyTheme {
        RecipeCard(
            modifier = Modifier,
            recipe = RecipeModel(
                id = 105827,
                name = "Pancakes",
                description = "This is the description of pancakes. It is food for breakfast.",
                ingredients = listOf("oil", "milk", "sugar", "egg", "flour"),
                favouriteButtonVisible = true,
                isFavourite = true,
                score = "3.45",
                scoreVisible = true,
            ),
            index = 2,
            onFavouriteClick = {},
            onClick = {},
        )
    }
}