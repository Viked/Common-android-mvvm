package com.viked.commonandroidmvvm.ui.fragment.list

import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.dialog.ConfirmDialogFragment

/**
 * Created by yevgeniishein on 1/13/18.
 */
//abstract class BaseRemovableListFragment<T : BaseRemovableListViewModel<*>, B : ViewDataBinding> : BaseFragment<T, B>(), ConfirmDialogFragment.Callback {

//    var selectAllButton: AutoClearedValue<MenuItem>? = null
//    var deleteButton: AutoClearedValue<MenuItem>? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        selectAllButton = AutoClearedValue(this, menu.add(Menu.NONE, R.id.select_all, Menu.NONE, R.string.select_all)
//                .apply {
//                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//                    setIcon(R.drawable.select_all)
//                    isVisible = false
//                })
//
//        deleteButton = AutoClearedValue(this, menu.add(Menu.NONE, R.id.delete, Menu.NONE, R.string.delete_selected)
//                .apply {
//                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//                    setIcon(R.drawable.delete)
//                    isVisible = false
//                })
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) =
//            when (item.itemId) {
//                R.id.select_all -> {
//                    viewModel.value?.selectAll()
//                    true
//                }
//                R.id.delete -> {
//                    ConfirmDialogFragment.newInstance(R.string.confirm_delete).show(childFragmentManager, ConfirmDialogFragment.TAG)
//                    true
//                }
//                else -> super.onOptionsItemSelected(item)
//            }
//
//    override fun updateIcons(selectMode: Boolean, canSelectAll: Boolean) {
//        super.updateIcons(selectMode, canSelectAll)
//        deleteButton?.value?.isVisible = selectMode
//        selectAllButton?.value?.isVisible = canSelectAll
//    }
//
//    override fun confirm(id: Int) {
//        if (id == R.string.confirm_delete) {
//            viewModel.value?.removeItems()
//        }
//    }
//}