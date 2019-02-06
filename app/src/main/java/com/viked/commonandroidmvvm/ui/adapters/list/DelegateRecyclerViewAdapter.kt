package com.viked.commonandroidmvvm.ui.adapters.list

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

/**
 * Created by Viked on 12/24/2016.
 */
class DelegateRecyclerViewAdapter : AsyncListDifferDelegationAdapter<ItemWrapper>(ItemWrapperDiffCallback) {

    fun addDelegate(delegate: AdapterDelegate<List<ItemWrapper>>) {
        delegatesManager.addDelegate(delegate)
    }

    override fun getItemId(position: Int) = position.toLong()
}