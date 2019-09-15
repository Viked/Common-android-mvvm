package com.viked.commonandroidmvvm.ui.preference

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.preference.EditTextPreferenceDialogFragmentCompat

class SingleLineEditTextPreferenceDialogFragmentCompat : EditTextPreferenceDialogFragmentCompat() {
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        val editText = view.findViewById<EditText>(android.R.id.edit)
        editText.setSingleLine()
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val dialog = dialog
                        ?: return@setOnEditorActionListener false
                onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                onDismiss(dialog)
                true
            } else false
        }
        editText.requestFocus()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        hideKeyboard()
        super.onClick(dialog, which)
    }

    private fun hideKeyboard() {
        val dialog = dialog ?: return
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val editText = dialog.findViewById<EditText>(android.R.id.edit)
        if (editText != null) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
            editText.clearFocus()
        }
    }
}