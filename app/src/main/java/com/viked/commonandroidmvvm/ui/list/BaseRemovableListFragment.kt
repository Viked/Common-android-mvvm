package com.viked.commonandroidmvvm.ui.list

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue

/**
 * Created by yevgeniishein on 1/13/18.
 */
abstract class BaseRemovableListFragment<T : BaseRemovableListViewModel, B : ViewDataBinding> : BaseSelectableListFragment<T, B>() {

    lateinit var selectAllButton: AutoClearedValue<MenuItem>
    lateinit var deleteButton: AutoClearedValue<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        selectAllButton = AutoClearedValue(this, menu.add(Menu.NONE, R.id.select_all, Menu.NONE, R.string.select_all)
                .apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    setIcon(R.drawable.select_all)
                    isVisible = false
                })

        deleteButton = AutoClearedValue(this, menu.add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete_selected)
                .apply {
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    setIcon(R.drawable.delete)
                    isVisible = false
                })
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.select_all -> {
                    viewModel.value?.selectAll()
                    true
                }
                R.id.delete -> {
                    viewModel.value?.removeItems()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun updateIcons(selectMode: Boolean, canSelectAll: Boolean) {
        super.updateIcons(selectMode, canSelectAll)
        deleteButton.value?.isVisible = selectMode
        selectAllButton.value?.isVisible = canSelectAll
    }

}