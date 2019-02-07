package com.viked.commonandroidmvvm.text

import android.content.Context
import androidx.annotation.StringRes

/**
 * Created by yevgeniishein on 10/7/17.
 */
class TextWrapper(@StringRes private val stringId: Int, private val text: String, private val consumer: ((Context) -> String)?) {
    constructor() : this(0, "", null)
    constructor(@StringRes stringId: Int) : this(stringId, "", null)
    constructor(text: String) : this(0, text, null)
    constructor(consumer: (Context) -> String) : this(0, "", consumer)

    operator fun get(context: Context) = consumer?.invoke(context)
            ?: if (stringId > 0) context.getString(stringId) else text

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is TextWrapper || other.consumer != null || consumer != null) return false
        return (stringId != 0 && stringId == other.stringId) || (text == other.text)
    }

}