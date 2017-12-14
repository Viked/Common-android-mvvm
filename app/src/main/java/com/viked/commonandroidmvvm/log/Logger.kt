package com.viked.commonandroidmvvm.log

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.viked.commonandroidmvvm.BuildConfig

/**
 * Created by yevgeniishein on 10/9/17.
 */

fun Throwable.log() {
    if (BuildConfig.DEBUG) {
        Log.e("Error", message ?: "", this)
    } else {
        Crashlytics.logException(this)
    }
}