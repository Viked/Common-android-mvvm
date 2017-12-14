package com.viked.commonandroidmvvm.rx

import io.reactivex.functions.Consumer

/**
 * Created by yevgeniishein on 10/9/17.
 */
class CompositeConsumer<T> : Consumer<T> {

    private val consumers: MutableList<Consumer<T>> = mutableListOf()

    fun add(consumer: (T) -> Unit) {
        consumers.add(Consumer { consumer.invoke(it) })
    }

    override fun accept(t: T) {
        consumers.forEach { it.accept(t) }
    }
}