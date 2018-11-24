package com.viked.commonandroidmvvm.ui.adapters.list.radio

import android.view.LayoutInflater
import com.viked.commonandroidmvvm.BR
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterDelegate

class RadioAdapterDelegate(inflater: LayoutInflater) : AdapterDelegate(RadioItemWrapper::class, inflater, R.layout.item_radio, BR.item, BR.delegate) {

    fun update(item: RadioItemWrapper, checked: Boolean) {
        if (checked && !item.data.checked) {
            item.data.checked = checked
            update(item)
        }
    }

}