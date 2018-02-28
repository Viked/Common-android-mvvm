package com.viked.commonandroidmvvm.log

import com.crashlytics.android.Crashlytics
import com.viked.commonandroidmvvm.BuildConfig
import timber.log.Timber

/**
 * Created by yevgeniishein on 10/9/17.
 */

fun Throwable.log() {
    Timber.e(this)
    if (!BuildConfig.DEBUG) {
        Crashlytics.logException(this)
    }
}