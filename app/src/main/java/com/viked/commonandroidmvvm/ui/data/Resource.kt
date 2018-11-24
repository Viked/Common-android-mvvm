package com.viked.commonandroidmvvm.ui.data

import android.support.annotation.StringRes
import com.viked.commonandroidmvvm.text.TextWrapper

class Resource<T>(val data: T? = null, val status: TextWrapper? = null) {
    companion object {
        fun <T> success(data: T) = Resource(data = data)

        fun <T> status(string: TextWrapper) = Resource<T>(status = string)

        fun <T> error(string: TextWrapper) = Resource<T>(status = string)

        fun <T> error(string: String) = Resource<T>(status = TextWrapper(string))

        fun <T> error(@StringRes stringId: Int) = Resource<T>(status = TextWrapper(stringId))

        fun <T, Y> map(old: Resource<T>, mapper: (T) -> Y) = Resource(data = old.data?.run(mapper), status = old.status)
    }
}
