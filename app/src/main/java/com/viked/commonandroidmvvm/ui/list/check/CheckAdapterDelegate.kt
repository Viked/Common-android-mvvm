package com.viked.commonandroidmvvm.ui.list.check

import android.view.LayoutInflater
import android.view.ViewGroup
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.ItemCheckBinding
import com.viked.commonandroidmvvm.ui.adapters.list.BaseAdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.BindingViewHolder
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

class CheckAdapterDelegate(inflater: LayoutInflater) : BaseAdapterDelegate<ItemCheckBinding>(inflater) {

    override fun isForViewType(items: List<ItemWrapper>, position: Int) = items[position] is CheckItemWrapper

    override val layoutId = R.layout.item_check

    override fun onCreateViewHolder(parent: ViewGroup) =
            super.onCreateViewHolder(parent).also { (it as BindingViewHolder<ItemCheckBinding>).binding.delegate = this }

    override fun bindViewHolder(holder: BindingViewHolder<ItemCheckBinding>, item: ItemWrapper) {
        holder.binding.viewModel = item as CheckItemWrapper
    }

    override fun getItemFromBinding(binding: ItemCheckBinding) = binding.viewModel

    fun update(item: CheckItemWrapper, checked: Boolean) {
        if (item.data.checked != checked) {
            item.data.checked = checked
            update(item)
        }
    }

}