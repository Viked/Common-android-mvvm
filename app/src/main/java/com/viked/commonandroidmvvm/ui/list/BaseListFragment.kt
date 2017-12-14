package com.viked.commonandroidmvvm.ui.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.databinding.LayoutListBinding
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.dialog.ConfirmDialogFragment
import com.viked.commonandroidmvvm.ui.fragment.BaseFragment

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseListFragment<T : BaseListViewModel> : BaseFragment<T, LayoutListBinding>(), ConfirmDialogFragment.Callback {

    override val layoutId: Int = R.layout.layout_list

    lateinit var adapter: AutoClearedValue<SelectableDelegateRecyclerViewAdapter>

    abstract fun addDelegates(adapter: SelectableDelegateRecyclerViewAdapter, viewModel: T)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun initRecyclerView(viewModel: T) {
        val adapter = SelectableDelegateRecyclerViewAdapter({
            ConfirmDialogFragment
                    .newInstance(getString(R.string.confirm_delete, it.size), R.string.confirm_delete)
                    .show(childFragmentManager, ConfirmDialogFragment::class.java.simpleName)
        }, { setTitle(it) }, viewModel.list)
        this.adapter = AutoClearedValue(this, adapter)
        addDelegates(adapter, viewModel)
    }

    //Do not override in other modules
    override fun setViewModelToBinding(binding: LayoutListBinding, viewModel: T) {
        binding.context = viewModel
        initRecyclerView(viewModel)
        applyViewModel(viewModel)
    }

    open fun applyViewModel(viewModel: T) {
        //Workaround for LayoutListBinding
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        adapter.value?.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            adapter.value?.onOptionsItemSelected(item) ?: false || super.onOptionsItemSelected(item)

    private fun setTitle(count: Int) {
        if (count == 0) {
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            viewModel.value?.title?.set(TextWrapper(viewModel.value?.titleId ?: 0))
        } else {
            (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            viewModel.value?.title?.set(TextWrapper(count.toString()))
        }
    }

    override fun handleOnBackPressed() = adapter.value?.handleBackPressed() ?: super.handleOnBackPressed()

    override fun confirm(id: Int) {
        if (id == R.string.confirm_delete) {
            viewModel.value?.removeSelected()
        }
    }
}