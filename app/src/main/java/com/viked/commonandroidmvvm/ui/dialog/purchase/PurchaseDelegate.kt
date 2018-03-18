package com.viked.commonandroidmvvm.ui.dialog.purchase

import android.view.LayoutInflater
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.ItemPurchaseBinding
import com.viked.commonandroidmvvm.ui.adapters.list.BaseAdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.BindingViewHolder
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 3/12/18.
 */
class PurchaseDelegate(inflater: LayoutInflater) : BaseAdapterDelegate<ItemPurchaseBinding>(inflater) {

    override fun isForViewType(items: List<ItemWrapper>, position: Int) = items[position] is PurchaseItemWrapper

    override val layoutId = R.layout.item_purchase

    override fun bindViewHolder(holder: BindingViewHolder<ItemPurchaseBinding>, item: ItemWrapper) {
        holder.binding.viewModel = item as PurchaseItemWrapper
    }

    override fun getItemFromBinding(binding: ItemPurchaseBinding): ItemWrapper? = binding.viewModel
}