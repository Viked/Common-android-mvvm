package com.viked.commonandroidmvvm.ui.common

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.viked.commonandroidmvvm.ui.activity.BaseActivity

abstract class BaseNavigationController(val activity: BaseActivity) {
    abstract val containerId: Int
    protected val fragmentManager: FragmentManager = activity.supportFragmentManager

    fun hasActiveFragment() = fragmentManager.findFragmentById(containerId) != null

    fun FragmentTransaction.addToBackStackIfCan(tag: String) = apply {
        if (hasActiveFragment()) {
            addToBackStack(tag)
        }
    }
}