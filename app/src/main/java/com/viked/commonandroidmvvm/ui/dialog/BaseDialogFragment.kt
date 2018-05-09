package com.viked.commonandroidmvvm.ui.dialog

import android.app.Dialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.crashlytics.android.answers.CustomEvent
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel
import javax.inject.Inject


/**
 * Created by yevgeniishein on 3/11/18.
 */
abstract class BaseDialogFragment<T : BaseViewModel, B : ViewDataBinding> : DialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytic: Analytic

    lateinit var viewModel: AutoClearedValue<T>

    lateinit var binding: AutoClearedValue<B>

    lateinit var adapters: AutoClearedValue<MutableList<AdapterDelegate>>

    abstract val layoutId: Int

    abstract val viewModelClass: Class<T>

    abstract fun setViewModelToBinding(binding: B, viewModel: T)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
        val binding = binding.value
        this.viewModel = AutoClearedValue(this, viewModel)
        val activity = activity()
        if (binding != null && activity != null) {
            arguments?.run { initArguments(viewModel, this) }
            adapters = AutoClearedValue(this, mutableListOf())
            viewModel.onInit()
            viewModel.loadData()
            setViewModelToBinding(binding, viewModel)
            initView(binding, viewModel)
            adapters.value?.forEach { it.subscribe() }
            logStartEvent()
        } else {
            RuntimeException("BaseFragment has empty params\nbinding: ${this.binding.value}\nviewModel: ${this.viewModel.value}").log()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
                .inflate<B>(inflater, layoutId, container, false)
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    override fun onDestroyView() {
        adapters.value?.forEach { it.unsubscribe() }
        viewModel.value?.onCleared()
        super.onDestroyView()
    }

    open fun initView(binding: B, viewModel: T) {
        //Set clicks, other view features
    }

    open fun initArguments(viewModel: T, arguments: Bundle) {
        //Set initial data to view model
    }

    fun activity() = activity as BaseActivity?

    protected fun addAdapterDelegate(adapterDelegate: AdapterDelegate) {
        adapters.value?.add(adapterDelegate)
    }

    private fun logStartEvent() {
        analytic.log(CustomEvent("Dialog viewed").putCustomAttribute("name", this::class.java.simpleName))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

}