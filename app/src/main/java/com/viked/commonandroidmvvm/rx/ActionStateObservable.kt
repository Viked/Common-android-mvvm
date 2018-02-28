package com.viked.commonandroidmvvm.rx

import android.databinding.ObservableField
import android.os.Handler
import android.os.Looper

/**
 * Created by yevgeniishein on 12/25/17.
 */
class ActionStateObservable : ObservableField<State>(State.None) {

    fun onComplete() {
        Handler(Looper.getMainLooper()).post({ set(State.Complete) })
    }

    fun onError(t: Throwable) {
        Handler(Looper.getMainLooper()).post({ set(State.Error(t)) })
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