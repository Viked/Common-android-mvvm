package com.viked.commonandroidmvvm.preference

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.widget.TimePicker
import java.util.*

/**
 * Created by yevgeniishein on 8/5/17.
 */
class TimePreferenceDialogFragment : PreferenceDialogFragmentCompat(), TimePickerDialog.OnTimeSetListener {

    companion object {
        fun newInstance(key: String): TimePreferenceDialogFragment {
            val fragment = TimePreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(Calendar.DAY_OF_MONTH.toString(), (dialog as TimePickerDialog).onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    private fun getTimePreference(): TimePreference {
        return preference as TimePreference
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (savedInstanceState == null) {
            val c = Calendar.getInstance()
            c.timeInMillis = getTimePreference().time
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            return TimePickerDialog(activity, this, hour, minute, false)
        } else {
            TimePickerDialog(activity, this, 0, 0, true).apply {
                onRestoreInstanceState(savedInstanceState.getBundle(Calendar.DAY_OF_MONTH.toString()))
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        getTimePreference().time = c.timeInMillis
    }

    override fun onDialogClosed(positiveResult: Boolean) {

    }

}