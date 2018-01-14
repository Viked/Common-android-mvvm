package com.viked.commonandroidmvvm.ui.preference

import android.support.v7.preference.PreferenceDialogFragmentCompat

/**
 * Created by yevgeniishein on 1/13/18.
 */
class DialogPreferenceDelegate<T>(val clazz: Class<T>, val preference: (String) -> PreferenceDialogFragmentCompat)