package com.viked.commonandroidmvvm.ui.adapters.list.check

import android.view.LayoutInflater
import com.viked.commonandroidmvvm.BR
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterDelegate

open class CheckAdapterDelegate(inflater: LayoutInflater) : AdapterDelegate(CheckItemWrapper::class, inflater, R.layout.item_check, BR.item, BR.delegate) {

    fun update(item: CheckItemWrapper, checked: Boolean) {
        if (checked != item.data.checked) {
            item.data.checked = checked
            update(item)
        }
    }

}