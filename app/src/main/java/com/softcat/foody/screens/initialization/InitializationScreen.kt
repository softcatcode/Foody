package com.softcat.foody.screens.initialization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.foody.R
import com.softcat.foody.common.ProgressBar
import com.softcat.foody.common.SimpleAppBar
import com.softcat.foody.ui.theme.FoodyTheme
import com.softcat.foody.ui.theme.LightGray

@Composable
fun InitializationScreen(component: InitializationComponent) {
    val model = component.model.collectAsState()
    val context = LocalContext.current

    InitializationContent(
        state = model.value,
        onOptionSelected = component::selectOption,
        onContinueClicked = { component.initRecipes(context) }
    )
}

@Composable
private fun InitializationContent(
    state: InitializationStore.State,
    onOptionSelected: (Int) -> Unit,
    onContinueClicked: () -> Unit
) {
    Scaffold(
        topBar = { SimpleAppBar(text = stringResource(R.string.initialization_title)) }
    ) { paddingValues ->
        when (state) {

            is InitializationStore.State.Options -> {
                Options(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.background),
                    options = state.options,
                    onOptionSelected = onOptionSelected,
                    onContinueClicked = onContinueClicked
                )
            }

            InitializationStore.State.Loading -> {
                Loading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                )
            }
        }
    }
}

@Composable
private fun Options(
    modifier: Modifier = Modifier,
    options: List<OptionModel>,
    onOptionSelected: (Int) -> Unit,
    onContinueClicked: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = stringResource(R.string.initialization_instruction),
            color = Black
        )
        Spacer(Modifier.height(32.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            items(
                items = options,
                key = { it.value }
            ) { option ->
                OptionCard(
                    modifier = Modifier.fillMaxWidth(),
                    option = option,
                    onClick = { onOptionSelected(option.value) }
                )
            }
        }
        Spacer(Modifier.height(48.dp))
        ContinueButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = onContinueClicked
        )
        Spacer(Modifier.height(64.dp))
    }
}

@Composable
@Preview
fun ContinueButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = stringResource(R.string.continue_title),
            style = MaterialTheme.typography.headlineSmall,
            color = White
        )
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ProgressBar(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    option: OptionModel,
    onClick: () -> Unit
) {
    val borderColor = if (option.isSelected)
        MaterialTheme.colorScheme.primary
    else
        LightGray
    val elevation = if (option.isSelected) 8.dp else 0.dp
    
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(elevation),
        border = BorderStroke(2.dp, borderColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray
            )
        }
    }
}

@Composable
@Preview
fun OptionCard_Preview() {
    val option = OptionModel(
        title = "Helo world",
        text = "A simple option to initialize the database.",
        value = 1000,
        isSelected = true
    )

    FoodyTheme {
        OptionCard(
            option = option,
            onClick = {},
        )
    }
}

@Composable
@Preview
fun InitializationContent_Options_Preview() {
    val state = InitializationStore.State.Options(
        listOf(
            OptionModel(
                title = stringResource(R.string.add_recipes_title)
                    .format(5000),
                text = stringResource(R.string.small_db_description),
                value = 5000,
                isSelected = false
            ),
            OptionModel(
                title = stringResource(R.string.add_recipes_title)
                    .format(50000),
                text = stringResource(R.string.small_db_description),
                value = 50000,
                isSelected = true
            ),
            OptionModel(
                title = stringResource(R.string.add_recipes_title)
                    .format(200000),
                text = stringResource(R.string.small_db_description),
                value = 200000,
                isSelected = false
            )
        )
    )

    FoodyTheme {
        InitializationContent(
            state = state,
            onOptionSelected = {},
            onContinueClicked = {}
        )
    }
}

@Composable
@Preview
fun InitializationContent_Loading_Preview() {
    val state = InitializationStore.State.Loading

    FoodyTheme {
        InitializationContent(
            state = state,
            onOptionSelected = {},
            onContinueClicked = {}
        )
    }
}
