package com.softcat.foody.screens.recomend

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.softcat.foody.R
import com.softcat.foody.common.AddToFavouritesButton
import com.softcat.foody.common.ElementsScrollableFlow
import com.softcat.foody.common.RecommendationButton
import com.softcat.foody.common.SimpleAppBar
import com.softcat.foody.screens.recomend.RecommendStore.State.RecommendationStatus
import com.softcat.foody.ui.theme.FoodyTheme

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientsAndTagsSelection(
    modifier: Modifier = Modifier,
    tags: List<String>,
    ingredients: List<String>,
    maxAbsentIngredients: Int,

    addIngredientClicked: () -> Unit,
    addTagClicked: () -> Unit,
    removeIngredientClicked: (String) -> Unit,
    removeTagClicked: (String) -> Unit,
    changeMaxAbsentIngredients: (Int) -> Unit,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxHeight = screenHeight * 0.2f
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        ElementsScrollableFlow(
            modifier = Modifier.heightIn(max = maxHeight),
            elements = ingredients,
            addElementClicked = addIngredientClicked,
            removeElementClicked = removeIngredientClicked,
            color = MaterialTheme.colorScheme.primary,
            title = stringResource(R.string.ingredients),
            iconId = R.drawable.vegetables,
        )
        Spacer(Modifier.height(12.dp))
        ElementsScrollableFlow(
            modifier = Modifier.heightIn(max = maxHeight),
            elements = tags,
            addElementClicked = addTagClicked,
            removeElementClicked = removeTagClicked,
            color = MaterialTheme.colorScheme.secondary,
            title = stringResource(R.string.tags),
            iconId = R.drawable.settings_image,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = stringResource(R.string.max_absent_ingredients),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                )
                Slider(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    value = maxAbsentIngredients.toFloat(),
                    onValueChange = { changeMaxAbsentIngredients(it.toInt()) },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                    ),
                    steps = 10,
                    valueRange = 0f..10f,
                    thumb = {
                        Box(
                            modifier = Modifier.background(Transparent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                        }
                    },
                    track = {
                        Row {
                            Spacer(
                                modifier = Modifier
                                    .height(2.dp)
                                    .weight(maxAbsentIngredients.toFloat() + 0.1f)
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(2.dp)
                                    .weight(10.1f - maxAbsentIngredients.toFloat())
                                    .background(MaterialTheme.colorScheme.tertiary),
                            )
                        }
                    },
                )
                Text(
                    text = maxAbsentIngredients.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun Initial(
    modifier: Modifier = Modifier,
    onRecommendClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            modifier = Modifier.weight(1f),
            painter = painterResource(R.drawable.hat_smile_image),
            contentDescription = null,
        )
        Spacer(Modifier.height(16.dp))
        RecommendationButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            isActive = true,
            onClick = onRecommendClick,
        )
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        AsyncImage(
            modifier = Modifier.size(128.dp),
            model = R.drawable.recommendation_loading_animation,
            contentDescription = null,
        )
        Spacer(Modifier.height(16.dp))
        RecommendationButton(
            modifier = Modifier.fillMaxWidth(),
            isActive = false,
            onClick = {},
        )
    }
}

@Composable
private fun RecommendationCard(
    modifier: Modifier = Modifier,
    recipe: RecipeRecommendationModel,

    onFavouriteButtonClick: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Black,
                    )
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
                Spacer(Modifier.width(16.dp))
            }
            if (recipe.isFavouriteVisible) {
                AddToFavouritesButton(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .align(Alignment.TopEnd)
                        .size(28.dp),
                    isFavourite = recipe.isFavourite,
                    onClick = onFavouriteButtonClick,
                )
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    recipes: List<RecipeRecommendationModel>,

    onRecipeClicked: (Int) -> Unit,
    onFavouriteButtonClicked: (Int, Boolean) -> Unit,
    onRecommendButtonClick: () -> Unit,
) {
    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            items(
                items = recipes,
                key = { it.id },
            ) { recipe ->
                RecommendationCard(
                    modifier = Modifier,
                    recipe = recipe,
                    onClick = { onRecipeClicked(recipe.id) },
                    onFavouriteButtonClick = { isFavourite ->
                        onFavouriteButtonClicked(recipe.id, isFavourite)
                    },
                )
            }
        }
        RecommendationButton(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            isActive = true,
            onClick = onRecommendButtonClick,
        )
    }
}

@Composable
private fun RecommendationResultContent(
    modifier: Modifier = Modifier,
    resultStatus: RecommendationStatus,

    onRecommendButtonClick: () -> Unit,
    onRecipeClicked: (Int) -> Unit,
    onFavouriteButtonClicked: (Int, Boolean) -> Unit,
) {
    when (resultStatus) {
        is RecommendationStatus.Content -> {
            Content(
                modifier = modifier,
                onRecommendButtonClick = onRecommendButtonClick,
                recipes = resultStatus.recipes,
                onRecipeClicked = onRecipeClicked,
                onFavouriteButtonClicked = onFavouriteButtonClicked,
            )
        }

        RecommendationStatus.Initial -> {
            Initial(
                modifier = modifier,
                onRecommendClick = onRecommendButtonClick,
            )
        }

        RecommendationStatus.Loading -> {
            Loading(modifier)
        }
    }
}

