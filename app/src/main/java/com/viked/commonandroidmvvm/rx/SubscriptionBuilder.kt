package com.viked.commonandroidmvvm.rx

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import org.reactivestreams.Subscription

/**
 * Created by yevgeniishein on 11/5/17.
 */
abstract class SubscriptionBuilder<T> {

    protected val onNext: CompositeConsumer<T> = CompositeConsumer()
    protected val onError: CompositeConsumer<Throwable> = CompositeConsumer()
    protected val onComplete: CompositeAction = CompositeAction()
    protected val onSubscribe: CompositeConsumer<Subscription> = CompositeConsumer()

    fun addOnNext(consumer: (T) -> Unit) = apply { onNext.add(consumer) }
    fun addOnError(consumer: (Throwable) -> Unit) = apply { onError.add(consumer) }
    fun addOnComplete(consumer: () -> Unit) = apply { onComplete.add(Action { consumer.invoke() }) }
    fun addOnSubscribe(consumer: (Subscription) -> Unit) = apply { onSubscribe.add(consumer) }

    abstract fun subscribe(): Disposable

}

fun <T> Flowable<T>.buildSubscription() = object : SubscriptionBuilder<T>() {
    override fun subscribe(): Disposable {
        return subscribe(onNext, onError, onComplete, onSubscribe)
    }
}

fun <T> Single<T>.buildSubscription() = object : SubscriptionBuilder<T>() {
    override fun subscribe(): Disposable {
        return subscribe(onNext, onError)
    }
}

fun <T> Maybe<T>.buildSubscription() = object : SubscriptionBuilder<T>() {
    override fun subscribe(): Disposable {
        return subscribe(onNext, onError, onComplete)
    }
}

fun Completable.buildSubscription() = object : SubscriptionBuilder<Any>() {
    override fun subscribe(): Disposable {
        return subscribe(onComplete, onError)
    }
}

