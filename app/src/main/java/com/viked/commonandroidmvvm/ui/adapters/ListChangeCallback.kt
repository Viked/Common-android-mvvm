package com.viked.commonandroidmvvm.ui.adapters

import android.databinding.ObservableList
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 2/24/18.
 */
open class ListChangeCallback(private val callback: Runnable) : ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>>() {

    override fun onChanged(sender: ObservableList<ItemWrapper>?) {
        callback.run()
    }

    override fun onItemRangeRemoved(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
        callback.run()
    }

    override fun onItemRangeMoved(sender: ObservableList<ItemWrapper>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
        callback.run()
    }

    override fun onItemRangeInserted(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
        callback.run()
    }

    override fun onItemRangeChanged(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
        callback.run()
    }
}