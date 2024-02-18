package com.viked.commonandroidmvvm.ui.adapters.list

import androidx.lifecycle.Observer
import android.view.View
import com.viked.commonandroidmvvm.ui.data.Resource

class ListVisibilityDelegate(private val view: View,
                             private val displayIfDataNotNull: Boolean = true,
                             private val displayIfDataNotEmpty: Boolean = false,
                             private val condition: ((Resource<List<ItemWrapper>>) -> Boolean)? = null,
                             private val invisibleState: Int = View.GONE) : Observer<Resource<List<ItemWrapper>>> {

    override fun onChanged(value: Resource<List<ItemWrapper>>) {
        val visible = when {
            condition != null -> condition.invoke(value)
            value.data == null -> !displayIfDataNotNull
            value.data.isEmpty() -> !displayIfDataNotEmpty
            else -> true
        }
        view.visibility = if (visible) View.VISIBLE else invisibleState
    }

}

class VisibilityDelegate(private val view: View,
                         private val invisibleState: Int = View.GONE) : Observer<Boolean> {

    override fun onChanged(value: Boolean) {
        view.visibility = if (value == true) View.VISIBLE else invisibleState
    }

}