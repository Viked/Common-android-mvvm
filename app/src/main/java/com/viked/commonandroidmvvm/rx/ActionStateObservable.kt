package com.viked.commonandroidmvvm.rx

import android.databinding.ObservableField

/**
 * Created by yevgeniishein on 12/25/17.
 */
class ActionStateObservable : ObservableField<State>(State.None) {

    fun onComplete() {
        set(State.Complete)
    }

    fun onError(t: Throwable) {
        set(State.Error(t))
    }
}

sealed class State {
    object None : State()
    object Complete : State()
    class Error(val t: Throwable) : State()
}

fun <T> SubscriptionBuilder<T>.subscribeOnActionState(actionStateObservable: ActionStateObservable) = this
        .addOnComplete { actionStateObservable.onComplete() }
        .addOnError { actionStateObservable.onError(it) }

fun SubscriptionBuilder<Boolean>.subscribeOnActionStateWithCheck(actionStateObservable: ActionStateObservable) = this
        .addOnNext { if (it) actionStateObservable.onComplete() else actionStateObservable.onError(RuntimeException()) }
        .addOnError { actionStateObservable.onError(it) }