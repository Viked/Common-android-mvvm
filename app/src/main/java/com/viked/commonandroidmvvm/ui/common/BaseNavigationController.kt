package com.viked.commonandroidmvvm.ui.common

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
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

    fun showDialog(dialogClass: String, arguments: Bundle = Bundle()) {
        val dialog = Fragment.instantiate(activity, dialogClass, arguments) as? DialogFragment
                ?: return
        dialog.setTargetFragment(fragmentManager.findFragmentById(containerId), 0)
        dialog.show(fragmentManager, dialogClass)
    }
}