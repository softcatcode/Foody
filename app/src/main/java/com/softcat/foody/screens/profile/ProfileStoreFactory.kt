package com.softcat.foody.screens.profile

import android.app.Application
import android.net.Uri
import android.widget.Toast
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.domain.entities.User
import com.softcat.domain.usecases.UserAvatarUseCase
import com.softcat.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import com.softcat.foody.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileStoreFactory @Inject constructor(
    private val userUseCase: UserUseCase,
    private val avatarUseCase: UserAvatarUseCase,
    private val storeFactory: StoreFactory,
    private val application: Application
) {

    fun create(user: User): ProfileStore =
        object:
            ProfileStore, Store<ProfileStore.Intent, ProfileStore.State, ProfileStore.Label>
        by
            storeFactory.create(
                name = this::class.simpleName,
                initialState = ProfileStore.State(
                    user = UserModel(user.name, user.email, user.registerDate.formatAsDate()),
                    avatarState = ProfileStore.State.AvatarState.Updating,
                    isDialogShown = false
                ),
                bootstrapper = ProfileBootstrapper(user.id),
                executorFactory = { ProfileExecutor(user.id) },
                reducer = ProfileReducer,
            ) {}

    sealed interface Action {
        data class AvatarLoaded(val avatarUrl: String): Action
        
        data object AvatarIsAbsent: Action
    }

    private inner class ProfileBootstrapper(
        private val userId: String
    ): CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch(Dispatchers.IO) {
                val result = avatarUseCase.get(userId)
                withContext(Dispatchers.Main) {
                    result.onSuccess {
                        dispatch(Action.AvatarLoaded(it))
                    }.onFailure {
                        dispatch(Action.AvatarIsAbsent)
                    }
                }
            }
        }
    }

    sealed interface Msg {
        data object AvatarIsAbsent: Msg

        data object AvatarLoading: Msg

        data object ShowDialog: Msg

        data object HideDialog: Msg
        
        data class AvatarLoaded(val avatarUrl: String): Msg
    }

    private inner class ProfileExecutor(
        private val userId: String
    ): CoroutineExecutor<ProfileStore.Intent, Action, ProfileStore.State, Msg, ProfileStore.Label>() {

        override fun executeAction(action: Action) {
            Timber.i("${this::class.simpleName}: Action is obtained: $action")
            when (action) {
                Action.AvatarIsAbsent -> dispatch(Msg.AvatarIsAbsent)
                is Action.AvatarLoaded -> dispatch(Msg.AvatarLoaded(action.avatarUrl))
            }
        }

        override fun executeIntent(intent: ProfileStore.Intent) {
            Timber.i("${this::class.simpleName}: Intent is obtained: $intent")
            when (intent) {
                ProfileStore.Intent.Exit -> {
                    scope.launch(Dispatchers.IO) {
                        userUseCase.exit()
                        withContext(Dispatchers.Main) {
                            publish(ProfileStore.Label.Exited)
                        }
                    }
                }

                is ProfileStore.Intent.ModifyUser -> {
                    scope.launch(Dispatchers.IO) {
                        userUseCase.modify(intent.newValue)
                    }
                }

                is ProfileStore.Intent.LoadScores ->
                    publish(ProfileStore.Label.OpenScores(userId))

                is ProfileStore.Intent.SaveAvatar -> saveAvatar(intent.uri, userId)

                ProfileStore.Intent.HideDialog -> dispatch(Msg.HideDialog)

                ProfileStore.Intent.ShowDialog -> dispatch(Msg.ShowDialog)
            }
        }

        private fun showMessage(msgId: Int) {
            scope.launch(Dispatchers.Main) {
                val text = application.getString(msgId)
                Toast.makeText(application, text, Toast.LENGTH_SHORT).show()
            }
        }

        private fun saveAvatar(uri: Uri?, userId: String) {
            dispatch(Msg.AvatarLoading)
            uri ?: return
            scope.launch(Dispatchers.IO) {
                avatarUseCase.save(userId, uri).onSuccess { url ->
                    withContext(Dispatchers.Main) {
                        dispatch(Msg.AvatarLoaded(url))
                    }
                }.onFailure {
                    showMessage( R.string.avatar_read_error)
                    withContext(Dispatchers.Main) {
                        dispatch(Msg.AvatarIsAbsent)
                    }
                }
            }
        }
    }

    private object ProfileReducer: Reducer<ProfileStore.State, Msg> {
        override fun ProfileStore.State.reduce(msg: Msg): ProfileStore.State {
            Timber.i("${this::class.simpleName}: Message is obtained: $msg")
            return when (msg) {
                Msg.AvatarIsAbsent -> copy(avatarState = ProfileStore.State.AvatarState.AvatarIsAbsent)
                is Msg.AvatarLoaded -> copy(avatarState = ProfileStore.State.AvatarState.Loaded(msg.avatarUrl))
                Msg.AvatarLoading -> copy(avatarState = ProfileStore.State.AvatarState.Updating)
                Msg.HideDialog -> copy(isDialogShown = false)
                Msg.ShowDialog -> copy(isDialogShown = true)
            }
        }
    }

    private fun Calendar.formatAsDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return application.getString(R.string.register_date) + " " + dateFormat.format(this.time)
    }
}