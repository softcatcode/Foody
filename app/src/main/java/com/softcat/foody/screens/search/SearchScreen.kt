package com.softcat.foody.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.softcat.domain.entities.FilterParams
import com.softcat.foody.R
import com.softcat.foody.common.FilterButton
import com.softcat.foody.common.FilterParamsSheet
import com.softcat.foody.common.ProgressBar
import com.softcat.foody.common.SimpleAppBar
import com.softcat.foody.common.RecipeGrid
import com.softcat.foody.common.RecipeModel
import com.softcat.foody.common.ResultText
import com.softcat.foody.common.ResultTitle
import com.softcat.foody.common.SearchLine
import com.softcat.foody.ui.theme.FoodyTheme

@Composable
private fun Empty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(128.dp),
            painter = painterResource(R.drawable.question_mark),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
        Spacer(Modifier.height(16.dp))
        ResultTitle(
            text = stringResource(R.string.empty_result_title)
        )
        Spacer(Modifier.height(4.dp))
        ResultText(
            text = stringResource(R.string.empty_result_hint)
        )
    }
}

@Composable
private fun Initial(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            modifier = Modifier.size(128.dp),
            model = R.drawable.search_animation,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(16.dp))
        ResultText(
            text = stringResource(R.string.search_screen_hint)
        )
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        ProgressBar(
            modifier = Modifier.size(128.dp)
        )
    }
}

@Composable
private fun SearchBase(
    onFilterClick: () -> Unit,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchSubmitted: (String) -> Unit,
    stateContent: @Composable BoxScope.() -> Unit
) {
    Scaffold(
        topBar = { SimpleAppBar(stringResource(R.string.search_title)) },
        floatingActionButton = { FilterButton(onClick = onFilterClick, size = 64.dp) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            stateContent()
            SearchLine(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 8.dp),
                value = searchText,
                onValueChange = onSearchTextChanged,
                onSearchSubmitted = onSearchSubmitted,
                placeholderText = stringResource(R.string.search_line_placeholder)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(component: SearchComponent) {
    val model = component.model.collectAsState()

    SearchContent(
        state = model.value,
        onTagClicked = component::tagClicked,
        onIngredientClicked = component::ingredientClicked,
        onCookingTimeChange = component::changeCookingTime,
        onCaloriesChange = component::changeCalories,
        onScoreClicked = component::changeScore,
        onCookedStateChange = component::changeIsCookedStatus,
        removeFromFavourites = component::removeFromFavourites,
        addToFavourites = component::addToFavourites,
        closeFilterSheet = component::hideFiltersSheet,
        openFilterSheet = component::expandFiltersSheet,
        openRecipeDetails = component::openRecipeDetails,
        search = component::search,
        changeQuery = component::changeQuery,
        onResetClicked = component::resetFilters
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    state: SearchStore.State,
    onTagClicked: (String) -> Unit,
    onIngredientClicked: (String) -> Unit,
    onCookingTimeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onCaloriesChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onScoreClicked: (Int) -> Unit,
    onCookedStateChange: (FilterParams.TripleChoice) -> Unit,
    removeFromFavourites: (Int) -> Unit,
    addToFavourites: (Int) -> Unit,
    closeFilterSheet: () -> Unit,
    openFilterSheet: () -> Unit,
    onResetClicked: () -> Unit,
    openRecipeDetails: (Int) -> Unit,
    search: (String) -> Unit,
    changeQuery: (String) -> Unit
) {
    SearchBase(
        onFilterClick = openFilterSheet,
        searchText = state.searchQuery,
        onSearchTextChanged = changeQuery,
        onSearchSubmitted = search
    ) {
        when (val searchState = state.searchStatus) {

            is SearchStore.State.SearchStatus.Content -> {
                RecipeGrid(
                    onRecipeClick = openRecipeDetails,
                    addToFavourite = addToFavourites,
                    removeFromFavourite = removeFromFavourites,
                    recipes = searchState.recipes,
                    topPadding = 64.dp
                )
            }

            SearchStore.State.SearchStatus.Empty -> {
                Empty(Modifier.fillMaxSize())
            }

            SearchStore.State.SearchStatus.Initial -> {
                Initial(Modifier.fillMaxSize())
            }

            SearchStore.State.SearchStatus.Loading -> {
                Loading(Modifier.fillMaxSize())
            }
        }

        FilterParamsSheet(
            modifier = Modifier.fillMaxWidth(),
            onDismiss = closeFilterSheet,
            params = state.filtersState.filterParameters,
            tags = state.filtersState.visibleTags,
            ingredients = state.filtersState.visibleIngredients,
            isExpanded = state.filtersState.expanded,
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
            score = "3.65",
            scoreVisible = true,
        )
    ).apply {
        add(first().copy())
        add(first().copy())
    }

    val state = SearchStore.State(
        searchQuery = "query",
        filtersState = SearchStore.State.FiltersSheetState(
            filterParameters = FilterParams(),
            visibleTags = listOf("ice", "breakfast", "15-minutes-or-less", "easy"),
            visibleIngredients = listOf("apple", "butter", "milk", "honey"),
            expanded = false
        ),
        searchStatus = SearchStore.State.SearchStatus.Content(recipes)
    )

    FoodyTheme {
        SearchContent(
            state = state,
            onScoreClicked = {},
            onCookingTimeChange = {},
            onCookedStateChange = {},
            onCaloriesChange = {},
            onTagClicked = {},
            onIngredientClicked = {},
            removeFromFavourites = {},
            addToFavourites = {},
            closeFilterSheet = {},
            openFilterSheet = {},
            openRecipeDetails = {},
            search = {},
            changeQuery = {},
            onResetClicked = {}
        )
    }
}