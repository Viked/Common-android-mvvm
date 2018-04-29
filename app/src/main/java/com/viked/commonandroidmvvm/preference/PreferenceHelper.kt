package com.viked.commonandroidmvvm.preference

import android.app.Application
import android.preference.PreferenceManager
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 9/20/17.
 */
@Singleton
class PreferenceHelper @Inject constructor(val context: Application) {

    val gson = Gson()

    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    inline operator fun <reified T : Any> get(id: Int): T? {
        val clazz = T::class.java
        val value = preferences.all[context.getString(id)] ?: return null

        return when {
            value::class.java == clazz || clazz == Set::class.java -> value as T
            value is String -> gson.fromJson(value, clazz)
            else -> error("Unsupported value type")
        }
    }

    operator fun set(id: Int, value: Any) = setPreferenceValue(id, value)

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
                else -> putString(key, gson.toJson(value))
            }
        }.apply()
    }


}