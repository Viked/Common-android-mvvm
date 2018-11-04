package com.viked.commonandroidmvvm.ui.adapters.list

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.ListChangeCallback

class AdapterBehavior(private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, private val items: ObservableList<ItemWrapper>) : AdapterDelegate {

    override fun subscribe() {
        items.addOnListChangedCallback(onListChangeCallback)
        update()
    }

    override fun unsubscribe() {
        items.removeOnListChangedCallback(onListChangeCallback)
    }

    override fun update() {
        adapter.notifyDataSetChanged()
    }

    fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeRemoved(positionStart, itemCount)
    }

    fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeChanged(positionStart, itemCount)
    }

    fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    private val onListChangeCallback =
            object : ListChangeCallback(Runnable { update() }) {
                override fun onItemRangeRemoved(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    onItemRangeRemoved(positionStart, itemCount)
                }

                override fun onItemRangeChanged(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    onItemRangeChanged(positionStart, itemCount)
                }

                override fun onItemRangeInserted(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    onItemRangeInserted(positionStart, itemCount)
                }
            }

}