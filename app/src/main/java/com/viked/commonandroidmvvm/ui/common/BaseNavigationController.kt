package com.viked.commonandroidmvvm.ui.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

abstract class BaseNavigationController(val context: Context, val fragmentManager: FragmentManager) {
    abstract val containerId: Int
    //    protected val fragmentManager: FragmentManager = activity.supportFragmentManager
    protected val fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory

    fun getActiveFragment() = fragmentManager.findFragmentById(containerId)

    fun hasActiveFragment() = getActiveFragment() != null

    fun FragmentTransaction.addToBackStackIfCan(tag: String) = apply {
        if (hasActiveFragment()) {
            addToBackStack(tag)
        }
    }

    fun showDialog(dialogClass: String, arguments: Bundle = Bundle()) {
        val dialog = fragmentFactory.instantiate(context.classLoader, dialogClass, arguments) as? DialogFragment
                ?: return
        dialog.show(fragmentManager, dialogClass)
    }
}