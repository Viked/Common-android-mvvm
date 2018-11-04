package com.viked.commonandroidmvvm.ui.list.radio

import android.view.LayoutInflater
import android.view.ViewGroup
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.ItemRadioBinding
import com.viked.commonandroidmvvm.ui.adapters.list.BaseAdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.BindingViewHolder
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

class RadioAdapterDelegate(inflater: LayoutInflater, private val set: (RadioItemWrapper) -> Unit) : BaseAdapterDelegate<ItemRadioBinding>(inflater) {

    override fun isForViewType(items: List<ItemWrapper>, position: Int) = items[position] is RadioItemWrapper

    override val layoutId = R.layout.item_radio

    override fun onCreateViewHolder(parent: ViewGroup) =
            super.onCreateViewHolder(parent).also { (it as BindingViewHolder<ItemRadioBinding>).binding.delegate = this }

    override fun bindViewHolder(holder: BindingViewHolder<ItemRadioBinding>, item: ItemWrapper) {
        holder.binding.viewModel = item as RadioItemWrapper
    }

    override fun getItemFromBinding(binding: ItemRadioBinding) = binding.viewModel

    fun update(item: RadioItemWrapper, checked: Boolean) {
        if (checked && !item.data.checked) {
            item.data.checked = checked
            set(item)
        }
    }

}