package com.viked.commonandroidmvvm.ui.preference

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.preference.*
import android.support.v7.preference.internal.AbstractMultiSelectListPreference
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.preference.TimePreference
import com.viked.commonandroidmvvm.preference.TimePreferenceDialogFragment
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.binding.addOnPropertyChangeListener
import com.viked.commonandroidmvvm.ui.common.AutoClearedValue
import com.viked.commonandroidmvvm.ui.common.Cancelable
import com.viked.commonandroidmvvm.ui.common.delegate.error.DialogErrorDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.error.ErrorDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.progress.DialogProgressDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.progress.ProgressDelegate
import javax.inject.Inject

/**
 * Created by yevgeniishein on 10/9/17.
 */
abstract class BasePreferenceFragment<T : BasePreferenceViewModel> : PreferenceFragmentCompat(), Injectable, Cancelable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var progressDelegate: AutoClearedValue<ProgressDelegate>

    lateinit var errorDelegate: AutoClearedValue<ErrorDelegate>

    lateinit var viewModel: AutoClearedValue<T>

    abstract val viewModelClass: Class<T>

    abstract val preferencesResId: Int

    abstract fun initPreferences(viewModel: T, activity: BaseActivity)

    private val dialogDelegates = mutableListOf<DialogPreferenceDelegate<*>>()

    operator fun <R : Preference> get(id: Int): R? = findPreference(getString(id)) as R?
    operator fun set(id: Int, click: () -> Unit) = findPreference(getString(id)).setOnPreferenceClickListener { click.invoke(); true }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
        this.viewModel = AutoClearedValue(this, viewModel)
        val activity = activity()
        if (activity != null) {
            arguments?.run { initArguments(viewModel, this) }
            viewModel.loadData()
            initToolbar(activity, viewModel)
            progressDelegate = AutoClearedValue(this, initProgressDelegate(viewModel, activity))
            errorDelegate = AutoClearedValue(this, initErrorDelegate(viewModel, activity))
            initDialogDelegates(dialogDelegates)
            initPreferences(viewModel, activity)

        } else {
            RuntimeException("BaseFragment has empty params\nviewModel: ${this.viewModel.value}").log()
        }
    }

    override fun onDestroyView() {
        viewModel.value?.onCleared()
        super.onDestroyView()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(preferencesResId)
    }

    override fun handleOnBackPressed() = viewModel.value?.progress?.get() ?: false

    open fun initToolbar(activity: BaseActivity, viewModel: T) {
        viewModel.title.addOnPropertyChangeListener { setTitle(it.get()) }

        //Set title if need
    }

    open fun initProgressDelegate(viewModel: T, activity: BaseActivity) = DialogProgressDelegate(activity)

    open fun initErrorDelegate(viewModel: T, activity: BaseActivity) = DialogErrorDelegate(activity)

    open fun initArguments(viewModel: T, arguments: Bundle) {
        //Set initial data to view model
    }

    open fun initDialogDelegates(dialogDelegates: MutableList<DialogPreferenceDelegate<*>>) {
        dialogDelegates.add(DialogPreferenceDelegate(EditTextPreference::class.java, { EditTextPreferenceDialogFragmentCompat.newInstance(it) }))
        dialogDelegates.add(DialogPreferenceDelegate(ListPreference::class.java, { ListPreferenceDialogFragmentCompat.newInstance(it) }))
        dialogDelegates.add(DialogPreferenceDelegate(AbstractMultiSelectListPreference::class.java, { MultiSelectListPreferenceDialogFragmentCompat.newInstance(it) }))
        dialogDelegates.add(DialogPreferenceDelegate(TimePreference::class.java, { TimePreferenceDialogFragment.newInstance(it) }))
    }

    fun activity() = activity as BaseActivity?

    override fun onPause() {
        progressDelegate.value?.unsubscribe()
        errorDelegate.value?.unsubscribe()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val viewModel = viewModel.value
        if (viewModel != null) {
            progressDelegate.value?.subscribe(viewModel.progress)
            errorDelegate.value?.subscribe(viewModel.error)
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

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        // check if dialog is already showing
        if (preference == null || fragmentManager?.findFragmentByTag(PREFERENCE_DIALOG_FRAGMENT_TAG) != null) {
            return
        }

        dialogDelegates.find { preference::class.java == it.clazz }?.preference?.invoke(preference.key)?.let {
            it.setTargetFragment(this, 0)
            it.show(fragmentManager, PREFERENCE_DIALOG_FRAGMENT_TAG)
        } ?: error("Tried to display dialog for unknown")
    }


}