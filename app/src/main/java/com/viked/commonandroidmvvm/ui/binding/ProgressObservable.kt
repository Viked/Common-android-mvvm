package com.viked.commonandroidmvvm.ui.binding

import android.databinding.ObservableBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by shein on 1/11/2018.
 */
class ProgressObservable(value: Boolean = false) : ObservableBoolean(value) {

    val count = AtomicInteger(0)

    override fun set(value: Boolean) {
        val count = if (value) {
            count.incrementAndGet()
        } else {
            count.decrementAndGet()
        }
        super.set(count > 0)
    }
}