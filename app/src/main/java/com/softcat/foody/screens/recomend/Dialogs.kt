package com.softcat.foody.screens.recomend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.RecipeTag
import com.softcat.foody.R
import com.softcat.foody.common.SearchLine
import com.softcat.foody.common.SimpleIngredientCard
import com.softcat.foody.common.SimpleTagCard
import com.softcat.foody.ui.theme.FoodyTheme

@Composable
fun AddTagsDialog(
    state: RecommendStore.State.SelectTagDialogState,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    searchTags: (String) -> Unit,
    addTag: (RecipeTag) -> Unit
) {
    if (state is RecommendStore.State.SelectTagDialogState.Shown) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 128.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SearchLine(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.query,
                        onValueChange = {
                            onQueryChange(it)
                            searchTags(it)
                        },
                        onSearchSubmitted = searchTags,
                        placeholderText = stringResource(R.string.search_tag),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(min = 64.dp, max = 256.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = state.searchResult,
                            key = { it.name }
                        ) { tag ->
                            SimpleTagCard(
                                modifier = Modifier,
                                isActive = true,
                                name = tag.name,
                                onClick = { addTag(tag) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddIngredientsDialog(
    state: RecommendStore.State.SelectIngredientDialogState,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    searchIngredients: (String) -> Unit,
    addIngredient: (Ingredient) -> Unit
) {
    if (state is RecommendStore.State.SelectIngredientDialogState.Shown) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 128.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SearchLine(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.query,
                        onValueChange = {
                            onQueryChange(it)
                            searchIngredients(it)
                        },
                        onSearchSubmitted = searchIngredients,
                        placeholderText = stringResource(R.string.search_ingredient),
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(min = 64.dp, max = 256.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = state.searchResult,
                            key = { it.name }
                        ) { tag ->
                            SimpleIngredientCard(
                                modifier = Modifier,
                                isActive = true,
                                name = tag.name,
                                onClick = { addIngredient(tag) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun AddTagsDialog_Preview() {
    val state = RecommendStore.State.SelectTagDialogState.Shown(
        query = "min",
        searchResult = listOf(
            RecipeTag("15-minutes-or-less"),
            RecipeTag("30-minutes-or-less"),
            RecipeTag("1-minute")
        )
    )
    FoodyTheme {
        AddTagsDialog(
            state = state,
            onDismiss = {},
            onQueryChange = {},
            searchTags = {},
            addTag = {},
        )
    }
}

@Composable
@Preview
private fun AddIngredientsDialog_Preview() {
    val state = RecommendStore.State.SelectIngredientDialogState.Shown(
        query = "to",
        searchResult = listOf(
            Ingredient(
                id = 1,
                name = "tomato"
            ),
        )
    )
    FoodyTheme {
        AddIngredientsDialog(
            state = state,
            onDismiss = {},
            onQueryChange = {},
            searchIngredients = {},
            addIngredient = {},
        )
    }
}