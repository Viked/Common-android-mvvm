package com.viked.commonandroidmvvm.preference

import android.app.Application
import android.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 9/20/17.
 */
@Singleton
class PreferenceHelper @Inject constructor(val context: Application) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    operator fun <T> get(id: Int): T? = getPreferenceValue(id) as T?
    operator fun set(id: Int, value: Any) = setPreferenceValue(id, value)

    private fun getPreferenceValue(id: Int): Any? = preferences.all[context.getString(id)]

    private fun setPreferenceValue(id: Int, value: Any) {
        val key = context.getString(id)
        preferences.edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Set<*> -> putStringSet(key, value as Set<String>)
                else -> error("Unsupported value type")
            }
        }.apply()
    }


}