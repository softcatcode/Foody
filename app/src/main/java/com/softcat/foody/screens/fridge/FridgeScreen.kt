package com.softcat.foody.screens.fridge

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.softcat.foody.R
import com.softcat.foody.common.FridgeTopBar
import com.softcat.foody.ui.theme.BaseOrange
import com.softcat.foody.ui.theme.FoodyTheme
import com.softcat.foody.ui.theme.Pink
import com.softcat.foody.ui.theme.Purple

@Composable
fun FridgeScreen(component: FridgeComponent) {
    val state by component.model.collectAsStateWithLifecycle()

    FridgeContent(
        state = state,
        onIngredientClick = component::removeIngredient,
        onAddIngredientClicked = component::addIngredientClick,
        onCartClicked = component::openCart,
        onResetClicked = component::resetIngredients,
        onBackClicked = component::back,
    )
    AddIngredientsDialog(
        state = state.dialogState,
        onDismiss = component::hideDialog,
        onQueryChange = component::changeSearchQuery,
        searchIngredients = component::searchIngredient,
        addIngredient = component::addIngredient
    )
}

@Composable
private fun FridgeContent(
    state: FridgeStore.State,
    onIngredientClick: (String) -> Unit,
    onAddIngredientClicked: () -> Unit,
    onCartClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            FridgeTopBar(
                onCartClicked = onCartClicked,
                onResetClicked = onResetClicked,
                onBackClicked = onBackClicked
            )
        },
        floatingActionButton = {
            AddIngredientButton(
                onClick = onAddIngredientClicked
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        ) {
            item {
                Spacer(Modifier.height(16.dp))
            }
            items(
                items = state.categories,
                key = { it.id }
            ) { model ->
                IngredientCategoryCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    titleResId = model.titleResId,
                    iconResId = model.iconResId,
                    color = model.color,
                    names = model.names,
                    onIngredientClick = onIngredientClick
                )
            }
        }
    }
}

@Composable
private fun AddIngredientButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = CircleShape,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = BaseOrange
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.plus),
            contentDescription = null,
            tint = White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
        )
    }
}

@Preview
@Composable
private fun FridgeContent_Preview() {
    val state = FridgeStore.State(
        categories = listOf(
            FridgeStore.State.IngredientCategoryCard(
                id = 1,
                titleResId = R.string.fridge_category_crops,
                iconResId = R.drawable.crops,
                names = listOf("Гречка", "Перловка", "Овсянка"),
                color = BaseOrange
            ),

            FridgeStore.State.IngredientCategoryCard(
                id = 2,
                titleResId = R.string.fridge_category_dairy,
                iconResId = R.drawable.milk,
                names = listOf("Молоко", "Творог"),
                color = Purple,
            ),

            FridgeStore.State.IngredientCategoryCard(
                id = 3,
                titleResId = R.string.fridge_category_fruit_vegetables,
                iconResId = R.drawable.vegetables,
                names = listOf("Яблоко", "Банан", "Морковь"),
                color = BaseOrange,
            ),

            FridgeStore.State.IngredientCategoryCard(
                id = 4,
                titleResId = R.string.fridge_category_meat_fish,
                iconResId = R.drawable.meat,
                names = listOf("Говядина", "Свинина"),
                color = Pink,
            ),
        ),
        dialogState = FridgeStore.State.SelectIngredientDialogState.Hidden
    )

    FoodyTheme {
        FridgeContent(
            state = state,
            onIngredientClick = {},
            onAddIngredientClicked = {},
            onCartClicked = {},
            onResetClicked = {},
            onBackClicked = {},
        )
    }
}