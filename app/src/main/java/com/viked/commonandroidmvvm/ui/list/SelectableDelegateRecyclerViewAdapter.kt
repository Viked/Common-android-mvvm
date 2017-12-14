package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.list.click.BlockerClickDecorator

/**
 * Created by yevgeniishein on 7/24/17.
 */
class SelectableDelegateRecyclerViewAdapter(private val deleteAction: (List<ItemWrapper>) -> Unit,
                                            private val setSelectedItemsCount: (Int) -> Unit,
                                            items: ObservableList<ItemWrapper>) : DelegateRecyclerViewAdapter(items) {

    private var menu: Menu? = null
    var selectAllButton: MenuItem? = null
    var deleteButton: MenuItem? = null

    val unhidableMenuItemIds: MutableList<Int> = mutableListOf(R.id.select_all, R.id.delete)

    private var recyclerView: RecyclerView? = null

    private var selectableItemsCount = 0

    var edit = false
        set(value) {
            if (field != value) {
                field = value
            }
            val list = getSelectedItems()
            val isNotEmpty = list.isNotEmpty()

            (0..(menu?.size() ?: 0))
                    .map { menu?.getItem(it) }
                    .filter { it != null && !unhidableMenuItemIds.contains(it.itemId) }
                    .forEach { it?.isVisible = !field }


            selectAllButton?.isVisible = isNotEmpty && list.size != selectableItemsCount
            deleteButton?.isVisible = isNotEmpty
            setSelectedItemsCount.invoke(list.size)
        }

    fun onCreateOptionsMenu(menu: Menu) {
        selectAllButton = menu.add(Menu.NONE, R.id.select_all, Menu.NONE, R.string.select_all)
                .apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    setIcon(R.drawable.select_all)
                    isVisible = false
                }

        deleteButton = menu.add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete_selected)
                .apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    setIcon(R.drawable.delete)
                    isVisible = false
                }

        this.menu = menu
    }

    fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.select_all -> selectAll().let { true }
        R.id.delete -> deleteAction(getSelectedItems()).let { true }
        else -> false
    }

    fun handleBackPressed() = selectOff()

    fun handleItemPressed(item: ItemWrapper): Boolean {
        return if (edit && item.selectable) {
            item.selected.set(!item.selected.get())
            edit = getSelectedItems().isNotEmpty()
            notifyItemChanged(items.indexOf(item))
            true
        } else {
            false
        }
    }

    private fun getSelectedItems(): List<ItemWrapper> = items.filter { it.selectable && it.selected.get() }

    private fun selectAll() {
        items.filter { it.selectable }
                .forEach { it.selected.set(true) }
        edit = true
        notifyDataSetChanged()
    }

    private fun selectOff(): Boolean =
            if (edit) {
                items.filter { it.selectable }
                        .forEach { it.selected.set(false) }
                edit = false
                notifyDataSetChanged()
                true
            } else {
                false
            }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        items.addOnListChangedCallback(onListChangeCallback)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        items.removeOnListChangedCallback(onListChangeCallback)
        this.recyclerView = null
    }

    override fun addDelegate(delegate: BaseAdapterDelegate<*>) {
        delegate.onItemClickListener = BlockerClickDecorator(delegate.onItemClickListener, { view, item -> handleItemPressed(item) })
        delegate.onLongClickListener = BlockerClickDecorator(delegate.onLongClickListener, { view, item -> handleLongClick(item) })
        super.addDelegate(delegate)
    }

    private fun handleLongClick(item: ItemWrapper): Boolean {
        return if (item.selectable) {
            item.selected.set(true)
            edit = true
            true
        } else {
            false
        }
    }

    private fun updateSelectableItems() {
        selectableItemsCount = items.count { it.selectable }
        edit = false
    }

    private val onListChangeCallback: ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>> =
            object : ObservableList.OnListChangedCallback<ObservableList<ItemWrapper>>() {
                override fun onItemRangeRemoved(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    updateSelectableItems()
                }

                override fun onItemRangeMoved(sender: ObservableList<ItemWrapper>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    updateSelectableItems()
                }

                override fun onItemRangeChanged(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    updateSelectableItems()
                }

                override fun onChanged(sender: ObservableList<ItemWrapper>?) {
                    updateSelectableItems()
                }

                override fun onItemRangeInserted(sender: ObservableList<ItemWrapper>?, positionStart: Int, itemCount: Int) {
                    updateSelectableItems()
                }
            }


}