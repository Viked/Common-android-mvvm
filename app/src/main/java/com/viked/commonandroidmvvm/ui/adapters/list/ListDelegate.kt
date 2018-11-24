package com.viked.commonandroidmvvm.ui.adapters.list

import android.arch.lifecycle.Observer
import android.view.View
import android.widget.TextView
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.data.Resource

class ListDelegate(private val statusTextView: TextView, private val adapter: DelegateRecyclerViewAdapter) : Observer<Resource<List<ItemWrapper>>> {

    override fun onChanged(t: Resource<List<ItemWrapper>>?) {
        when {
            t == null -> {
                adapter.items = listOf()
                statusTextView.setText(R.string.empty_list)
                statusTextView.visibility = View.VISIBLE
            }
            t.status != null -> {
                adapter.items = listOf()
                statusTextView.text = t.status[statusTextView.context]
                statusTextView.visibility = View.VISIBLE
            }
            else -> {
                adapter.items = t.data ?: listOf()
                statusTextView.visibility = View.GONE
            }
        }
    }

}