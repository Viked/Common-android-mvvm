package com.viked.commonandroidmvvm.ui.adapters.list

import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.text.TextWrapper

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class ItemWrapper(val value: Any, val name: TextWrapper = TextWrapper())

open class SelectableItemWrapper(value: Any, name: TextWrapper = TextWrapper()) : ItemWrapper(value, name) {

    val selected = ObservableBoolean(false)
}

val emptyTextWrapper = ItemWrapper("")