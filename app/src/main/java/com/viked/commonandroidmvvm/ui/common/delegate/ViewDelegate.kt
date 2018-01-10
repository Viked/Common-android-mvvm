package com.viked.commonandroidmvvm.ui.common.delegate

import android.databinding.Observable

/**
 * Created by shein on 1/9/2018.
 */
interface ViewDelegate<in T : Observable> {

    fun subscribe(observable: T)

    fun unsubscribe()

    fun update()

}