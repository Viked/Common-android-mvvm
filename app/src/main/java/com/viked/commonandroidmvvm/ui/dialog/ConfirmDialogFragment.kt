package com.viked.commonandroidmvvm.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.viked.commonandroidmvvm.R

/**
 * Created by yevgeniishein on 7/25/17.
 */
class ConfirmDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        private const val TITLE_KEY = "title_key"
        private const val ID_KEY = "id_key"
        val TAG: String = ConfirmDialogFragment::class.java.simpleName

        fun newInstance(title: String, id: Int) =
            ConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                    putInt(ID_KEY, id)
                }
            }

        fun newInstance(@StringRes titleId: Int) =
            ConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ID_KEY, titleId)
                }
            }

    }

    private lateinit var title: String
    private var dialogId: Int = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState == null) {
            dialogId = arguments?.getInt(ID_KEY, -1) ?: -1
            title = arguments?.getString(TITLE_KEY, getString(dialogId)) ?: getString(dialogId)
        } else {
            title = savedInstanceState.getString(TITLE_KEY, "")
            dialogId = savedInstanceState.getInt(ID_KEY, -1)
        }
        return AlertDialog.Builder(activity)
            .setMessage(title)
            .setPositiveButton(R.string.confirm, this)
            .setNegativeButton(android.R.string.cancel, this)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TITLE_KEY, title)
        outState.putInt(ID_KEY, dialogId)
        super.onSaveInstanceState(outState)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                setFragmentResult(getString(dialogId), Bundle())
                dialog.dismiss()
            }
            else -> dialog.cancel()
        }
    }
}