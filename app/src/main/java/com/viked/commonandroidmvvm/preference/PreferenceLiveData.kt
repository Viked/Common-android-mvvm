package com.viked.commonandroidmvvm.preference

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData

class PreferenceLiveData(private val preferenceHelper: PreferenceHelper, private val keys: Set<Int>) : MediatorLiveData<Map<Int, Any>>(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val keyMap = keys.associateBy { preferenceHelper.getKeyForId(it) }

    override fun onInactive() {
        preferenceHelper.preferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onInactive()
    }

    override fun onActive() {
        super.onActive()
        setValue()
        preferenceHelper.preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        keyMap[key] ?: return
        setValue()
    }

    private fun setValue() {
        val values = keys.map {
            Pair(it, preferenceHelper.getValue(it))
        }.toMap()
        postValue(values)
    }
}
