package com.viked.commonandroidmvvm.preference.file.manager

import android.view.LayoutInflater
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.ItemFileBinding
import com.viked.commonandroidmvvm.ui.adapters.list.BaseAdapterDelegate
import com.viked.commonandroidmvvm.ui.adapters.list.BindingViewHolder
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.common.click.ClickDecorator
import java.io.File


/**
 * Created by Viked on 12/31/2016.
 */
class FileAdapterDelegate(inflater: LayoutInflater) : BaseAdapterDelegate<ItemFileBinding>(inflater) {

    override fun isForViewType(items: List<ItemWrapper>, position: Int) = items[position] is FileItemWrapper

    override val layoutId = R.layout.item_file

    override fun bindViewHolder(holder: BindingViewHolder<ItemFileBinding>, item: ItemWrapper) {
        holder.binding.viewModel = item as FileItemWrapper
    }

    override fun getItemFromBinding(binding: ItemFileBinding) = binding.viewModel

}