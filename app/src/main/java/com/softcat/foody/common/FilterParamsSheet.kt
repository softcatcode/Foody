package com.softcat.foody.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.domain.entities.FilterParams
import com.softcat.foody.R

@Composable
@Preview(showBackground = true)
fun ResetButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = Modifier
            .wrapContentSize()
            .then(modifier),
        onClick = onClick,
        shape = CircleShape,
    ) {
        Icon(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                .padding(8.dp)
                .size(32.dp),
            painter = painterResource(R.drawable.reset),
            contentDescription = null,
            tint = Black
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FilterParamsSheetElem(
    modifier: Modifier = Modifier,
    title: String = "Title",
    label: String = "1500 units",
    content: @Composable BoxScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(2f),
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Black,
            textAlign = TextAlign.Start
        )
        Box(
            modifier = Modifier.weight(3f),
            content = content,
            contentAlignment = Alignment.Center
        )
        Text(
            modifier = Modifier.weight(2f),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FilterParamsSheetTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        modifier = Modifier
            .wrapContentSize()
            .then(modifier),
        text = title,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.bodyLarge,
        color = Black
    )
}

@Composable
fun TagFlow(
    modifier: Modifier = Modifier,
    suggestedValues: List<String>,
    selectedValues: List<String>,
    onTagClicked: (String) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(items = selectedValues, key = { it }) {
            SimpleTagCard(
                name = it,
                isActive = true,
                onClick = { onTagClicked(it) }
            )
        }
        items(items = suggestedValues, key = { it }) {
            SimpleTagCard(
                name = it,
                isActive = false,
                onClick = { onTagClicked(it) }
            )
        }
    }
}

@Composable
fun IngredientFlow(
    modifier: Modifier = Modifier,
    selectedValues: List<String>,
    otherValues: List<String>,
    onIngredientClicked: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        items(items = selectedValues, key = { it }) {
            SimpleIngredientCard(
                name = it,
                isActive = true,
                onClick = { onIngredientClicked(it) }
            )
        }
        items(items = otherValues, key = { it }) {
            SimpleIngredientCard(
                name = it,
                isActive = false,
                onClick = { onIngredientClicked(it) }
            )
        }
    }
}

@Composable
fun BottomSheetDragHandle() {
    val color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .height(3.dp)
                .width(50.dp)
                .background(color)
        )
        Spacer(Modifier.height(3.dp))
        Spacer(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .height(3.dp)
                .width(30.dp)
                .background(color)

        )
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterParamsSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(),
    params: FilterParams = FilterParams(),
    suggestedTags: List<String>,
    suggestedIngredients: List<String>,
    isExpanded: Boolean = true,
    onScoreClicked: (Int) -> Unit = {},
    onCaloriesChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    onCookingTimeChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    onCookedStateChange: (FilterParams.TripleChoice) -> Unit = {},
    onIngredientClicked: (String) -> Unit = {},
    onTagClicked: (String) -> Unit = {},
    onResetClicked: () -> Unit = {}
) {
    if (isExpanded) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .then(modifier),
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            tonalElevation = 2.dp,
            contentColor = MaterialTheme.colorScheme.background,
            scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
            dragHandle = { BottomSheetDragHandle() },
            shape = RoundedCornerShape(25.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                ResetButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = onResetClicked
                )
                Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp)) {
                    FilterParamsSheetTitle(
                        title = stringResource(R.string.filters_sheet_title)
                    )
                    Spacer(Modifier.height(8.dp))
                    FilterParamsSheetElem(
                        title = stringResource(R.string.min_score_filter_param_title),
                        label = stringResource(R.string.min_score_filter_param_value)
                            .format(params.minScore)
                    ) {
                        ScoreSelector(
                            scoreValue = params.minScore,
                            iconSize = 20.dp,
                            onScoreClicked = onScoreClicked
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    FilterParamsSheetElem(
                        title = stringResource(R.string.calories_filter_param_title),
                        label = stringResource(R.string.calories_filter_param_value)
                            .format(params.calories.start.toInt(), params.calories.endInclusive.toInt())
                    ) {
                        ParameterRangeSlider(
                            onValueChange = onCaloriesChange,
                            steps = FilterParams.MAX_CALORIES.toInt(),
                            maxValue = FilterParams.MAX_CALORIES,
                            value = params.calories
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    FilterParamsSheetElem(
                        title = stringResource(R.string.duration_filter_param_title),
                        label = stringResource(R.string.duration_filter_param_value)
                            .format(params.duration.start.toInt(), params.duration.endInclusive.toInt())
                    ) {
                        ParameterRangeSlider(
                            steps = FilterParams.MAX_DURATION.toInt(),
                            maxValue = FilterParams.MAX_DURATION,
                            onValueChange = onCookingTimeChange,
                            value = params.duration
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    FilterParamsSheetElem(
                        title = stringResource(R.string.is_cooked_filter_param_title),
                        label = stringResource(
                            when (params.isCooked) {
                                FilterParams.TripleChoice.Yes -> R.string.yes
                                FilterParams.TripleChoice.No -> R.string.no
                                FilterParams.TripleChoice.NotImportant -> R.string.not_important
                            }
                        )
                    ) {
                        TripleSwitcher(
                            size = 70,
                            onStateChange = onCookedStateChange,
                            state = params.isCooked
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    FilterParamsSheetTitle(
                        title = stringResource(R.string.filters_sheet_tags_title)
                    )
                    Spacer(Modifier.height(4.dp))
                    TagFlow(
                        suggestedValues = suggestedTags,
                        selectedValues = params.tags,
                        onTagClicked = onTagClicked,
                    )
                    Spacer(Modifier.height(8.dp))
                    FilterParamsSheetTitle(
                        title = stringResource(R.string.filters_sheet_ingredients_title)
                    )
                    Spacer(Modifier.height(4.dp))
                    IngredientFlow(
                        selectedValues = params.ingredients,
                        otherValues = suggestedIngredients,
                        onIngredientClicked = onIngredientClicked
                    )
                }
            }
        }
    }
}