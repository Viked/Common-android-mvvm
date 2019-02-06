package com.viked.commonandroidmvvm.ui.binding

import androidx.databinding.ObservableBoolean

/**
 * Created by shein on 1/11/2018.
 */
class ProgressObservable(value: Boolean = false) : ObservableBoolean(value) {

    private val default = 0

    private val items = mutableSetOf<Int>()

    operator fun plus(id: Int) {
        items.add(id)
        set(items.isNotEmpty())
    }

    operator fun minus(id: Int) {
        items.remove(id)
        set(items.isNotEmpty())
    }

    override fun set(value: Boolean) {
        if (value && items.isEmpty()) {
            items.add(default)
        } else if (!value && items.count() == 1 && items.contains(default)) {
            items.remove(default)
        }
        super.set(value)
    }
}