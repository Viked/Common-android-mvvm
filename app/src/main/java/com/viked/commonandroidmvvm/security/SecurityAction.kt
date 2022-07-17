package com.viked.commonandroidmvvm.security

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat

abstract class Security {
    abstract fun getContract(): ActivityResultContract<*, *>
    abstract fun requestPermission(context: Context, launcher: ActivityResultLauncher<*>)
    abstract fun needPermissionRequest(context: Context): Boolean

    fun checkAndroidPermission(context: Context, vararg permissions: String): Boolean =
        permissions.any {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
}