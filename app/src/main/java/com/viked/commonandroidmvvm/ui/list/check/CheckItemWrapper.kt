package com.viked.commonandroidmvvm.ui.list.check

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.list.common.CheckListData

class CheckItemWrapper(val data: CheckListData) : ItemWrapper(data, name = TextWrapper(data.getTitle()))