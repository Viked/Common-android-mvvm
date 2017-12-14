package com.viked.commonandroidmvvm.ui.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.BaseViewModel

/**
 * Created by yevgeniishein on 10/9/17.
 */
abstract class BaseFragment<T : BaseViewModel, B : ViewDataBinding> : Fragment() {

    lateinit var viewModel: AutoClearedValue<T>

    lateinit var binding: AutoClearedValue<B>

    abstract val layoutId: Int

    abstract fun getViewModelInstance(): T

    abstract fun setViewModelToBinding(binding: B, viewModel: T)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = AutoClearedValue(this, getViewModelInstance())
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
                .inflate<B>(inflater, layoutId, container, false)
        binding = AutoClearedValue(this, dataBinding)
        onCreateAdditionalViews(inflater, container)
        return dataBinding.root
    }

    override fun onDestroyView() {
        viewModel.value?.onCleared()
        super.onDestroyView()
    }

    open fun handleOnBackPressed() = false

    open fun onCreateAdditionalViews(inflater: LayoutInflater?, container: ViewGroup?) {
        //Init toolbar view if need
    }

    open fun initToolbar(activity: BaseActivity, binding: B, viewModel: T) {
        //Set title if need
    }

    open fun initView() {
        if (binding.value != null && viewModel.value != null) {
            setViewModelToBinding(binding.value!!, viewModel.value!!)
            initToolbar(activity as BaseActivity, binding.value!!, viewModel.value!!)
        } else {
            Crashlytics.logException(RuntimeException("BaseFragment has empty params\nbinding: ${binding.value}\nviewModel: ${viewModel.value}"))
        }
    }


}