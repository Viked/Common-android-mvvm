package com.viked.commonandroidmvvm.log

import timber.log.Timber

/**
 * Created by yevgeniishein on 10/9/17.
 */

fun Throwable.log() {
    Timber.e(this)
}