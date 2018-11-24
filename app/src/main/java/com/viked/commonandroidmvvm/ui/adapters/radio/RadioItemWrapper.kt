package com.viked.commonandroidmvvm.ui.adapters.radio

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.SelectableItemWrapper

/**
 * Created by yevgeniishein on 2/24/18.
 */
abstract class RadioItemWrapper(value: Any, val titleId: Int) : SelectableItemWrapper(value = value, name = TextWrapper(titleId))