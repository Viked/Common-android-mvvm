package com.viked.commonandroidmvvm.ui.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import com.viked.commonandroidmvvm.ui.activity.BaseActivity

abstract class BaseNavigationController(val activity: BaseActivity) {
    abstract val containerId: Int
    protected val fragmentManager: FragmentManager = activity.supportFragmentManager
    protected val fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory

    fun hasActiveFragment() = fragmentManager.findFragmentById(containerId) != null

    fun androidx.fragment.app.FragmentTransaction.addToBackStackIfCan(tag: String) = apply {
        if (hasActiveFragment()) {
            addToBackStack(tag)
        }
    }

    fun showDialog(dialogClass: String, arguments: Bundle = Bundle()) {
        val dialog = fragmentFactory.instantiate(activity.classLoader, dialogClass, arguments) as? DialogFragment
                ?: return
        dialog.setTargetFragment(fragmentManager.findFragmentById(containerId), 0)
        dialog.show(fragmentManager, dialogClass)
    }
}