package com.viked.commonandroidmvvm.di

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by Marshall Banana on 09.07.2017.
 */
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)