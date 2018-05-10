package com.viked.commonandroidmvvm.ui.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TimePicker


/**
 * Created by Marshall Banana on 02.07.2017.
 */
class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var dialogId: Int = -1

    companion object {
        const val ID_KEY = "id_key"
        fun newInstance(id: Int) = TimePickerDialogFragment().apply { arguments = Bundle().apply { putInt(ID_KEY, id) } }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogId = savedInstanceState?.getInt(ID_KEY, -1) ?: (arguments?.getInt(ID_KEY, -1)
                ?: -1) ?: -1
        return TimePickerDialog(activity, this, 0, 0, true).apply {
            if (savedInstanceState != null) {
                onRestoreInstanceState(savedInstanceState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putAll(dialog.onSaveInstanceState())
        outState.putInt(ID_KEY, dialogId)
        super.onSaveInstanceState(outState)
    }

    private fun getParentCallback() = when {
        activity is Callback -> activity as Callback
        parentFragment is Callback -> parentFragment as Callback
        targetFragment is Callback -> targetFragment as Callback
        else -> error("TimePickerDialogFragment.Callback not implemented in parent elements")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        getParentCallback().setTime(dialogId, hourOfDay, minute)
    }

    interface Callback {
        fun setTime(id: Int, hourOfDay: Int, minute: Int)
    }
}