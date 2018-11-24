package com.viked.commonandroidmvvm.work

import androidx.work.Worker
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class WorkerKey(val value: KClass<out Worker>)