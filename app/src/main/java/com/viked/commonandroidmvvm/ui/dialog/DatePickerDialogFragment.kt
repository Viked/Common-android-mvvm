package com.viked.commonandroidmvvm.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*


/**
 * Created by Marshall Banana on 02.07.2017.
 */
class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var dialogId: Int = -1

    companion object {
        const val ID_KEY = "id_key"
        const val YEAR_KEY = "year_key"
        const val MONTH_KEY = "month_key"
        const val DAY_KEY = "day_key"
        fun newInstance(id: Int) =
            DatePickerDialogFragment().apply { arguments = Bundle().apply { putInt(ID_KEY, id) } }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogId = savedInstanceState?.getInt(ID_KEY, -1) ?: (arguments?.getInt(ID_KEY, -1)
            ?: -1)

        return DatePickerDialog(requireActivity(), this, year, month, day).apply {
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

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setFragmentResult(getString(dialogId), Bundle().apply {
            putInt(YEAR_KEY, year)
            putInt(MONTH_KEY, month)
            putInt(DAY_KEY, dayOfMonth)
        })
    }
}