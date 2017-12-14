package com.viked.commonandroidmvvm.ui.list

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.viked.commonandroidmvvm.ui.list.click.BaseClickComponent
import com.viked.commonandroidmvvm.ui.list.click.ClickComponent

/**
 * Created by Viked on 12/31/2016.
 */
abstract class BaseAdapterDelegate<T : ViewDataBinding>(val inflater: LayoutInflater) : AdapterDelegate<List<ItemWrapper>>() {

    var onItemClickListener: ClickComponent = BaseClickComponent { v, i -> false }
    var onLongClickListener: ClickComponent = BaseClickComponent { v, i -> false }

    abstract val layoutId: Int

    abstract fun bindViewHolder(holder: BindingViewHolder<T>, item: ItemWrapper)

    abstract fun getItemFromBinding(binding: T): ItemWrapper?

    open fun setOnClickListeners(binding: T) {
        binding.root.setOnClickListener { getItemFromBinding(binding)?.run { onItemClickListener.handleClick(it, this) } }
        binding.root.setOnClickListener { getItemFromBinding(binding)?.run { onLongClickListener.handleClick(it, this) } }
    }

    override fun onBindViewHolder(items: List<ItemWrapper>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        bindViewHolder(holder as BindingViewHolder<T>, items[position])
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            BindingViewHolder<T>(DataBindingUtil.inflate(inflater, layoutId, parent, false)).apply { setOnClickListeners(binding) }

}