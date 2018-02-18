package com.viked.commonandroidmvvm.ui.list

import android.databinding.ViewDataBinding
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.binding.addOnPropertyChangeListener

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseSelectableListFragment<T : BaseSelectableListViewModel<*>, B : ViewDataBinding> : BaseListFragment<T, B>() {

    override fun newAdapterInstance(viewModel: T): DelegateRecyclerViewAdapter {
        return SelectableDelegateRecyclerViewAdapter(viewModel.selectMode, viewModel.list)
    }

    override fun handleOnBackPressed() = viewModel.value?.handleBackPressed() ?: false

    open fun updateIcons(selectMode: Boolean, canSelectAll: Boolean) {
        activity()?.supportActionBar?.setHomeAsUpIndicator(if (selectMode) R.drawable.ic_close else R.drawable.abc_ic_ab_back_material)
    }

    override fun initView(binding: B, viewModel: T) {
        super.initView(binding, viewModel)
        viewModel.selectMode.addOnPropertyChangeListener {
            updateIcons(
                    this.viewModel.value?.selectMode?.get() ?: false,
                    this.viewModel.value?.canSelectAll() ?: false)
        }
    }
}