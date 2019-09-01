package com.viked.commonandroidmvvm.ui.adapters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
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

    var onItemClickListener: ClickComponent = BaseClickComponent { false }
    var onLongClickListener: ClickComponent = BaseClickComponent { false }

    private val onUpdateCallback = mutableListOf<(ItemWrapper) -> Unit>()

    fun addOnUpdateCallback(consumer: (ItemWrapper) -> Unit) = onUpdateCallback.add(consumer)

    fun update(item: ItemWrapper) = onUpdateCallback.forEach { it.invoke(item) }

    override fun isForViewType(items: List<ItemWrapper>, position: Int) = items[position]::class == itemWrapperClass

    open fun setOnClickListeners(holder: BindingViewHolder) {
        holder.binding.root.setOnClickListener {
            holder.onSureClick(onItemClickListener)
        }
        holder.binding.root.setOnLongClickListener {
            holder.onSureClick(onLongClickListener)
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

    protected fun RecyclerView.ViewHolder.onSureClick(onClick: ClickComponent): Boolean {
        val position = adapterPosition
        if (position == NO_POSITION) return false
        return onClick.handleClick(position)
    }
}