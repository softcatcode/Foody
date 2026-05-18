package com.softcat.foody.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.softcat.foody.R
import com.softcat.foody.common.ProgressBar
import com.softcat.foody.common.SimpleAppBar
import com.softcat.foody.common.StringDataItem
import com.softcat.foody.ui.theme.FoodyTheme
import com.softcat.foody.ui.theme.WarningColor

@Composable
fun TextIconButton(
    modifier: Modifier = Modifier,
    text: String,
    iconId: Int,
    onClick: () -> Unit = {},
    background: Color
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 16.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(iconId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                text = text,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

    }
}

@Composable
fun OptionPanel(
    modifier: Modifier = Modifier,
    onExitClick: () -> Unit,
    openScores: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextIconButton(
            modifier = Modifier.wrapContentHeight(),
            iconId = R.drawable.exit,
            text = stringResource(R.string.exit),
            onClick = onExitClick,
            background = MaterialTheme.colorScheme.surface
        )
        TextIconButton(
            modifier = Modifier.wrapContentHeight(),
            iconId = R.drawable.star_outlined,
            text = stringResource(R.string.scores),
            onClick = openScores,
            background = MaterialTheme.colorScheme.surface
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun UserDataSheet(
    modifier: Modifier = Modifier,
    user: UserModel,
    onExitClick: () -> Unit,
    openScores: () -> Unit
) {
    val icons = listOf(
        R.drawable.profile_filled,
        R.drawable.email_filled,
    )
    val data = with(user) {
        listOf(name, email)
    }
    val hints = listOf(
        stringResource(R.string.user_name),
        stringResource(R.string.user_email),
    )
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxTextFieldHeight = screenHeight * 0.15f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 80.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in data.indices) {
                StringDataItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxTextFieldHeight),
                    hint = hints[i],
                    text = data[i],
                    leadingIconId = icons[i]
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 64.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall,
                text = user.registrationDate
            )
            OptionPanel(
                modifier = Modifier.heightIn(max = screenHeight * 0.3f),
                onExitClick = onExitClick,
                openScores = openScores
            )
        }
    }
}

@Composable
fun AvatarCard(
    modifier: Modifier = Modifier,
    avatarState: ProfileStore.State.AvatarState,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (avatarState is ProfileStore.State.AvatarState.Loaded) {
                AsyncImage(
                    modifier = Modifier.size(90.dp),
                    model = avatarState.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(20.dp),
                    painter = painterResource(R.drawable.add_photo),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            if (avatarState is ProfileStore.State.AvatarState.Updating) {
                ProgressBar(Modifier.size(90.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfo(
    user: UserModel,
    avatarState: ProfileStore.State.AvatarState,
    onExitClick: () -> Unit,
    openScores: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SimpleAppBar(stringResource(R.string.profile_title))
        }
    ) { paddingValues ->
        val color = MaterialTheme.colorScheme.surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(16.dp)
                .drawBehind {
                    drawCircle(
                        color = color,
                        radius = size.height / 2.5f,
                        center = Offset(size.width / 2, 0f)
                    )
                }
        ) {
            UserDataSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 32.dp, bottom = 16.dp),
                user = user,
                openScores = openScores,
                onExitClick = onExitClick
            )
            AvatarCard(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .wrapContentSize(),
                avatarState = avatarState,
                onClick = onAvatarClick
            )
        }
    }
}

@Composable
@Preview
fun ConfirmDialog(
    message: String = "Default message",
    onConfirmClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                contentDescription = null,
                tint = WarningColor,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(4f)
                ) {
                    Text(
                        text = stringResource(R.string.delay),
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onConfirmClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(5f)
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(component: ProfileComponent) {
    val model by component.model.collectAsState()
    val context = LocalContext.current
    val permissionWarning = stringResource(R.string.deny_read_images_warning)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        component.saveAvatar(context, it)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            Toast.makeText(context, permissionWarning, Toast.LENGTH_SHORT).show()
        }
    }
    val checkReadImagePermission = {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }
    if (model.isDialogShown) {
        BasicAlertDialog(
            onDismissRequest = component::hideExitDialog // Dismiss when tap outside.
        ) {
            ConfirmDialog(
                message = stringResource(R.string.confirm_exit),
                onConfirmClick = component::exit,
                onDismissRequest = component::hideExitDialog // Dismiss when close explicitly.
            )
        }
    }
    UserInfo(
        user = model.user,
        avatarState = model.avatarState,
        openScores = component::openScores,
        onAvatarClick = {
            if (checkReadImagePermission())
                launcher.launch("image/*")
            else
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        },
        onExitClick = component::showExitDialog
    )
}

@Composable
@Preview
private fun Profile_Preview() {
    FoodyTheme {
        UserInfo(
            user = UserModel(
                email = "user@gmail.com",
                name = "Mike",
                registrationDate = "11.05.2026"
            ),
            avatarState = ProfileStore.State.AvatarState.AvatarIsAbsent,
            openScores = {},
            onExitClick = {},
            onAvatarClick = {},
        )
    }
}