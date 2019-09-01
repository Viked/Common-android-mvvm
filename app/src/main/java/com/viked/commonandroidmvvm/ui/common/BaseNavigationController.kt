package com.viked.commonandroidmvvm.ui.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

abstract class BaseNavigationController(val context: Context, val fragmentManager: FragmentManager) {
    abstract val containerId: Int
    protected val fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory

    private fun checkState(): Boolean = !fragmentManager.isDestroyed && !fragmentManager.isStateSaved

    private fun getActiveFragment() = fragmentManager.findFragmentById(containerId)

    fun hasActiveFragment() = getActiveFragment() != null

    fun FragmentTransaction.addToBackStackIfCan(tag: String) = apply {
        if (hasActiveFragment()) {
            addToBackStack(tag)
        }
    }

    fun showDialog(dialogClass: String, arguments: Bundle? = null) {
        if (!checkState()) return
        val dialog = fragmentFactory.instantiate(context.classLoader, dialogClass) as? DialogFragment
                ?: return
        dialog.arguments = arguments
        dialog.show(fragmentManager, dialogClass)
    }
}