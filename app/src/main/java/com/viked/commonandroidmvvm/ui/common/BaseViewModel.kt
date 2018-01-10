package com.viked.commonandroidmvvm.ui.common

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.SparseArray
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.common.delegate.error.BaseError
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class BaseViewModel(val titleId: Int = 0) : ViewModel() {

    private val subscriptionsHolder = SparseArray<Disposable>()

    private val subscription: CompositeDisposable = CompositeDisposable()

    val progress = ObservableBoolean(false)

    val error = ObservableField<BaseError>()

    val title: ObservableField<TextWrapper> = ObservableField(TextWrapper(titleId))

    open fun loadData() {
        //request initial data
    }

    override public fun onCleared() {
        subscription.dispose()
        subscriptionsHolder.clear()
        super.onCleared()
    }

    fun <T> subscribe(subscriptionBuilder: () -> SubscriptionBuilder<T>,
                      key: Int = 0,
                      silent: Boolean = false,
                      force: Boolean = false) {
        val disposable = subscriptionsHolder[key]
        if (disposable != null) {
            if (force && !disposable.isDisposed) {
                disposable.dispose()
            }
            if (!disposable.isDisposed) {
                return
            }
        }
        if (!silent) {
            progress.set(true)
        }
        subscriptionsHolder.append(key, subscriptionBuilder.invoke().subscribeOnModel(silent))
    }

    private fun <T> SubscriptionBuilder<T>.subscribeOnModel(silent: Boolean): Disposable = this
            .setProgress(silent)
            .addOnError { it.log() }
            .addOnError {
                if (it is BaseError) error.set(it)
                else if (it.cause != null && it.cause is BaseError) error.set(it.cause as BaseError)
            }
            .subscribe()
            .also { subscription.add(it) }

    private fun <T> SubscriptionBuilder<T>.setProgress(silent: Boolean) = apply {
        if (!silent) this
                .addOnSubscribe { progress.set(true) }
                .addOnComplete { progress.set(false) }
                .addOnDispose { progress.set(false) }
                .addOnError { progress.set(false) }
    }
}