package com.viked.commonandroidmvvm.ui.adapters.spinner

import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.emptyTextWrapper

/**
 * Created by shein on 2/1/2018.
 */
class SpinnerData {
    var selected: ItemWrapper = emptyTextWrapper
    var list: List<ItemWrapper> = listOf()

    fun hasSelected() = list.isNotEmpty() && list.contains(selected)
}