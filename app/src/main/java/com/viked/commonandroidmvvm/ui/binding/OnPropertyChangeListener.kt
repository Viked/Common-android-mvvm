package com.viked.commonandroidmvvm.ui.binding

import android.databinding.Observable

/**
 * Created by yevgeniishein on 12/24/17.
 */
class OnPropertyChangeWrapper<T : Observable>(val observable: T, val callback: (T) -> Unit) : Observable.OnPropertyChangedCallback() {

    override fun onPropertyChanged(p0: Observable?, p1: Int) {
        if (p0 == observable) {
            callback.invoke(observable)
        }
    }
}

fun <T : Observable> T.addOnPropertyChangeListener(callback: (T) -> Unit) {
    addOnPropertyChangedCallback(OnPropertyChangeWrapper(this, callback))
    callback.invoke(this)
}