package com.softcat.foody.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softcat.foody.R
import com.softcat.foody.ui.theme.BaseYellow
import kotlin.math.max

@Composable
@Preview
fun ScoreSelector(
    modifier: Modifier = Modifier,
    scoreValue: Int = 3,
    maxScore: Int = 5,
    iconSize: Dp = 16.dp,
    onScoreClicked: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier.wrapContentSize().then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(maxScore) {
            val iconResId = if (it < scoreValue) R.drawable.star_filled else R.drawable.star_outlined
            val color = if (it < scoreValue) BaseYellow else MaterialTheme.colorScheme.tertiary
            IconButton(
                modifier = Modifier.size(iconSize),
                onClick = { onScoreClicked(it + 1) }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    tint = color
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ParameterRangeSlider(
    modifier: Modifier = Modifier,
    value: ClosedFloatingPointRange<Float> = 0f..100f,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    steps: Int = 1000,
    maxValue: Float = 1000f
) {
    val scale = 16
    RangeSlider(
        modifier = Modifier.wrapContentSize().then(modifier),
        value = value,
        steps = steps,
        onValueChange = onValueChange,
        valueRange = 0f..maxValue,
        onValueChangeFinished = {},
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        startThumb = {
            Box(contentAlignment = Alignment.Center) {
                Spacer(
                    modifier = Modifier
                        .size(scale.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        },
        track = {
            val thickness = max(2, scale / 10)
            Spacer(
                modifier = Modifier
                    .size(100.dp, thickness.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        },
        endThumb = {
            Spacer(
                modifier = Modifier
                    .size(scale.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        },
    )
}