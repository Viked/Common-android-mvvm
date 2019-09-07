package com.viked.commonandroidmvvm.security

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

interface ContextHolder {
    fun startActivityForResult(intent: Intent, requestCode: Int)
    val activity: Activity
}

class ActivityContextHolder(override val activity: Activity) : ContextHolder {
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        activity.startActivityForResult(intent, requestCode)
    }
}

class FragmentContextHolder(val fragment: Fragment) : ContextHolder {
    override val activity: Activity
        get() = fragment.requireActivity()

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        fragment.startActivityForResult(intent, requestCode)
    }
}

private fun Activity.toHolder(): ContextHolder = ActivityContextHolder(this)
private fun Fragment.toHolder(): ContextHolder = FragmentContextHolder(this)

abstract class Security {
    abstract fun needPermissionRequest(holder: ContextHolder): Boolean
    abstract fun requestPermission(holder: ContextHolder, id: Int)

    fun needPermissionRequest(activity: Activity): Boolean = needPermissionRequest(activity.toHolder())
    fun requestPermission(activity: Activity, id: Int) {
        requestPermission(activity.toHolder(), id)
    }

    fun needPermissionRequest(fragment: Fragment): Boolean = needPermissionRequest(fragment.toHolder())
    fun requestPermission(fragment: Fragment, id: Int) {
        requestPermission(fragment.toHolder(), id)
    }

    fun checkAndroidPermission(holder: ContextHolder, vararg permissions: String): Boolean =
            !EasyPermissions.hasPermissions(holder.activity, *permissions)

    fun requestAndroidPermission(holder: ContextHolder, id: Int, vararg permissions: String) {
        when (holder) {
            is ActivityContextHolder -> EasyPermissions.requestPermissions(holder.activity, "", id, *permissions)
            is FragmentContextHolder -> EasyPermissions.requestPermissions(holder.fragment, "", id, *permissions)
        }
    }
}