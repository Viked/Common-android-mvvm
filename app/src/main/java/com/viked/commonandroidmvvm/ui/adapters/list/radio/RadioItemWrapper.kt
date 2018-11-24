package com.viked.commonandroidmvvm.ui.adapters.list.radio

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.CheckListData

class RadioItemWrapper(val data: CheckListData) : ItemWrapper(data, name = TextWrapper(data.getTitle()))