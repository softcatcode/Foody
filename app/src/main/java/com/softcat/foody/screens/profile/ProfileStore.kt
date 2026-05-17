package com.softcat.foody.screens.profile

import android.net.Uri
import com.arkivanov.mvikotlin.core.store.Store
import com.softcat.domain.entities.User

interface ProfileStore: Store<ProfileStore.Intent, ProfileStore.State, ProfileStore.Label> {

    sealed interface Intent {
        data class ModifyUser(val newValue: User): Intent

        data object Exit: Intent

        data object LoadScores: Intent

        data object ShowDialog: Intent

        data object HideDialog: Intent

        data class SaveAvatar(val uri: Uri?): Intent
    }

    data class State(
        val user: UserModel,
        val avatarState: AvatarState,
        val isDialogShown: Boolean
    ) {
        sealed interface AvatarState {
            data object Updating: AvatarState

            data class Loaded(val avatarUrl: String): AvatarState

            data object AvatarIsAbsent: AvatarState
        }
    }

    sealed interface Label {
        data object Exited: Label

        data class OpenScores(val userId: String): Label
    }
}