package com.viked.commonandroidmvvm.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.EditText
import com.viked.commonandroidmvvm.R

/**
 * Created by yevgeniishein on 2/28/18.
 */
class EditTextDialogFragment : DialogFragment() {

    private var text = ""
    private var titleId = 0

    private var etText: EditText? = null

    companion object {
        const val TITLE_KEY = "title_key"
        const val TEXT_KEY = "text_key"

        fun newInstance(text: String, titleId: Int) =
                EditTextDialogFragment()
                        .apply {
                            arguments = Bundle()
                                    .apply {
                                        putInt(TITLE_KEY, titleId)
                                        putString(TEXT_KEY, text)
                                    }
                        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState == null) {
            titleId = arguments?.getInt(TITLE_KEY, 0) ?: 0
            text = arguments?.getString(TEXT_KEY, "") ?: ""
        } else {
            titleId = savedInstanceState.getInt(TITLE_KEY, 0)
            text = savedInstanceState.getString(TEXT_KEY, "") ?: ""
        }

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null, false)
        etText = view.findViewById(R.id.etText)
        etText?.setText(text)

        return AlertDialog.Builder(activity)
                .setTitle(titleId)
                .setPositiveButton(android.R.string.ok, { dialog, which ->
                    getParentCallback().confirm(titleId, etText?.text?.toString() ?: text)
                    dialog.dismiss()
                })
                .setNegativeButton(android.R.string.cancel, { dialog, which -> dialog.cancel() })
                .setView(view)
                .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putInt(TITLE_KEY, titleId)
            putString(TEXT_KEY, etText?.text?.toString() ?: text)
        }
        super.onSaveInstanceState(outState)
    }

    private fun getParentCallback(): Callback {
        return when {
            activity is Callback -> activity as Callback
            parentFragment is Callback -> parentFragment as Callback
            targetFragment is Callback -> targetFragment as Callback
            else -> error("EditTextDialogFragment.Callback not implemented in parent elements")
        }
    }

    interface Callback {
        fun confirm(id: Int, text: String)
    }

}