package com.viked.commonandroidmvvm.rx

import io.reactivex.Single
import java.net.InetAddress

/**
 * Created by yevgeniishein on 3/25/18.
 */
fun isInternetAvailable() = Single.fromCallable<Boolean> { !InetAddress.getByName("google.com").equals("") }.onErrorReturn { false }