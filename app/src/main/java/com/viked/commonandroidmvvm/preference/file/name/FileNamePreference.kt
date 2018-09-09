package com.viked.commonandroidmvvm.preference.file.name

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import com.viked.commonandroidmvvm.R

/**
 * Created by 1 on 25.03.2016.
 */
class FileNamePreference(context: Context, attrs: AttributeSet? = null) : DialogPreference(context, attrs) {

    var fileName: String = "/"
        set(value) {
            if (field.equals(value).not()) {
                field = value
                this.persistString(value)
                this.summary = value
                this.notifyChanged()

            }
        }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        this.fileName = if (restorePersistedValue) this.getPersistedString(this.fileName) else {
            if (defaultValue != null) {
                defaultValue as String
            } else "/"
        }
    }

    init {
        this.dialogLayoutResource = R.layout.dialog_file_name
        this.dialogMessage = context.getText(R.string.file_name_dialog_massage)
        this.positiveButtonText = context.getText(android.R.string.ok)
        this.negativeButtonText = context.getText(android.R.string.cancel)
    }
}