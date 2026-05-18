package com.softcat.foody.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softcat.foody.R

@Composable
@Preview(showBackground = true)
fun FilterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    size: Dp = 64.dp
) {
    OutlinedCard(
        modifier = Modifier
            .wrapContentSize()
            .size(size)
            .then(modifier),
        onClick = onClick,
        shape = CircleShape,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        )
    ) {
        Icon(
            modifier = Modifier
                .size(size)
                .padding(12.dp),
            painter = painterResource(R.drawable.filter),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
@Preview
fun AddToFavouritesButton(
    modifier: Modifier = Modifier,
    isFavourite: Boolean = false,
    onClick: () -> Unit = {}
) {
    val resId = if (isFavourite) R.drawable.bookmark_filled else R.drawable.bookmark_outlined
    val color = if (isFavourite) Red else Gray
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(resId),
            contentDescription = null,
            tint = color
        )
    }
}

@Composable
@Preview
fun AddElementButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.primary,
    iconId: Int = R.drawable.plus
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(2.dp, color),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = stringResource(R.string.add),
                color = color,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(iconId),
                contentDescription = null,
                tint = color
            )
        }
    }
}

@Composable
@Preview
fun RecommendationButton(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface else White
        )
    ) {
        Text(
            text = stringResource(R.string.recommend_button_text),
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive) White else MaterialTheme.colorScheme.surface
        )
    }
}