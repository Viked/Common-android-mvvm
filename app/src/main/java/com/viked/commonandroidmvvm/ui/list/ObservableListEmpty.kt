package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.viked.commonandroidmvvm.ui.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/28/17.
 */
class ObservableListEmpty(private val list: ObservableList<ItemWrapper>) : ObservableBoolean(true) {

    private val onListChangeCallback: ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>> =
            object : ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>>() {
                override fun onItemRangeRemoved(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    set(list.isEmpty())
                }

                override fun onItemRangeMoved(sender: ObservableList<ItemWrapper>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    set(list.isEmpty())
                }

                override fun onItemRangeChanged(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    set(list.isEmpty())
                }

                override fun onChanged(sender: ObservableList<ItemWrapper>?) {
                    set(list.isEmpty())
                }

                override fun onItemRangeInserted(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    set(list.isEmpty())
                }
            }

    init {
        list.addOnListChangedCallback(onListChangeCallback)
    }

    fun clear() {
        list.removeOnListChangedCallback(onListChangeCallback)
    }

}