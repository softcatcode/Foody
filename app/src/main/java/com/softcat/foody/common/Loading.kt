package com.softcat.foody.common

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    color: Color = Color.DarkGray
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        trackColor = MaterialTheme.colorScheme.secondary,
        strokeWidth = 5.dp,
        strokeCap = StrokeCap.Butt
    )
}