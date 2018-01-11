package com.viked.commonandroidmvvm.ui.common.delegate.error

import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.activity.BaseActivity

/**
 * Created by shein on 1/9/2018.
 */
class BaseError(val title: TextWrapper, val errorMessage: TextWrapper, val action: ((BaseActivity) -> Unit)? = null, throwable: Throwable? = null) : Exception(throwable)