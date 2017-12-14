package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableBoolean

/**
 * Created by yevgeniishein on 10/9/17.
 */
abstract class ItemWrapper(val value: Any) {

    abstract val selectable: Boolean

    val selected = ObservableBoolean(false)
}