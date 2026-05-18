package com.softcat.foody.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.domain.entities.FilterParams

@Composable
@Preview(showBackground = true)
fun Switcher(
    modifier: Modifier = Modifier,
    checked: Boolean = true,
    onCheckedChanged: () -> Unit = {},
) {
    val size = 64.dp
    val squareOffset by animateDpAsState(
        targetValue = if (checked) size / 2 else 0.dp,
        label = "squareOffset"
    )
    val color = if (checked)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondary
    val cornerRadius = 5.dp
    Box(
        modifier = Modifier
            .size(width = size, height = size / 2)
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(cornerRadius)
            ).clickable(
                enabled = true,
                onClick = onCheckedChanged,
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = color.copy(alpha = 0.2f))
            )
            .then(modifier),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .padding(4.dp)
                .offset(x = squareOffset)
                .clip(RoundedCornerShape(cornerRadius / 2))
                .background(color)
        )
    }
}

@Composable
@Preview
fun TripleSwitcher(
    modifier: Modifier = Modifier,
    onStateChange: (FilterParams.TripleChoice) -> Unit = {},
    state: FilterParams.TripleChoice = FilterParams.TripleChoice.NotImportant,
    size: Int = 60
) {
    val targetOffset = when (state) {
        FilterParams.TripleChoice.Yes -> 2 * size / 3
        FilterParams.TripleChoice.NotImportant -> size / 3
        FilterParams.TripleChoice.No -> 0
    }
    val squareOffset by animateDpAsState(
        targetValue = targetOffset.dp,
        label = "squareOffset"
    )
    val color = when (state) {
        FilterParams.TripleChoice.Yes -> MaterialTheme.colorScheme.primary
        FilterParams.TripleChoice.NotImportant -> MaterialTheme.colorScheme.tertiary
        FilterParams.TripleChoice.No -> MaterialTheme.colorScheme.secondary
    }
    val cornerRadius = 5.dp
    Box(
        modifier = Modifier
            .size(size.dp, size.dp / 3)
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(cornerRadius)
            ).clickable(
                enabled = true,
                onClick = {
                    val newState = when (state) {
                        FilterParams.TripleChoice.Yes -> FilterParams.TripleChoice.No
                        FilterParams.TripleChoice.NotImportant -> FilterParams.TripleChoice.Yes
                        FilterParams.TripleChoice.No -> FilterParams.TripleChoice.NotImportant
                    }
                    onStateChange(newState)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = color.copy(alpha = 0.2f))
            )
            .then(modifier),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.333f)
                .padding(4.dp)
                .offset(x = squareOffset)
                .clip(RoundedCornerShape(cornerRadius / 2))
                .background(color)
        )
    }
}