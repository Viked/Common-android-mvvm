package com.viked.commonandroidmvvm.ui.common.delegate

import android.databinding.Observable

/**
 * Created by shein on 1/9/2018.
 */
abstract class BaseViewDelegate<T : Observable> : ViewDelegate<T> {

    protected var observable: T? = null

    override fun subscribe(observable: T) {
        this.observable = observable
        observable.addOnPropertyChangedCallback(callback)
        update()
    }

    override fun unsubscribe() {
        observable?.removeOnPropertyChangedCallback(callback)
    }

    private val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            update()
        }
    }

}