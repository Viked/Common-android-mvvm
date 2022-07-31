package com.viked.commonandroidmvvm.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class Security {

    abstract fun initWithRegistry(appCompatActivity: AppCompatActivity, callback: Runnable)
    abstract fun initWithRegistry(fragment: Fragment, callback: Runnable)
    abstract fun requestPermission(context: Context)
    abstract fun needPermissionRequest(context: Context): Boolean

    fun executeCallback(callback: Runnable) =
        Handler(Looper.getMainLooper()).postDelayed({ callback.run() }, 300)

    fun checkAndroidPermission(context: Context, vararg permissions: String): Boolean =
        permissions.any {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
}