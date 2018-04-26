package com.viked.commonandroidmvvm.ui.fragment

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.SparseArray
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder
import com.viked.commonandroidmvvm.rx.buildSubscription
import com.viked.commonandroidmvvm.rx.isInternetAvailable
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.binding.ProgressObservable
import com.viked.commonandroidmvvm.ui.common.delegate.error.BaseError
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class BaseViewModel(val titleId: Int = 0) : ViewModel() {

    private val subscriptionsHolder = SparseArray<Disposable>()

    private var subscription: CompositeDisposable? = null

    val progress = ProgressObservable(false)

    val error = ObservableField<BaseError>()

    val connection = ObservableBoolean(false)

    val title: ObservableField<TextWrapper> = ObservableField(TextWrapper(titleId))

    open fun loadData() {
        subscribe({ isInternetAvailable().buildSubscription().addOnNext { connection.set(it) } }, -10001, true, true)
        //request initial data
    }

    open fun onInit() {
        subscription = CompositeDisposable()
    }

    override public fun onCleared() {
        subscription?.dispose()
        subscriptionsHolder.clear()
        subscription = null
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
        subscriptionsHolder.append(key, subscriptionBuilder.invoke().subscribeOnModel(silent, key))
    }

    private fun <T> SubscriptionBuilder<T>.subscribeOnModel(silent: Boolean, key: Int): Disposable = this
            .setProgress(silent, key)
            .addOnError {
                if (it is BaseError) error.set(it)
                else if (it.cause != null && it.cause is BaseError) error.set(it.cause as BaseError)
            }
            .subscribe()
            .also { subscription?.add(it) }

    private fun <T> SubscriptionBuilder<T>.setProgress(silent: Boolean, id: Int) = apply {
        if (!silent) this
                .addOnSubscribe { progress + id }
                .addOnComplete { progress - id }
                .addOnDispose { progress - id }
                .addOnError { progress - id }
    }
}