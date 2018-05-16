package com.viked.commonandroidmvvm.text

import android.text.InputFilter
import android.text.Spanned

class DecimalInputFilter(private val dot: String = ".", private val dicimals: Int = 2) : InputFilter {

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val separatorIndex = dest.toString().indexOf(dot)
        if (source != dot) {
            if (separatorIndex in 0..dstart) {
                return if ((dest?.length ?: 0) - separatorIndex > dicimals) "" else null
            }
        } else {
            return if (separatorIndex >= 0 || (dest?.length ?: 0) - dend > dicimals) "" else null
        }
        return null
    }

}