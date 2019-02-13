package com.viked.commonandroidmvvm.preference

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

    fun getValue(id: Int): Any = getValue(id, getKeyForId(id))

    fun <T> getValue(id: Int, clazz: Class<T>) = getValue(id, getKeyForId(id), clazz)

    fun <T> getValue(key: String, clazz: Class<T>) = getValue(getIdForKey(key), key, clazz)

    private fun getValue(id: Int, key: String) = preferences.all[key]
            ?: initialValues.find { it.key == id }?.initialValue ?: error("No initial value")

    private fun <T> getValue(id: Int, key: String, clazz: Class<T>): T {
        val value = getValue(id, key)
        return when {
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

    fun getIdForKey(key: String) = keyMap[key] ?: 0

    fun getKeyForId(id: Int) = context.getString(id)

    fun getLivePreferences(vararg id: Int): LiveData<Map<Int, Any>> {
        return PreferenceLiveData(this, id.toList())
    }

}

inline operator fun <reified T : Any> PreferenceHelper.get(id: Int): T {
    return getValue(id, T::class.java)
}

inline operator fun <reified T : Any> PreferenceHelper.get(key: String): T {
    return getValue(key, T::class.java)
}

inline fun <reified T : Any> PreferenceHelper.getLivePreference(id: Int): LiveData<T> {
    return Transformations.map(getLivePreferences(id)) { it[id] as T }
}
