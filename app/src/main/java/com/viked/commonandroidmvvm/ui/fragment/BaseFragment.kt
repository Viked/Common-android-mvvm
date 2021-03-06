package com.viked.commonandroidmvvm.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.answers.CustomEvent
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.Cancelable
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/**
 * Created by yevgeniishein on 10/9/17.
 */
abstract class BaseFragment<T : BaseViewModel, B : ViewDataBinding> : Fragment(), Injectable, Cancelable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytic: Analytic

    val viewModel:  AutoClearedValue<T> by lazy {
        AutoClearedValue(this, ViewModelProvider(this, viewModelFactory).get())
    }

    lateinit var binding: AutoClearedValue<B>

    abstract val layoutId: Int

    abstract fun ViewModelProvider.get(): T

    open val viewModelBindingId: Int = BR.viewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = viewModel.value
        val binding = binding.value
        val activity = activity()
        if (viewModel!= null && binding != null && activity != null) {
            binding.lifecycleOwner = this
            arguments?.run { initArguments(viewModel, this) }
            viewModel.onInit()
            loadData()
            binding.setVariable(viewModelBindingId, viewModel)
            initView(binding, viewModel)
            logStartEvent()
            viewModel.title.observe(this, Observer { setTitle(it) })
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

    override fun onResume() {
        super.onResume()
        viewModel.value?.title?.value?.let { setTitle(it) }
    }

    override fun handleOnBackPressed() = false

    open fun initToolbar(activity: BaseActivity, binding: B, viewModel: T) {
        //Set title if need
    }

    open fun initView(binding: B, viewModel: T) {
        //Set clicks, other view features
    }

    open fun loadData() {
        viewModel.value?.loadData()
    }

    open fun initArguments(viewModel: T, arguments: Bundle) {
        //Set initial data to view model
    }

    fun activity() = activity as BaseActivity?

    protected fun setTitle(title: TextWrapper?) {
        val activity = activity()
        if (activity != null && title != null) {
            val newTitle = title[activity]
            if (newTitle.isNotEmpty()) {
                activity.title = newTitle
            }
        }
    }

    private fun logStartEvent() {
        analytic.log(CustomEvent("Screen viewed").putCustomAttribute("name", this::class.java.simpleName))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        Handler().post { EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this) }
    }

}