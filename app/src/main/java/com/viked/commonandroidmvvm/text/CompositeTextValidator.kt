package com.viked.commonandroidmvvm.text

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

class CompositeTextValidator(private val textView: TextView) : TextWatcher {

    private val validators: MutableList<(String) -> TextWrapper?> = mutableListOf()

    var updateDelegate: Runnable? = null

    fun addValidator(validator: (String) -> TextWrapper?) = apply { validators.add(validator) }

    fun hasError() = getError(textView.text.toString()) != null

    fun force() {
        val error = getError(textView.text.toString())?.get(textView.context)
        textView.error = if (!error.isNullOrBlank()) error else null
    }

    private fun getError(text: String) = validators.firstOrNull { it.invoke(text) != null }?.invoke(text)

    override fun afterTextChanged(s: Editable?) {
        val error = getError(s.toString())?.get(textView.context)
        textView.error = if (!error.isNullOrBlank()) error else null
        updateDelegate?.run()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    init {
        textView.addTextChangedListener(this)
    }
}