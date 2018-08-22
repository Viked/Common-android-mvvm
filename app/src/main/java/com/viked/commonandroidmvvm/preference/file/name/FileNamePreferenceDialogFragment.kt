package com.viked.commonandroidmvvm.preference.file.name

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.viked.commonandroidmvvm.R

/**
 * Created by 1 on 24.03.2016.
 */
class FileNamePreferenceDialogFragment : PreferenceDialogFragmentCompat() {

    companion object {

        private val TEXT_KEY = "text"

        private val presenter = FileNamePreferencePresenter()

    }

    lateinit var fileNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            presenter.fileName = this.getFileNamePreference().fileName
        } else {
            presenter.fileName = (savedInstanceState.getString(TEXT_KEY))
        }
    }

    private fun getFileNamePreference(): FileNamePreference {
        return this.preference as FileNamePreference
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        fileNameEditText = view.findViewById(R.id.etText)
        fileNameEditText.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start..end - 1) {
                if (source[i] == '\\' || source[i] == '|' ||
                        source[i] == '?' || source[i] == '<' ||
                        source[i] == '>' || source[i] == '*' ||
                        source[i] == '/' || source[i] == '"' ||
                        source[i] == ':' || source[i] == '.') {
                    return@InputFilter ""
                }
            }
            null
        })

        fileNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (dialog != null) {
                    val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    val text = s.toString().trim { it <= ' ' }
                    button.isEnabled = !text.isEmpty()
                    presenter.fileName = text
                }
            }
        })
        fileNameEditText.setText(presenter.fileName, TextView.BufferType.EDITABLE)
        fileNameEditText.hint = getFileNamePreference().dialogMessage
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, presenter.fileName)
    }


    override fun onDialogClosed(p0: Boolean) {
        if (p0) {
            val preference = getFileNamePreference()
            if (preference.callChangeListener(presenter.fileName)) {
                preference.fileName = presenter.fileName
            }
        }
    }

}