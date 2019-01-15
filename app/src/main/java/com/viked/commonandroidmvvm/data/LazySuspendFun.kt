package com.viked.commonandroidmvvm.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart.LAZY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class LazySuspendFun<out T>(
        scope: CoroutineScope,
        private val block: suspend () -> T
) {
    private val deferred = scope.async(Dispatchers.Main, LAZY) { block() }

    suspend operator fun invoke() = deferred.await()
}

fun <T> CoroutineScope.lazySuspendFun(block: suspend () -> T) =
        LazySuspendFun(this, block)