package com.viked.commonandroidmvvm.ui.adapters.list.check

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.CheckListData
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

class CheckItemWrapper(val data: CheckListData,
                       val enabled: Boolean = true,
                       val title: () -> TextWrapper = { TextWrapper(data.getTitle()) },
                       val subtitle: () -> TextWrapper = { TextWrapper(data.getSubtitle()) }) : ItemWrapper(data.id, data, name = TextWrapper(data.getTitle())) {

    override fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        if (oldItem !is CheckItemWrapper) return false
        return oldItem.enabled == enabled
                && oldItem.data.getTitle() == data.getTitle()
                && oldItem.data.getSubtitle() == data.getSubtitle()
                && oldItem.data.checked == data.checked
    }
}


