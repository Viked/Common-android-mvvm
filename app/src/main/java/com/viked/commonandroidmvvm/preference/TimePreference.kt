package com.viked.commonandroidmvvm.preference

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.utils.formatTime
import java.util.*


/**
 * Created by yevgeniishein on 8/3/17.
 */
class TimePreference(ctxt: Context, attrs: AttributeSet) : DialogPreference(ctxt, attrs) {

    init {
        dialogIcon = null
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
        dialogLayoutResource = R.layout.preference_dialog_time_picker
    }

    var time: Long
        set(value) {
            if (callChangeListener(value)) {
                persistLong(value)
                notifyChanged()
            }
        }
        get() = getPersistedLong(0)

    override fun getSummary(): CharSequence = Calendar.getInstance().also { it.timeInMillis = time }.time.formatTime(context)

}