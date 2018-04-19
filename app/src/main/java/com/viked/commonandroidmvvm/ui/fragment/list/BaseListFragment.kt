package com.viked.commonandroidmvvm.ui.fragment.list

import android.databinding.ViewDataBinding
import com.viked.commonandroidmvvm.ui.adapters.list.AdapterBehavior
import com.viked.commonandroidmvvm.ui.adapters.list.DelegateRecyclerViewAdapter
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.fragment.BaseFragment

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseListFragment<T : BaseListViewModel<*>, B : ViewDataBinding> : BaseFragment<T, B>() {

    lateinit var adapter: AutoClearedValue<DelegateRecyclerViewAdapter>

    private lateinit var behavior: AutoClearedValue<AdapterBehavior>

    abstract fun addDelegates(adapter: DelegateRecyclerViewAdapter, viewModel: T)

    abstract fun setAdapter(adapter: DelegateRecyclerViewAdapter, binding: B)

    open fun newAdapterInstance(viewModel: T) = DelegateRecyclerViewAdapter(viewModel.list)

    private fun initRecyclerView(binding: B, viewModel: T) {
        val adapter = newAdapterInstance(viewModel)
        val behavior = AdapterBehavior(adapter, viewModel.list)
        this.behavior = AutoClearedValue(this, behavior)
        addAdapterDelegate(behavior)
        this.adapter = AutoClearedValue(this, adapter)
        addDelegates(adapter, viewModel)
        setAdapter(adapter, binding)
    }

    override fun initView(binding: B, viewModel: T) {
        super.initView(binding, viewModel)
        initRecyclerView(binding, viewModel)
    }

    override fun onResume() {
        super.onResume()
        viewModel.value?.onRefresh()
    }
}