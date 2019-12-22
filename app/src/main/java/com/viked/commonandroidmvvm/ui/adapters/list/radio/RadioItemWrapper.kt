package com.viked.commonandroidmvvm.ui.adapters.list.radio

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.CheckListData
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

class RadioItemWrapper(
        val data: CheckListData,
        val title: () -> TextWrapper = { TextWrapper(data.getTitle()) },
        val subtitle: () -> TextWrapper = { TextWrapper(data.getSubtitle()) }) : ItemWrapper(data.id, data, name = TextWrapper(data.getTitle())) {

    override fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        if (oldItem !is RadioItemWrapper) return false
        return oldItem.data.getTitle() == data.getTitle()
                && oldItem.data.getSubtitle() == data.getSubtitle()
                && oldItem.data.checked == data.checked
    }
}