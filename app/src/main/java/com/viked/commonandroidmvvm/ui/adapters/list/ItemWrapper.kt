package com.viked.commonandroidmvvm.ui.adapters.list

import androidx.recyclerview.widget.DiffUtil
import com.viked.commonandroidmvvm.text.TextWrapper

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class ItemWrapper(val itemId: Long, val value: Any, val name: TextWrapper = TextWrapper()) {

    open fun areItemsTheSame(oldItem: ItemWrapper): Boolean {
        return oldItem.itemId == itemId
    }

    open fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        return oldItem.value == value
    }
}

val emptyTextWrapper = ItemWrapper(0L, "")

object ItemWrapperDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {

    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return newItem.areItemsTheSame(oldItem)
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return newItem.areContentsTheSame(oldItem)
    }
}