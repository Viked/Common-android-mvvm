package com.viked.commonandroidmvvm.text

import android.databinding.ObservableBoolean

class TextValidatorPool(private val textError: ObservableBoolean) {

    private val textFieldValidators = mutableListOf<CompositeTextValidator>()

    private val update = Runnable { update() }

    fun addTextValidator(validator: CompositeTextValidator) = apply {
        validator.updateDelegate = update
        textFieldValidators.add(validator)
    }

    fun update() {
        textError.set(textFieldValidators.firstOrNull { it.hasError() } != null)
    }

    fun force(): Boolean {
        textFieldValidators.forEach { it.force() }
        update()
        return textError.get()
    }

}