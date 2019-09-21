package com.viked.commonandroidmvvm.ui.activity

import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.viked.commonandroidmvvm.extentions.handleOnBackPressed
import com.viked.commonandroidmvvm.extentions.hideKeyboard
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> =
            dispatchingAndroidInjector

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!hideKeyboard()
                && (supportFragmentManager.isStateSaved || !supportFragmentManager.handleOnBackPressed())) {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        Handler().post { EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this) }
    }

}