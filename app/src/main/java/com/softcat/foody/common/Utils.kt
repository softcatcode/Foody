package com.softcat.foody.common

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.decompose.Cancellation
import com.arkivanov.mvikotlin.core.rx.observer

fun <State : Any> Store<*, State, *>.asValue(lifecycle: Lifecycle): Value<State> =
    object : Value<State>() {
        override val value: State
            get() = state

        override fun subscribe(observer: (State) -> Unit): Cancellation {
            // Подписываемся на изменения стейта MVIKotlin Store
            val disposable = states(observer(onNext = observer))

            // Если компонент полностью уничтожается, отменяем подписку
            lifecycle.doOnDestroy { disposable.dispose() }

            return Cancellation {
                disposable.dispose()
            }
        }
    }
