package com.viked.commonandroidmvvm.ui.adapters.list

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BindingViewHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)