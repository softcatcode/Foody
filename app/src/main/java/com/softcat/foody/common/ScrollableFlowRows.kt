package com.softcat.foody.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.foody.R

@Composable
@Preview(showBackground = true)
fun ElementsScrollableFlow(
    modifier: Modifier = Modifier,
    elements: List<String> = listOf(
        "Milk", "Eggs", "Pork", "Nuts", "Chocolate", "Oil", "Strawberry", "Coffee",
        "Apples", "Mango", "Cucumber", "Carrot", "Plum", "Tomatoes"
    ),
    addElementClicked: () -> Unit = {},
    removeElementClicked: (String) -> Unit = {},
    color: Color = MaterialTheme.colorScheme.primary,
    title: String = stringResource(R.string.ingredients),
    iconId: Int = R.drawable.vegetables,
    showAddButton: Boolean = true,
    appendixContent: @Composable BoxScope.() -> Unit = {},
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(iconId),
                    contentDescription = null,
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                FlowRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    elements.forEach {
                        SimpleStringValueCard(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            isActive = true,
                            label = it,
                            color = color,
                            onClick = { removeElementClicked(it) }
                        )
                    }
                    if (showAddButton) {
                        AddElementButton(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = addElementClicked,
                            color = color
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    content = appendixContent
                )
            }
        }
    }
}