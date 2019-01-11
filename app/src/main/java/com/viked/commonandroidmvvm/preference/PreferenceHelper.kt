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
class PreferenceHelper @Inject constructor(private val context: Application,
                                           private val initialValues: Set<@JvmSuppressWildcards PreferenceItem>) {

    private val gson = Gson()

    private val keyMap: Map<String, Int> by lazy { initialValues.map { Pair(context.getString(it.key), it.key) }.toMap() }

    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun reset() {
        initialValues.forEach { set(it.key, it.initialValue) }
    }

    fun reset(id: Int) {
        initialValues.find { it.key == id }?.apply { set(key, initialValue) }
    }

    fun <T> getValue(id: Int, clazz: Class<T>) = getValue(id, context.getString(id), clazz)

    fun <T> getValue(key: String, clazz: Class<T>) = getValue(getIdForKey(key), key, clazz)

    private fun <T> getValue(id: Int, key: String, clazz: Class<T>): T {
        val value = preferences.all[key]
        return if (value == null) {
            initialValues.find { it.key == id }?.initialValue as? T
                    ?: error("No initial value")
        } else when {
            value::class.java == clazz || clazz == Set::class.java -> value as T
            value is String -> gson.fromJson(value, clazz)
            else -> error("Unsupported value type")
        }
    }

    operator fun set(id: Int, value: Any) = setPreferenceValue(context.getString(id), value)

    operator fun set(key: String, value: Any) = setPreferenceValue(key, value)

    private fun setPreferenceValue(key: String, value: Any) {
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

    private fun getIdForKey(key: String) = keyMap[key] ?: 0

}

inline operator fun <reified T : Any> PreferenceHelper.get(id: Int): T {
    return getValue(id, T::class.java)
}

inline operator fun <reified T : Any> PreferenceHelper.get(key: String): T {
    return getValue(key, T::class.java)
}