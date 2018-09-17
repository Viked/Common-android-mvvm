package com.viked.commonandroidmvvm.security

import android.support.v4.app.Fragment

class SecurityAction(val needPermissionRequest: (Fragment) -> Boolean, val requestPermission: (Fragment, Int) -> Unit)

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