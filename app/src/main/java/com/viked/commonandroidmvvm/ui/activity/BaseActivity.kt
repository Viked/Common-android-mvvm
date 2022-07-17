package com.viked.commonandroidmvvm.ui.activity

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.viked.commonandroidmvvm.extentions.handleOnBackPressed
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 * Created by yevgeniishein on 10/20/17.
 */
abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if ((supportFragmentManager.isStateSaved || !supportFragmentManager.handleOnBackPressed())) {
            super.onBackPressed()
        }
    }
}