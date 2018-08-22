package com.viked.commonandroidmvvm.preference.file.manager

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.viked.commonandroidmvvm.R


class NewFolderDialogFragment : DialogFragment() {

    private lateinit var text: String

    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        text = if (savedInstanceState != null) {
            savedInstanceState.getString(TITLE_KEY) ?: ""
        } else {
            arguments?.getString(TITLE_KEY) ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_new_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input = view.findViewById(R.id.etText)
        input.setText(text)

        input.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
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
//        input.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                if (dialog != null) {
//                    val button = dialog!!.getButton(AlertDialog.BUTTON_POSITIVE)
//                    button.isEnabled = !s.toString().trim { it <= ' ' }.isEmpty()
//                }
//            }
//        })
//
//        val ab = AlertDialog.Builder(activity).setTitle("")
//                .setView(view).setNegativeButton(android.R.string.cancel, this)
//                .setPositiveButton(android.R.string.ok, this)
//        dialog = ab.create()
//        dialog!!.show()
//        dialog!!.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
//

    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, getReturnIntent(input.text.toString().trim { it <= ' ' }))

    }

    companion object {

        private val TITLE_KEY = "title"
        private val HINT_KEY = "hint"
        private val TEXT_KEY = "text"

        val EDIT_VALUE_KEY = "value"

        @JvmStatic
        fun newInstance(targetFragment: Fragment, requestCode: Int, title: String, hint: String, text: String): NewFolderDialogFragment {
            val ret = NewFolderDialogFragment()
            val args = Bundle()
            args.putString(TITLE_KEY, title)
            args.putString(HINT_KEY, hint)
            args.putString(TEXT_KEY, text)
            ret.arguments = args
            ret.setTargetFragment(targetFragment, requestCode)
            return ret
        }

        private fun getReturnIntent(result: String): Intent {
            val ret = Intent()
            ret.putExtra(EDIT_VALUE_KEY, result)
            return ret
        }
    }
}
