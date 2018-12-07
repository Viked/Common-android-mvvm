package com.viked.commonandroidmvvm.ui.adapters.list.check

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.CheckListData
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

class CheckItemWrapper(val data: CheckListData,
                       val title: () -> TextWrapper = { TextWrapper(data.getTitle()) },
                       val subtitle: () -> TextWrapper = { TextWrapper(data.getSubtitle()) }) : ItemWrapper(data, name = TextWrapper(data.getTitle()))
