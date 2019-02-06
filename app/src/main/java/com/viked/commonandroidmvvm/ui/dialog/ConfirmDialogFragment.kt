package com.viked.commonandroidmvvm.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.viked.commonandroidmvvm.R

/**
 * Created by yevgeniishein on 7/25/17.
 */
class ConfirmDialogFragment : androidx.fragment.app.DialogFragment() {

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
                .setPositiveButton(R.string.confirm, { dialog, which ->
                    getParentCallback().confirm(dialogId)
                    dialog.dismiss()
                })
                .setNegativeButton(android.R.string.cancel, { dialog, which -> dialog.cancel() }).create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TITLE_KEY, title)
        outState.putInt(ID_KEY, dialogId)
        super.onSaveInstanceState(outState)
    }

    private fun getParentCallback(): Callback {
        return when {
            targetFragment is Callback -> targetFragment as Callback
            parentFragment is Callback -> parentFragment as Callback
            activity is Callback -> activity as Callback
            else -> error("ConfirmDialogFragment.Callback not implemented in parent elements")
        }
    }

    interface Callback {
        fun confirm(id: Int)
    }

}