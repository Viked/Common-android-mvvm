package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableBoolean

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class ItemWrapper(val value: Any, val selectable: Boolean = false, val name: String = "") {

    val selected = ObservableBoolean(false)
}