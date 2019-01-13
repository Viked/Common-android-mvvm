package com.viked.commonandroidmvvm.ui.adapters.list

import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.ui.common.click.BlockerClickDecorator

/**
 * Created by yevgeniishein on 7/24/17.
 */
class SelectableDelegateRecyclerViewAdapter(private val selectMode: ObservableBoolean) : DelegateRecyclerViewAdapter() {

    private fun handleItemPressed(item: ItemWrapper): Boolean {
        return if (selectMode.get() && item is SelectableItemWrapper) {
            item.selected.set(!item.selected.get())
            selectMode.set(getSelectedItems().isNotEmpty())
            selectMode.notifyChange()
            notifyItemChanged(items.indexOf(item))
            true
        } else {
            false
        }
    }

    private fun getSelectedItems(): List<ItemWrapper> = items.filter { it is SelectableItemWrapper && it.selected.get() }

    override fun addDelegate(delegate: com.hannesdorfmann.adapterdelegates3.AdapterDelegate<List<ItemWrapper>>) {
        if (delegate is AdapterDelegate) {
            delegate.onItemClickListener = BlockerClickDecorator(delegate.onItemClickListener) { view, item -> handleItemPressed(items[item]) }
            delegate.onLongClickListener = BlockerClickDecorator(delegate.onLongClickListener) { view, item -> handleLongClick(items[item]) }
        }
        super.addDelegate(delegate)
    }

    private fun handleLongClick(item: ItemWrapper): Boolean {
        return if (item is SelectableItemWrapper) {
            item.selected.set(true)
            selectMode.set(true)
            true
        } else {
            false
        }
    }

}