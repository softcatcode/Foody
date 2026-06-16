package com.softcat.foody.screens.favourites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.domain.entities.FilterParams
import com.softcat.foody.R
import com.softcat.foody.common.FilterButton
import com.softcat.foody.common.FilterParamsSheet
import com.softcat.foody.common.SimpleAppBar
import com.softcat.foody.common.RecipeGrid
import com.softcat.foody.common.RecipeModel
import com.softcat.foody.common.ResultText
import com.softcat.foody.common.ResultTitle
import com.softcat.foody.ui.theme.FoodyTheme
import kotlin.collections.first

@Composable
fun NavigationButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 8.dp)
            .then(modifier),
        onClick = onClick,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        shape = RectangleShape
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun Empty(
    modifier: Modifier = Modifier,
    openSearch: () -> Unit,
    openRecommendations: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(128.dp),
            painter = painterResource(R.drawable.hat_smile_image),
            contentDescription = null
        )
        Spacer(Modifier.height(16.dp))
        ResultTitle(
            text = stringResource(R.string.favourites_without_content_title)
        )
        Spacer(Modifier.height(4.dp))
        ResultText(
            text = stringResource(R.string.favourites_without_content_text)
        )
        Spacer(Modifier.height(16.dp))
        NavigationButton(
            text = stringResource(R.string.find_recipe_navigation_title),
            onClick = openSearch
        )
        Spacer(Modifier.height(8.dp))
        NavigationButton(
            text = stringResource(R.string.recommend_recipe_navigation_title),
            onClick = openRecommendations
        )
    }
}

@Composable
private fun FavouritesScaffold(
    onFilterClick: () -> Unit,
    stateContent: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        topBar = {
            SimpleAppBar(
                text = stringResource(R.string.favourites_title),
                windowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal)
            )
        },
        floatingActionButton = { FilterButton(onClick = onFilterClick, size = 64.dp) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            stateContent()
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun UserIsAbsent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(128.dp),
            painter = painterResource(R.drawable.hat_smile_image),
            contentDescription = null
        )
        Spacer(Modifier.height(16.dp))
        ResultTitle(
            text = stringResource(R.string.favourites_without_user_title)
        )
        Spacer(Modifier.height(4.dp))
        ResultText(
            text = stringResource(R.string.favourites_without_user_text)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesContent(
    state: FavouritesStore.State,
    onFilterClick: () -> Unit,
    closeFilterSheet: () -> Unit,
    openRecommendations: () -> Unit,
    openSearch: () -> Unit,
    onResetClicked: () -> Unit,
    onRecipeClick: (Int) -> Unit,
    removeFromFavourite: (Int) -> Unit,
    onScoreClicked: (Int) -> Unit,
    onCookingTimeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onCaloriesChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onCookedStateChange: (FilterParams.TripleChoice) -> Unit,
    onTagClicked: (String) -> Unit,
    onIngredientClicked: (String) -> Unit
) {
    FavouritesScaffold(
        onFilterClick = onFilterClick
    ) {
        when (val contentState = state.contentStatus) {
            FavouritesStore.State.ContentStatus.Empty -> {
                Empty(
                    openSearch = openSearch,
                    openRecommendations = openRecommendations
                )
            }

            is FavouritesStore.State.ContentStatus.RecipeList -> {
                RecipeGrid(
                    modifier = Modifier.fillMaxSize(),
                    onRecipeClick = onRecipeClick,
                    changeFavouriteStatus = removeFromFavourite,
                    recipes = contentState.recipes,
                )
            }

            FavouritesStore.State.ContentStatus.UserIsAbsent -> {
                UserIsAbsent()
            }

            FavouritesStore.State.ContentStatus.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = { CircularProgressIndicator() }
                )
            }
        }

        FilterParamsSheet(
            modifier = Modifier.fillMaxWidth(),
            onDismiss = closeFilterSheet,
            params = state.filtersStatus.filterParameters,
            suggestedTags = state.filtersStatus.suggestedTags,
            suggestedIngredients = state.filtersStatus.suggestedIngredients,
            isExpanded = state.filtersStatus.expanded,
            onScoreClicked = onScoreClicked,
            onCaloriesChange = onCaloriesChange,
            onCookingTimeChange = onCookingTimeChange,
            onCookedStateChange = onCookedStateChange,
            onIngredientClicked = onIngredientClicked,
            onTagClicked = onTagClicked,
            onResetClicked = onResetClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(component: FavouritesComponent) {
    val model = component.model.collectAsState()

    FavouritesContent(
        state = model.value,
        openRecommendations = component::openRecommendationScreen,
        openSearch = component::openSearchScreen,
        onRecipeClick = component::openRecipeDetailsScreen,
        removeFromFavourite = component::removeFromFavourites,
        onScoreClicked = component::changeScore,
        onCookingTimeChange = component::changeCookingTime,
        onCookedStateChange = component::changeIsCookedStatus,
        onCaloriesChange = component::changeCalories,
        onTagClicked = component::tagClicked,
        onIngredientClicked = component::ingredientClicked,
        onFilterClick = component::expandFiltersSheet,
        closeFilterSheet = component::hideFiltersSheet,
        onResetClicked = component::resetFilters
    )
}

@Composable
@Preview
private fun Empty_Preview() {
    val state = FavouritesStore.State(
        filtersStatus = FavouritesStore.State.FiltersSheetState(
            filterParameters = FilterParams(
                tags = listOf("ice", "15-minutes-or-less"),
                ingredients = listOf("milk")
            ),
            suggestedTags = listOf("breakfast", "easy"),
            suggestedIngredients = listOf("apple", "butter", "honey"),
            expanded = false
        ),
        contentStatus = FavouritesStore.State.ContentStatus.Empty,
    )
    FoodyTheme {
        FavouritesContent(
            state = state,
            openRecommendations = {},
            openSearch = {},
            onRecipeClick = {},
            removeFromFavourite = {},
            onScoreClicked = {},
            onCookingTimeChange = {},
            onCookedStateChange = {},
            onCaloriesChange = {},
            onTagClicked = {},
            onIngredientClicked = {},
            onFilterClick = {},
            closeFilterSheet = {},
            onResetClicked = {}
        )
    }
}

@Composable
@Preview
private fun Content_Preview() {
    val recipes = mutableListOf(
        RecipeModel(
            id = 1,
            name = "Pancakes",
            description = "Food for breakfast.",
            ingredients = listOf("Milk", "Oil", "Flour", "egg"),
            favouriteButtonVisible = true,
            isFavourite = true,
            score = "4",
            scoreVisible = true,
        )
    ).apply {
        add(first().copy())
        add(first().copy())
    }

    val state = FavouritesStore.State(
        filtersStatus = FavouritesStore.State.FiltersSheetState(
            filterParameters = FilterParams(),
            suggestedTags = listOf("breakfast", "easy"),
            suggestedIngredients = listOf("apple", "butter", "honey"),
            expanded = false
        ),
        contentStatus = FavouritesStore.State.ContentStatus.RecipeList(
            recipes = recipes,
        ),
    )

    FoodyTheme {
        FavouritesContent(
            state = state,
            openRecommendations = {},
            openSearch = {},
            onRecipeClick = {},
            removeFromFavourite = {},
            onScoreClicked = {},
            onCookingTimeChange = {},
            onCookedStateChange = {},
            onCaloriesChange = {},
            onTagClicked = {},
            onIngredientClicked = {},
            onFilterClick = {},
            closeFilterSheet = {},
            onResetClicked = {}
        )
    }
}