@Composable
fun RecommendScreen(component: RecommendComponent) {
    val model = component.model.collectAsState()
    val state = model.value

    RecommendContent(
        state = state,
        onRecommendButtonClick = component::recommend,
        onFavouriteButtonClicked = component::changeFavouriteStatus,
        removeTag = component::removeTag,
        removeIngredient = component::removeIngredient,
        showAddTagDialog = component::showAddTagDialog,
        showAddIngredientDialog = component::showAddIngredientDialog,
        changeMaxAbsentIngredients = component::changeMaxAbsentIngredients,
        openRecipeDetails = component::openRecipeDetails,
    )
    AddTagsDialog(
        state = state.tagDialogState,
        onDismiss = component::hideDialog,
        onQueryChange = component::changeSearchTagQuery,
        searchTags = component::searchTags,
        addTag = component::addTag,
    )
    AddIngredientsDialog(
        state = state.ingredientDialogState,
        onDismiss = component::hideDialog,
        onQueryChange = component::changeSearchIngredientQuery,
        searchIngredients = component::searchIngredients,
        addIngredient = component::addIngredient
    )
}

@Composable
fun RecommendContent(
    state: RecommendStore.State,
    onRecommendButtonClick: () -> Unit,
    onFavouriteButtonClicked: (Int, Boolean) -> Unit,
    openRecipeDetails: (Int) -> Unit,
    removeTag: (String) -> Unit,
    removeIngredient: (String) -> Unit,
    showAddTagDialog: () -> Unit,
    showAddIngredientDialog: () -> Unit,
    changeMaxAbsentIngredients: (Int) -> Unit,
) {
    Scaffold(
        topBar = { SimpleAppBar(stringResource(R.string.recommendations_title)) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            IngredientsAndTagsSelection(
                modifier = Modifier.wrapContentHeight(),
                addIngredientClicked = showAddIngredientDialog,
                addTagClicked = showAddTagDialog,
                removeIngredientClicked = removeIngredient,
                removeTagClicked = removeTag,
                tags = state.tags,
                ingredients = state.ingredients,
                maxAbsentIngredients = state.maxAbsentIngredients,
                changeMaxAbsentIngredients = changeMaxAbsentIngredients,
            )
            Spacer(Modifier.height(6.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .background(MaterialTheme.colorScheme.tertiary),
            )
            Spacer(Modifier.height(6.dp))
            RecommendationResultContent(
                modifier = Modifier.weight(1f),
                resultStatus = state.resultStatus,
                onRecommendButtonClick = onRecommendButtonClick,
                onRecipeClicked = openRecipeDetails,
                onFavouriteButtonClicked = onFavouriteButtonClicked,
            )
        }
    }
}

@Composable
@Preview
private fun Loading_Preview() {
    FoodyTheme {
        Loading()
    }
}

@Composable
@Preview
private fun Initial_Preview() {
    FoodyTheme {
        Initial()
    }
}

@Composable
@Preview
private fun Content_Preview() {
    FoodyTheme {
        Content(
            modifier = Modifier,
            recipes = listOf(
                RecipeRecommendationModel(
                    id = 1,
                    name = "pancakes",
                    description = "Regular dish for regular breakfast.",
                    isFavourite = true,
                    isFavouriteVisible = true
                )
            ),
            onRecipeClicked = {},
            onFavouriteButtonClicked = { _, _ -> },
            onRecommendButtonClick = {}
        )
    }
}

@Composable
@Preview
private fun Recommendations_Preview() {
    val state = RecommendStore.State(
        ingredients = listOf("fish", "pork", "tomato", "butter", "egg"),
        tags = listOf("breakfast", "15-minutes-or-less"),
        maxAbsentIngredients = 1,
        ingredientDialogState = RecommendStore.State.SelectIngredientDialogState.Hidden,
        tagDialogState = RecommendStore.State.SelectTagDialogState.Hidden,
        resultStatus = RecommendationStatus.Content(
            recipes = listOf(
                RecipeRecommendationModel(
                    id = 1,
                    name = "pancakes",
                    description = "Regular dish for regular breakfast.",
                    isFavourite = true,
                    isFavouriteVisible = true
                )
            )
        )
    )
    FoodyTheme {
        RecommendContent(
            state = state,
            onRecommendButtonClick = {},
            onFavouriteButtonClicked = { _, _ -> },
            openRecipeDetails = {},
            removeTag = {},
            removeIngredient = {},
            showAddTagDialog = {},
            showAddIngredientDialog = {},
            changeMaxAbsentIngredients = {}
        )
    }
}