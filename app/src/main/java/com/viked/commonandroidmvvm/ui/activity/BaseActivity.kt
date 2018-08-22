package com.viked.commonandroidmvvm.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.viked.commonandroidmvvm.extentions.handleOnBackPressed
import com.viked.commonandroidmvvm.ui.common.delegate.error.DialogErrorDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.error.ErrorDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.progress.DialogProgressDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.progress.ProgressDelegate
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    lateinit var progressDelegate: ProgressDelegate

    lateinit var errorDelegate: ErrorDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDelegate = DialogProgressDelegate(this)
        errorDelegate = DialogErrorDelegate(this)
    }

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> =
            dispatchingAndroidInjector

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.handleOnBackPressed()) {
            super.onBackPressed()
        }
    }

    var active = false

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onResume() {
        super.onResume()
        active = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}