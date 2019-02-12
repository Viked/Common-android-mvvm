package com.viked.commonandroidmvvm.ui.preference

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.*
import androidx.preference.MultiSelectListPreference
import com.crashlytics.android.answers.CustomEvent
import com.viked.commonandroidmvvm.di.Injectable
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.preference.ARG_KEY
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
abstract class BasePreferenceFragment<T : BaseViewModel> : PreferenceFragmentCompat(), Injectable, Cancelable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytic: Analytic

    @Inject
    lateinit var initialValues: Set<@JvmSuppressWildcards PreferenceItem>

    lateinit var viewModel: AutoClearedValue<T>

    abstract val viewModelClass: Class<T>

    abstract val preferencesResId: Int

    abstract fun initPreferences(viewModel: T, activity: BaseActivity)

    private val dialogDelegates = mutableListOf<DialogPreferenceDelegate<*>>()

    operator fun <R : Preference> get(id: Int): R? = findPreference(getString(id)) as R?
    operator fun set(id: Int, click: () -> Unit) = findPreference(getString(id)).setOnPreferenceClickListener { click.invoke(); true }

    fun <T> default(id: Int): T = initialValues.find { it.key == id }?.initialValue as? T
            ?: error("Not valid default preference id: $id")

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)
        this.viewModel = AutoClearedValue(this, viewModel)
        val activity = activity()
        if (activity != null) {
            arguments?.run { initArguments(viewModel, this) }
            viewModel.loadData()
            initToolbar(activity, viewModel)
            initDialogDelegates(dialogDelegates)
            initPreferences(viewModel, activity)
            logStartEvent()
            viewModel.title.observe(this, Observer { setTitle(it) })
        } else {
            RuntimeException("BasePreferenceFragment has empty params\nviewModel: ${this.viewModel.value}").log()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(preferencesResId)
    }

    override fun handleOnBackPressed() = false

    open fun initToolbar(activity: BaseActivity, viewModel: T) {
        //Set title if need
    }

    open fun initArguments(viewModel: T, arguments: Bundle) {
        //Set initial data to view model
    }

    open fun initDialogDelegates(dialogDelegates: MutableList<DialogPreferenceDelegate<*>>) {
        dialogDelegates.add(DialogPreferenceDelegate(EditTextPreference::class.java, EditTextPreferenceDialogFragmentCompat::class.java.name))
        dialogDelegates.add(DialogPreferenceDelegate(ListPreference::class.java, ListPreferenceDialogFragmentCompat::class.java.name))
        dialogDelegates.add(DialogPreferenceDelegate(MultiSelectListPreference::class.java, MultiSelectListPreferenceDialogFragmentCompat::class.java.name))
        dialogDelegates.add(DialogPreferenceDelegate(TimePreference::class.java, TimePreferenceDialogFragment::class.java.name))
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

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        // check if dialog is already showing
        if (preference == null || fragmentManager?.findFragmentByTag(PREFERENCE_DIALOG_FRAGMENT_TAG) != null) {
            return
        }

        val dialogClassName = dialogDelegates.find { preference::class.java == it.clazz }?.preferenceDialogClassName
                ?: error("Tried to display dialog for unknown")

        (androidx.fragment.app.Fragment.instantiate(context!!, dialogClassName, Bundle(1).apply { putString(ARG_KEY, preference.key) }) as? androidx.fragment.app.DialogFragment)?.also {
            it.setTargetFragment(this, 0)
        }
                ?.show(fragmentManager!!, PREFERENCE_DIALOG_FRAGMENT_TAG)
    }

    private fun logStartEvent() {
        analytic.log(CustomEvent("Screen viewed").putCustomAttribute("name", this::class.java.simpleName))
    }

}