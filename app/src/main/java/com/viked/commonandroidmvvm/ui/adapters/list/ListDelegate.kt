package com.viked.commonandroidmvvm.ui.adapters.list

import androidx.lifecycle.Observer
import android.view.View
import android.widget.TextView
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.data.Resource

class ListDelegate(private val statusTextView: TextView, private val adapter: DelegateRecyclerViewAdapter) : Observer<Resource<List<ItemWrapper>>> {

    override fun onChanged(value: Resource<List<ItemWrapper>>) {
        when {
            value.status != null -> {
                adapter.items = listOf()
                statusTextView.text = value.status[statusTextView.context]
                statusTextView.visibility = View.VISIBLE
            }
            else -> {
                adapter.items = value.data ?: listOf()
                statusTextView.visibility = View.GONE
            }
        }
    }

}