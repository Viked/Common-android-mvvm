package com.viked.commonandroidmvvm.ui.activity

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.viked.commonandroidmvvm.extentions.handleOnBackPressed
import com.viked.commonandroidmvvm.ui.common.delegate.error.DialogErrorDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.error.ErrorDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.progress.DialogProgressDelegate
import com.viked.commonandroidmvvm.ui.common.delegate.progress.ProgressDelegate
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    lateinit var progressDelegate: ProgressDelegate

    lateinit var errorDelegate: ErrorDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDelegate = DialogProgressDelegate(this)
        errorDelegate = DialogErrorDelegate(this)
    }

    override fun supportFragmentInjector(): DispatchingAndroidInjector<androidx.fragment.app.Fragment> =
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
        Handler().post { EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this) }
    }

}