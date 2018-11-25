package com.viked.commonandroidmvvm.ui.adapters.list

import android.arch.lifecycle.Observer
import android.view.View
import com.viked.commonandroidmvvm.ui.data.Resource

class VisibilityDelegate(private val view: View,
                         private val displayIfDataNotNull: Boolean = true,
                         private val displayIfDataNotEmpty: Boolean = false,
                         private val condition: ((Resource<List<ItemWrapper>>) -> Boolean)? = null,
                         private val invisibleState: Int = View.GONE) : Observer<Resource<List<ItemWrapper>>> {

    override fun onChanged(t: Resource<List<ItemWrapper>>?) {
        val visible = when {
            t == null -> false
            condition != null -> condition.invoke(t)
            t.data == null -> !displayIfDataNotNull
            t.data.isEmpty() -> !displayIfDataNotEmpty
            else -> true
        }
        view.visibility = if (visible) View.VISIBLE else invisibleState
    }

}