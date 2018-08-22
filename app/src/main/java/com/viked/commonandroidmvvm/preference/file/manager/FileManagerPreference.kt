package com.viked.commonandroidmvvm.preference.file.manager

import android.content.Context
import android.support.v7.preference.DialogPreference
import com.viked.commonandroidmvvm.R
import java.io.File

/**
 * Created by 1 on 19.03.2016.(
 */
class FileManagerPreference(context: Context) : DialogPreference(context) {

    private val SUMMARY_START = "/.."

    var path: String = "/"
        set(value) {
            if (field.equals(value).not()) {
                val file = File(value)
                if (file.exists() || file.mkdirs()) {
                    field = value
                    this.persistString(value)
                    this.summary = SUMMARY_START + value.substring(value.lastIndexOf("/"))
                    this.notifyChanged()
                }
            }
        }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        this.path = if (restorePersistedValue) this.getPersistedString(this.path) else {
            if (defaultValue != null) {
                defaultValue as String
            } else "/"
        }
    }

    init {
        this.positiveButtonText = context.getText(android.R.string.ok)
        this.negativeButtonText = context.getText(android.R.string.cancel)
    }


}