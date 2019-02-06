package com.viked.commonandroidmvvm.ui.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.viked.commonandroidmvvm.ui.activity.BaseActivity

abstract class BaseNavigationController(val activity: BaseActivity) {
    abstract val containerId: Int
    protected val fragmentManager: androidx.fragment.app.FragmentManager = activity.supportFragmentManager

    fun hasActiveFragment() = fragmentManager.findFragmentById(containerId) != null

    fun androidx.fragment.app.FragmentTransaction.addToBackStackIfCan(tag: String) = apply {
        if (hasActiveFragment()) {
            addToBackStack(tag)
        }
    }

    fun showDialog(dialogClass: String, arguments: Bundle = Bundle()) {
        val dialog = androidx.fragment.app.Fragment.instantiate(activity, dialogClass, arguments) as? androidx.fragment.app.DialogFragment
                ?: return
        dialog.setTargetFragment(fragmentManager.findFragmentById(containerId), 0)
        dialog.show(fragmentManager, dialogClass)
    }
}