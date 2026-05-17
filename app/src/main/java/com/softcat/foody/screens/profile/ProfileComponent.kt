package com.softcat.foody.screens.profile

import android.content.Context
import android.net.Uri
import com.softcat.domain.entities.User
import kotlinx.coroutines.flow.StateFlow

interface ProfileComponent {
    val model: StateFlow<ProfileStore.State>

    fun exit()

    fun openScores()

    fun modifyUser(user: User)

    fun saveAvatar(context: Context, uri: Uri?)

    fun showExitDialog()

    fun hideExitDialog()
}