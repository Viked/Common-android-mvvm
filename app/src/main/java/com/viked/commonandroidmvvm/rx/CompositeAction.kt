package com.viked.commonandroidmvvm.rx

import io.reactivex.functions.Action

/**
 * Created by yevgeniishein on 10/9/17.
 */
class CompositeAction : Action {

    private val consumers: MutableList<Action> = mutableListOf()

    fun add(consumer: Action) = consumers.add(consumer)

    override fun run() {
        consumers.forEach { it.run() }
    }
}