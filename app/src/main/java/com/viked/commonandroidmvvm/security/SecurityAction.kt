package com.viked.commonandroidmvvm.security

import androidx.fragment.app.Fragment

open class SecurityAction(private val needRequest: (androidx.fragment.app.Fragment) -> Boolean,
                          private val request: (androidx.fragment.app.Fragment, Int) -> Unit) : Security {

    override fun needPermissionRequest(fragment: androidx.fragment.app.Fragment) = needRequest(fragment)

    override fun requestPermission(fragment: androidx.fragment.app.Fragment, id: Int) = request(fragment, id)

}

interface Security {
    fun needPermissionRequest(fragment: androidx.fragment.app.Fragment): Boolean
    fun requestPermission(fragment: androidx.fragment.app.Fragment, id: Int)
    fun action() {}
}

/*
*  if (EasyPermissions.hasPermissions(context, readPermission)) {
            super.loadData()
        } else {
            EasyPermissions.requestPermissions(this, "",
                    PERMISSION_READ, readPermission)
        }



        if (!GoogleSignIn.hasPermissions(
//                    GoogleSignIn.getLastSignedInAccount(getActivity()),
//                    Scope(SCOPE_TEST))) {
//        GoogleSignIn.requestPermissions(this,
//                0,
//                GoogleSignIn.getLastSignedInAccount(getActivity()),
//                Scope(SCOPE_TEST))
*
*
* */