package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableList
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager


/**
 * Created by Viked on 12/24/2016.
 */
open class DelegateRecyclerViewAdapter(val items: ObservableList<ItemWrapper>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var delegateViewType = 0

    private val delegatesManager = AdapterDelegatesManager<List<ItemWrapper>>()

    fun setStartViewTypeIndex(index: Int) {
        if (index > 0) {
            delegateViewType = index
        } else {
            error("Index should be more than 0")
        }
    }

    open fun addDelegate(@NonNull delegate: BaseAdapterDelegate<*>) {
        delegatesManager.addDelegate(++delegateViewType, delegate)
    }

    override fun getItemViewType(position: Int): Int =
            delegatesManager.getItemViewType(items, position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegatesManager.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(items, position, holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<*>) {
        delegatesManager.onBindViewHolder(items, position, holder, payloads)
    }

    override fun getItemCount(): Int = items.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        items.addOnListChangedCallback(onListChangeCallback)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        items.removeOnListChangedCallback(onListChangeCallback)
    }

    private val onListChangeCallback: ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>> =
            object : ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>>() {
                override fun onItemRangeRemoved(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeRemoved(positionStart, itemCount)
                }

                override fun onItemRangeMoved(sender: ObservableList<ItemWrapper>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeChanged(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeChanged(positionStart, itemCount)
                }

                override fun onChanged(sender: ObservableList<ItemWrapper>?) {
                    notifyDataSetChanged()
                }

                override fun onItemRangeInserted(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeInserted(positionStart, itemCount)
                }
            }
}