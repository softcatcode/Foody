package com.softcat.foody.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcat.foody.R

@Composable
@Preview(showBackground = true)
fun ResultTitle(
    modifier: Modifier = Modifier,
    text: String = "Hello, world"
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = Black,
        fontWeight = FontWeight.Bold
    )
}

@Composable
@Preview(showBackground = true)
fun ResultText(
    modifier: Modifier = Modifier,
    text: String = "Hello, world"
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Black,
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun StringDataItem(
    modifier: Modifier = Modifier,
    leadingIconId: Int = R.drawable.profile_outlined,
    trailingIconId: Int? = null,
    trailingIconAction: () -> Unit = {},
    hint: String = "hint",
    text: String = "some text",
    onValueChange: (String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .then(modifier),
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        value = text,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledBorderColor = MaterialTheme.colorScheme.onBackground,
            errorBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledTextColor = Black,
            focusedTextColor = Black,
            unfocusedTextColor = Black,
        ),
        label = {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(leadingIconId),
                contentDescription = hint,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            trailingIconId?.let {
                IconButton(trailingIconAction) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(it),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        readOnly = readOnly
    )
}