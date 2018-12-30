package com.viked.commonandroidmvvm.security

import android.support.v4.app.Fragment

open class SecurityAction(private val needRequest: (Fragment) -> Boolean,
                          private val request: (Fragment, Int) -> Unit) : Security {

    override fun needPermissionRequest(fragment: Fragment) = needRequest(fragment)

    override fun requestPermission(fragment: Fragment, id: Int) = request(fragment, id)

}

interface Security {
    fun needPermissionRequest(fragment: Fragment): Boolean
    fun requestPermission(fragment: Fragment, id: Int)
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