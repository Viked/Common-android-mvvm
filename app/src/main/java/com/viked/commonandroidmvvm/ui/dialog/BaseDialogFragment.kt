package com.viked.commonandroidmvvm.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.Cancelable
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel
import javax.inject.Inject


/**
 * Created by yevgeniishein on 3/11/18.
 */
abstract class BaseDialogFragment<T : BaseViewModel, B : ViewDataBinding> : DialogFragment(),
    Injectable, Cancelable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytic: Analytic

    val viewModel: AutoClearedValue<T> by lazy {
        AutoClearedValue(this, ViewModelProvider(this, viewModelFactory).get())
    }

    lateinit var binding: AutoClearedValue<B>

    lateinit var adapters: AutoClearedValue<MutableList<AdapterDelegate>>

    abstract val layoutId: Int

    abstract fun ViewModelProvider.get(): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil
            .inflate<B>(inflater, layoutId, container, false)
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = viewModel.value
        val binding = binding.value
        val activity = activity()
        if (viewModel != null && binding != null && activity != null) {
            binding.lifecycleOwner = this
            arguments?.run { initArguments(viewModel, this) }
            adapters = AutoClearedValue(this, mutableListOf())
            viewModel.onInit()
            loadData()
            binding.setVariable(BR.viewModel, viewModel)
            initView(binding, viewModel)
            adapters.value?.forEach { it.subscribe() }
            logStartEvent()
        } else {
            RuntimeException("BaseFragment has empty params\nbinding: ${this.binding.value}\nviewModel: ${this.viewModel.value}").log()
        }
    }

    override fun onDestroyView() {
        adapters.value?.forEach { it.unsubscribe() }
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
        analytic.log(
            "dialog_viewed",
            Bundle().apply { putString("name", this::class.java.simpleName) })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        setWindowParams(dialog?.window ?: return)
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.action != KeyEvent.ACTION_DOWN)
                    handleOnBackPressed()
                else false
            } else false
        }
    }

    open fun loadData() {
        viewModel.value?.loadData()
    }

    open fun setWindowParams(window: Window) {
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun handleOnBackPressed() = false
}