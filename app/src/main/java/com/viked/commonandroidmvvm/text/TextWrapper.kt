package com.viked.commonandroidmvvm.text

import android.content.Context
import android.support.annotation.StringRes

/**
 * Created by yevgeniishein on 10/7/17.
 */
class TextWrapper(@StringRes private val stringId: Int, private val text: String, private val consumer: ((Context) -> String)?) {
    constructor(@StringRes stringId: Int) : this(stringId, "", null)
    constructor(text: String) : this(0, text, null)
    constructor(consumer: (Context) -> String) : this(0, "", consumer)

    fun get(context: Context) = consumer?.invoke(context) ?: if (stringId > 0) context.getString(stringId) else text

}