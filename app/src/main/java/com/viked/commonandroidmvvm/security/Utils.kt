package com.viked.commonandroidmvvm.security

import pub.devrel.easypermissions.AfterPermissionGranted
import timber.log.Timber
import java.lang.reflect.InvocationTargetException


fun runAnnotatedMethodsAfterPermissionGranted(target: Any, requestCode: Int) {
    var clazz: Class<*>? = target.javaClass
    if (isUsingAndroidAnnotations(target)) {
        clazz = clazz!!.superclass
    }

    while (clazz != null) {
        for (method in clazz.declaredMethods) {
            val ann = method.getAnnotation(AfterPermissionGranted::class.java)
            if (ann != null) {
                // Check for annotated methods with matching request code.
                if (ann.value == requestCode) {
                    // Method must be void so that we can invoke it
                    if (method.parameterTypes.isNotEmpty()) {
                        throw RuntimeException(
                                "Cannot execute method " + method.name + " because it is non-void method and/or has input parameters.")
                    }

                    try {
                        // Make method accessible if private
                        if (!method.isAccessible) {
                            method.isAccessible = true
                        }
                        method.invoke(target)
                    } catch (e: IllegalAccessException) {
                        Timber.e(e, "runDefaultMethod:IllegalAccessException")
                    } catch (e: InvocationTargetException) {
                        Timber.e(e, "runDefaultMethod:InvocationTargetException")
                    }

                }
            }
        }

        clazz = clazz.superclass
    }
}

/**
 * Determine if the project is using the AndroidAnnotations library.
 */
private fun isUsingAndroidAnnotations(`object`: Any): Boolean {
    if (!`object`.javaClass.simpleName.endsWith("_")) {
        return false
    }
    return try {
        val clazz = Class.forName("org.androidannotations.api.view.HasViews")
        clazz.isInstance(`object`)
    } catch (e: ClassNotFoundException) {
        false
    }

}