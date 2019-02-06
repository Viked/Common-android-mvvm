package com.viked.commonandroidmvvm.ui.adapters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.viked.commonandroidmvvm.ui.common.click.BaseClickComponent
import com.viked.commonandroidmvvm.ui.common.click.ClickComponent
import kotlin.reflect.KClass

/**
 * Created by Viked on 12/31/2016.
 */
open class AdapterDelegate(private val itemWrapperClass: KClass<out ItemWrapper>,
                           private val inflater: LayoutInflater,
                           private val layoutId: Int,
                           private val itemId: Int,
                           private val delegateId: Int = -1) : AdapterDelegate<List<ItemWrapper>>() {

    var onItemClickListener: ClickComponent = BaseClickComponent { v, i -> false }
    var onLongClickListener: ClickComponent = BaseClickComponent { v, i -> false }

    private val onUpdateCallback = mutableListOf<(ItemWrapper) -> Unit>()

    fun addOnUpdateCallback(consumer: (ItemWrapper) -> Unit) = onUpdateCallback.add(consumer)

    fun update(item: ItemWrapper) = onUpdateCallback.forEach { it.invoke(item) }

    override fun isForViewType(items: List<ItemWrapper>, position: Int) = items[position]::class == itemWrapperClass

    open fun setOnClickListeners(holder: BindingViewHolder) {
        holder.binding.root.setOnClickListener { onItemClickListener.handleClick(it, holder.adapterPosition) }
        holder.binding.root.setOnLongClickListener {
            onLongClickListener.handleClick(it, holder.adapterPosition)
            return@setOnLongClickListener true
        }
    }

    override fun onBindViewHolder(items: List<ItemWrapper>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as BindingViewHolder).binding.setVariable(itemId, items[position])
    }

    public override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val viewHolder = BindingViewHolder(DataBindingUtil.inflate(inflater, layoutId, parent, false)).apply { }
        setOnClickListeners(viewHolder)
        viewHolder.binding.setVariable(delegateId, this)
        return viewHolder
    }

}