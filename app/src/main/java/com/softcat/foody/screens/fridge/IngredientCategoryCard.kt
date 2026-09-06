package com.softcat.foody.screens.fridge

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.foody.R
import com.softcat.foody.ui.theme.FoodyTheme
import com.softcat.foody.ui.theme.Pink

@Composable
fun IngredientCategoryCard(
    modifier: Modifier = Modifier,
    titleResId: Int,
    iconResId: Int,
    color: Color,
    names: List<String>,
    onIngredientClick: (String) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            IngredientCategoryTitle(
                titleResId = titleResId,
                iconResId = iconResId
            )
            FlowRow(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                names.forEach { ingredient ->
                    key(ingredient) {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 32.dp) {
                            IngredientItem(
                                name = ingredient,
                                color = color,
                                onClick = onIngredientClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientCategoryTitle(
    titleResId: Int,
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(titleResId),
            color = Black,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun IngredientItem(
    name: String,
    color: Color,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        onClick = { onClick(name) }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            text = name,
            color = White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Composable
private fun IngredientCategoryCard_Preview() {
    FoodyTheme {
        IngredientCategoryCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            titleResId = R.string.fridge_category_meat_fish,
            iconResId = R.drawable.meat,
            names = listOf("Свинина", "Баранина", "Осётр", "Свинина", "Баранина", "Осётр", "Свинина", "Баранина", "Осётр"),
            onIngredientClick = {},
            color = Pink,
        )
    }
}