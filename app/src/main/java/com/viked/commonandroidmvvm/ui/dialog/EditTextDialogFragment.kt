package com.viked.commonandroidmvvm.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.viked.commonandroidmvvm.R

/**
 * Created by yevgeniishein on 2/28/18.
 */
class EditTextDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

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
            .setPositiveButton(android.R.string.ok, this)
            .setNegativeButton(android.R.string.cancel, this)
            .setView(view)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                setFragmentResult(getString(titleId), Bundle().apply {
                    putString(TEXT_KEY, etText?.text?.toString() ?: text)
                })
                dialog?.dismiss()
            }
            else -> dialog?.cancel()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putInt(TITLE_KEY, titleId)
            putString(TEXT_KEY, etText?.text?.toString() ?: text)
        }
        super.onSaveInstanceState(outState)
    }

}