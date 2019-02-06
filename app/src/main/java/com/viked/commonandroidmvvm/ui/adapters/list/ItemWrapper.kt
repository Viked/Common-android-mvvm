package com.viked.commonandroidmvvm.ui.adapters.list

import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.DiffUtil
import com.viked.commonandroidmvvm.text.TextWrapper

/**
 * Created by yevgeniishein on 10/9/17.
 */
open class ItemWrapper(val value: Any, val name: TextWrapper = TextWrapper()) {

    var updated = false

    open fun areItemsTheSame(oldItem: ItemWrapper): Boolean {
        return oldItem.value == value
    }

    open fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        return !oldItem.updated && !updated
    }

}

open class SelectableItemWrapper(value: Any, name: TextWrapper = TextWrapper()) : ItemWrapper(value, name) {

    val selected = ObservableBoolean(false)
}

val emptyTextWrapper = ItemWrapper("")

object ItemWrapperDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {

    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return newItem.areItemsTheSame(oldItem)
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return newItem.areContentsTheSame(oldItem)
    }
}