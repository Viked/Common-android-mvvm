package com.viked.commonandroidmvvm.ui.dialog.purchase

import android.view.LayoutInflater
import com.viked.commonandroidmvvm.BR
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.ItemPurchaseBinding
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.BindingViewHolder

/**
 * Created by yevgeniishein on 3/12/18.
 */
class PurchaseDelegate(inflater: LayoutInflater) : AdapterDelegate(PurchaseItemWrapper::class, inflater, R.layout.item_purchase, BR.viewModel) {

    override fun setOnClickListeners(holder: BindingViewHolder) {
        val binding = holder.binding
        if (binding is ItemPurchaseBinding) {
            binding.stateButton.setOnClickListener { onItemClickListener.handleClick(it, holder.adapterPosition) }
        }
    }
}