package com.viked.commonandroidmvvm.preference

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData

class PreferenceLiveData(private val preferenceHelper: PreferenceHelper, private val keys: List<Int>) : MediatorLiveData<Map<Int, Any>>(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val keyMap = keys.map { Pair(preferenceHelper.getKeyForId(it), it) }.toMap()

    override fun onInactive() {
        preferenceHelper.preferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onInactive()
    }

    override fun onActive() {
        super.onActive()
        setInitialValue()
        preferenceHelper.preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val id = keyMap[key]
        val value = value?.toMutableMap()

        if (id == null || value == null) {
            setInitialValue()
            return
        }

        value[id] = preferenceHelper.getValue(id)
        postValue(value.toMap())
    }

    private fun setInitialValue() {
        val values = keys.map {
            Pair(it, preferenceHelper.getValue(it))
        }.toMap()
        postValue(values)
    }
}