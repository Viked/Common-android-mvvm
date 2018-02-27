package com.viked.commonandroidmvvm.ui.adapters.list

import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.text.TextWrapper

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class ItemWrapper(val value: Any, val selectable: Boolean = false, val name: TextWrapper = TextWrapper()) {

    val selected = ObservableBoolean(false)
}

val emptyTextWrapper = ItemWrapper("")