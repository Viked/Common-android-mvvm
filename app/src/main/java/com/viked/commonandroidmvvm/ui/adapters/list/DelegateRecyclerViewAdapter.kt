package com.viked.commonandroidmvvm.ui.adapters.list

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager


/**
 * Created by Viked on 12/24/2016.
 */
open class DelegateRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var items = listOf<ItemWrapper>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var delegateViewType = 0

    private val delegatesManager = AdapterDelegatesManager<List<ItemWrapper>>()

    fun setStartViewTypeIndex(index: Int) {
        if (index > 0) {
            delegateViewType = index
        } else {
            error("Index should be more than 0")
        }
    }

    open fun addDelegate(delegate: AdapterDelegate) {
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

    override fun getItemId(position: Int) = position.toLong()
}