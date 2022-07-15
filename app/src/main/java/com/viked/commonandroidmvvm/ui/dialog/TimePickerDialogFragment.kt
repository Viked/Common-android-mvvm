package com.viked.commonandroidmvvm.ui.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult


/**
 * Created by Marshall Banana on 02.07.2017.
 */
class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var dialogId: Int = -1

    companion object {
        const val ID_KEY = "id_key"
        const val HOUR_KEY = "hour_key"
        const val MINUTE_KEY = "hour_key"
        fun newInstance(id: Int) =
            TimePickerDialogFragment().apply { arguments = Bundle().apply { putInt(ID_KEY, id) } }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogId = savedInstanceState?.getInt(ID_KEY, -1) ?: (arguments?.getInt(ID_KEY, -1)
            ?: -1)
        return TimePickerDialog(activity, this, 0, 0, true).apply {
            if (savedInstanceState != null) {
                onRestoreInstanceState(savedInstanceState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putAll(dialog?.onSaveInstanceState())
        outState.putInt(ID_KEY, dialogId)
        super.onSaveInstanceState(outState)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        setFragmentResult(getString(dialogId), Bundle().apply {
            putInt(HOUR_KEY, hourOfDay)
            putInt(MINUTE_KEY, minute)
        })
    }
}