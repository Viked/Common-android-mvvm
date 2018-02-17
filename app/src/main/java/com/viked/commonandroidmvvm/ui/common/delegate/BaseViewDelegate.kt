package com.viked.commonandroidmvvm.ui.common.delegate

import android.databinding.Observable

/**
 * Created by shein on 1/9/2018.
 */
abstract class BaseViewDelegate<T : Observable> : ViewDelegate<T> {

    private val mutableSet = mutableSetOf<Observable>()

    abstract fun updateWithValue(observables: Set<Observable>)

    override fun subscribe(observable: T) {
        observable.addOnPropertyChangedCallback(callback)
        mutableSet.add(observable)
        update()
    }

    override fun unsubscribe(observable: T) {
        observable.removeOnPropertyChangedCallback(callback)
        mutableSet.remove(observable)
    }

    override fun update() {
        updateWithValue(mutableSet)
    }

    private val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            update()
        }
    }

}