package com.viked.commonandroidmvvm.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.DatePicker
import java.util.*


/**
 * Created by Marshall Banana on 02.07.2017.
 */
class DatePickerDialogFragment : androidx.fragment.app.DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dialogId: Int = -1

    companion object {
        const val ID_KEY = "id_key"
        fun newInstance(id: Int) = DatePickerDialogFragment().apply { arguments = Bundle().apply { putInt(ID_KEY, id) } }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogId = savedInstanceState?.getInt(ID_KEY, -1) ?: (arguments?.getInt(ID_KEY, -1)
                ?: -1) ?: -1

        return DatePickerDialog(activity, this, year, month, day).apply {
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

    private fun getParentCallback() = when {
        activity is Callback -> activity as Callback
        parentFragment is Callback -> parentFragment as Callback
        targetFragment is Callback -> targetFragment as Callback
        else -> error("DatePickerDialogFragment.Callback not implemented in parent elements")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        getParentCallback().setDate(dialogId, year, month, dayOfMonth)
    }

    interface Callback {
        fun setDate(id: Int, year: Int, month: Int, dayOfMonth: Int)
    }
}