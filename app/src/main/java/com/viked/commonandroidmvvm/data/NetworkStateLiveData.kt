package com.viked.commonandroidmvvm.data

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData

class NetworkStateLiveData(private val context: Application) : MutableLiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val filter: IntentFilter by lazy {
        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    override fun onActive() {
        context.registerReceiver(networkChangeReceiver, filter)
        updateValue()
    }

    override fun onInactive() {
        context.unregisterReceiver(networkChangeReceiver)
    }

    private fun isNetworkAvailable(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnectedOrConnecting
    }

    private fun updateValue() {
        val newValue = isNetworkAvailable()
        if (newValue != value) {
            postValue(newValue)
        }
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateValue()
        }
    }
}