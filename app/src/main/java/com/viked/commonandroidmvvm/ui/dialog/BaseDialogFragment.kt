package com.viked.commonandroidmvvm.ui.dialog

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.DialogFragment
import android.view.*
import androidx.databinding.library.baseAdapters.BR
import com.crashlytics.android.answers.CustomEvent
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.Cancelable
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


/**
 * Created by yevgeniishein on 3/11/18.
 */
abstract class BaseDialogFragment<T : BaseViewModel, B : ViewDataBinding> : DialogFragment(), Injectable, Cancelable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytic: Analytic

    lateinit var viewModel: AutoClearedValue<T>

    lateinit var binding: AutoClearedValue<B>

    lateinit var adapters: AutoClearedValue<MutableList<AdapterDelegate>>

    abstract val layoutId: Int

    abstract val viewModelClass: Class<T>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
        val binding = binding.value
        this.viewModel = AutoClearedValue(this, viewModel)
        val activity = activity()
        if (binding != null && activity != null) {
            binding.setLifecycleOwner(this)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
                .inflate<B>(inflater, layoutId, container, false)
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
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
        analytic.log(CustomEvent("Dialog viewed").putCustomAttribute("name", this::class.java.simpleName))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                if (event.action != KeyEvent.ACTION_DOWN)
                    handleOnBackPressed()
                else false
            } else false
        }
    }

    open fun loadData() {
        viewModel.value?.loadData()
    }

    override fun handleOnBackPressed() = false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        Handler().post { EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this) }
    }
}