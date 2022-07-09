package com.viked.commonandroidmvvm.ui.preference

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.preference.PreferenceItem
import com.viked.commonandroidmvvm.preference.time.TimePreference
import com.viked.commonandroidmvvm.preference.time.TimePreferenceDialogFragment
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.Cancelable
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel
import javax.inject.Inject

/**
 * Created by yevgeniishein on 10/9/17.
 */
abstract class BasePreferenceFragment<T : BaseViewModel> : PreferenceFragmentCompat(), Injectable,
    Cancelable, PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytic: Analytic

    @Inject
    lateinit var initialValues: Set<@JvmSuppressWildcards PreferenceItem>

    val viewModel: AutoClearedValue<T> by lazy {
        AutoClearedValue(this, ViewModelProvider(this, viewModelFactory).get())
    }

    abstract fun ViewModelProvider.get(): T

    abstract val screenName: String

    abstract fun initPreferences(viewModel: T, activity: BaseActivity)

    operator fun <R : Preference> get(id: Int): R? = findPreference(getString(id)) as R?
    operator fun set(id: Int, click: () -> Unit) =
        findPreference<Preference>(getString(id))?.setOnPreferenceClickListener { click.invoke(); true }

    fun <T> default(id: Int): T = initialValues.find { it.key == id }?.initialValue as? T
        ?: error("Not valid default preference id: $id")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = viewModel.value
        val activity = activity()
        if (viewModel != null && activity != null) {
            arguments?.run { initArguments(viewModel, this) }
            viewModel.loadData()
            initToolbar(activity, viewModel)
            initPreferences(viewModel, activity)
            logStartEvent()
            viewModel.title.observe(viewLifecycleOwner) { setTitle(it) }
        } else {
            RuntimeException("BasePreferenceFragment has empty params\nviewModel: ${this.viewModel.value}").log()
        }
    }

    override fun handleOnBackPressed() = false

    open fun initToolbar(activity: BaseActivity, viewModel: T) {
        //Set title if need
    }

    open fun initArguments(viewModel: T, arguments: Bundle) {
        //Set initial data to view model
    }

    fun activity() = activity as BaseActivity?

    fun refreshSettings() {
        val viewModel = viewModel.value
        val activity = activity()
        if (viewModel != null && activity != null) {
            preferenceScreen = null
            onCreatePreferences(null, null)
            initPreferences(viewModel, activity)
        }
    }

    protected fun setTitle(title: TextWrapper?) {
        val activity = activity()
        if (activity != null && title != null) {
            val newTitle = title[activity]
            if (newTitle.isNotEmpty()) {
                activity.title = newTitle
            }
        }
    }

    override fun onPreferenceDisplayDialog(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val fragment: PreferenceDialogFragmentCompat? = when (pref) {
            is EditTextPreference -> SingleLineEditTextPreferenceDialogFragmentCompat.newInstance(
                pref.key
            )
            is TimePreference -> TimePreferenceDialogFragment.newInstance(pref.key)
            else -> null
        }

        return fragment?.also {
            it.setTargetFragment(this, 0)
            it.show(parentFragmentManager, PREFERENCE_DIALOG_FRAGMENT_TAG)
        } != null
    }

    private fun logStartEvent() {
        analytic.setScreen(screenName, this::class.java)
    }

}