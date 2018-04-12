package com.viked.commonandroidmvvm.ui.adapters.list

import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.viked.commonandroidmvvm.ui.common.click.BlockerClickDecorator

/**
 * Created by yevgeniishein on 7/24/17.
 */
class SelectableDelegateRecyclerViewAdapter(private val selectMode: ObservableBoolean,
                                            items: ObservableList<ItemWrapper>) : DelegateRecyclerViewAdapter(items) {

    private fun handleItemPressed(item: ItemWrapper): Boolean {
        return if (selectMode.get() && item.selectable) {
            item.selected.set(!item.selected.get())
            selectMode.set(getSelectedItems().isNotEmpty())
            selectMode.notifyChange()
            notifyItemChanged(items.indexOf(item))
            true
        } else {
            false
        }
    }

    private fun getSelectedItems(): List<ItemWrapper> = items.filter { it.selectable && it.selected.get() }

    override fun addDelegate(delegate: BaseAdapterDelegate<*>) {
        delegate.onItemClickListener = BlockerClickDecorator(delegate.onItemClickListener, { view, item -> handleItemPressed(item) })
        delegate.onLongClickListener = BlockerClickDecorator(delegate.onLongClickListener, { view, item -> handleLongClick(item) })
        super.addDelegate(delegate)
    }

    private fun handleLongClick(item: ItemWrapper): Boolean {
        return if (item.selectable) {
            item.selected.set(true)
            selectMode.set(true)
            true
        } else {
            false
        }
    }

}